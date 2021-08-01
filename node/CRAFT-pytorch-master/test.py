"""  
Copyright (c) 2019-present NAVER Corp.
MIT License
"""

# -*- coding: utf-8 -*-
import sys
import os
import time
import argparse

import torch
import torch.nn as nn
import torch.backends.cudnn as cudnn
from torch.autograd import Variable

from PIL import Image, ImageDraw 

import cv2
from skimage import io
import numpy as np

import craft_utils
import imgproc
import file_utils
import json
import zipfile
import base64
import json

from io import BytesIO

from craft import CRAFT

from collections import OrderedDict
def copyStateDict(state_dict):
    if list(state_dict.keys())[0].startswith("module"):
        start_idx = 1
    else:
        start_idx = 0
    new_state_dict = OrderedDict()
    for k, v in state_dict.items():
        name = ".".join(k.split(".")[start_idx:])
        new_state_dict[name] = v
    return new_state_dict

def str2bool(v):
    return v.lower() in ("yes", "y", "true", "t", "1")
    
# change working directory
cur_path = os.path.dirname(os.path.abspath(__file__))
os.chdir(cur_path)

parser = argparse.ArgumentParser(description='CRAFT Text Detection')
parser.add_argument('--trained_model', default="./craft_mlt_25k.pth", type=str, help='pretrained model')
parser.add_argument('--text_threshold', default=0.7, type=float, help='text confidence threshold')
parser.add_argument('--low_text', default=0.4, type=float, help='text low-bound score')
parser.add_argument('--link_threshold', default=0.4, type=float, help='link confidence threshold')
parser.add_argument('--cuda', default=True, type=str2bool, help='Use cuda for inference')
parser.add_argument('--canvas_size', default=1280, type=int, help='image size for inference')
parser.add_argument('--mag_ratio', default=1.5, type=float, help='image magnification ratio')
parser.add_argument('--poly', default=False, action='store_true', help='enable polygon type')
parser.add_argument('--show_time', default=False, action='store_true', help='show processing time')
parser.add_argument('--test_folder', default='../sample2/', type=str, help='folder path to input images')
parser.add_argument('--refine', default=False, action='store_true', help='enable link refiner')
parser.add_argument('--refiner_model', default='weights/craft_refiner_CTW1500.pth', type=str, help='pretrained refiner model')

args = parser.parse_args()


""" For test images in a folder """
image_list, _, _ = file_utils.get_files(args.test_folder)

result_folder = '../img/result/'
if not os.path.isdir(result_folder):
    os.mkdir(result_folder)

def test_net(net, image, text_threshold, link_threshold, low_text, cuda, poly, refine_net=None):
    t0 = time.time()

    # resize
    img_resized, target_ratio, size_heatmap = imgproc.resize_aspect_ratio(image, args.canvas_size, interpolation=cv2.INTER_LINEAR, mag_ratio=args.mag_ratio)
    ratio_h = ratio_w = 1 / target_ratio

    # preprocessing
    x = imgproc.normalizeMeanVariance(img_resized)
    x = torch.from_numpy(x).permute(2, 0, 1)    # [h, w, c] to [c, h, w]
    x = Variable(x.unsqueeze(0))                # [c, h, w] to [b, c, h, w]
    if cuda:
        x = x.cuda()

    # forward pass
    with torch.no_grad():
        y, feature = net(x)

    # make score and link map
    score_text = y[0,:,:,0].cpu().data.numpy()
    score_link = y[0,:,:,1].cpu().data.numpy()

    # refine link
    if refine_net is not None:
        with torch.no_grad():
            y_refiner = refine_net(y, feature)
        score_link = y_refiner[0,:,:,0].cpu().data.numpy()

    t0 = time.time() - t0
    t1 = time.time()

    # Post-processing
    boxes, polys = craft_utils.getDetBoxes(score_text, score_link, text_threshold, link_threshold, low_text, poly)

    # coordinate adjustment
    boxes = craft_utils.adjustResultCoordinates(boxes, ratio_w, ratio_h)
    polys = craft_utils.adjustResultCoordinates(polys, ratio_w, ratio_h)
    for k in range(len(polys)):
        if polys[k] is None: polys[k] = boxes[k]

    t1 = time.time() - t1

    # render results (optional)
    render_img = score_text.copy()
    render_img = np.hstack((render_img, score_link))
    ret_score_text = imgproc.cvt2HeatmapImg(render_img)

    if args.show_time : print("\ninfer/postproc time : {:.3f}/{:.3f}".format(t0, t1))

    return boxes, polys, ret_score_text



if __name__ == '__main__':
    # load net
    net = CRAFT()     # initialize

    #print('Loading weights from checkpoint (' + args.trained_model + ')')
    if args.cuda:
        net.load_state_dict(copyStateDict(torch.load(args.trained_model)))
    else:
        net.load_state_dict(copyStateDict(torch.load(args.trained_model, map_location='cpu')))

    if args.cuda:
        net = net.cuda()
        net = torch.nn.DataParallel(net)
        cudnn.benchmark = False

    net.eval()

    # LinkRefiner
    refine_net = None
    if args.refine:
        from refinenet import RefineNet
        refine_net = RefineNet()
        print('Loading weights of refiner from checkpoint (' + args.refiner_model + ')')
        if args.cuda:
            refine_net.load_state_dict(copyStateDict(torch.load(args.refiner_model)))
            refine_net = refine_net.cuda()
            refine_net = torch.nn.DataParallel(refine_net)
        else:
            refine_net.load_state_dict(copyStateDict(torch.load(args.refiner_model, map_location='cpu')))

        refine_net.eval()
        args.poly = True

    t = time.time()

    jsondata = {
        "res_img" : "",
        "res_cropped" : []
    }


    # nodejs에서 전달받은 base64 문자열 파일을 받는다.
    node_server_data = input()
    im2_bytes = base64.b64decode(node_server_data)
    im2_file = BytesIO(im2_bytes)
    im2 = Image.open(im2_file).convert("RGB")
    im2_data = np.asarray(im2)
    # 해당 파일을 이미지로 변환해주고, numpy객체로 변환시켜서, 글자를 읽어내도록 한다.

    bboxes, polys, score_text = test_net(net, im2_data, args.text_threshold, args.link_threshold, args.low_text, args.cuda, args.poly, refine_net)
    im2_data = im2_data.copy()

    im2_res = file_utils.saveResult("result", im2_data, polys, dirname=result_folder)

    im2_fr = Image.fromarray(im2_res)
    im2_buffer = BytesIO()
    im2_fr.save(im2_buffer, format="PNG")
    im2_resImg_base64 = base64.b64encode(im2_buffer.getvalue()).decode("utf-8")

    jsondata['res_img'] = im2_resImg_base64

    # print(jsondata)

    # 읽어낸 값을 적용하여 저장한다 (해당 부분은 저장이 아닌, 서버로 다시 base64파일로 돌려주도록 해야함(JSON객체에 담아서))


    
    im2_proc = Image.open(im2_file).convert("RGBA")
    imArray = np.asarray(im2_proc)
    
    for num in range (0, len(polys)) :
    
        # create mask
        polygon = polys[num]
        maskIm = Image.new('L', (imArray.shape[1], imArray.shape[0]), 0)
        ImageDraw.Draw(maskIm).polygon(polygon, outline=1, fill=1)
        mask = np.array(maskIm)
        
        # assemble new image (uint8: 0-255)
        newImArray = np.empty(imArray.shape,dtype='uint8')
        
        # colors (three first columns, RGB)
        newImArray[:,:,:3] = imArray[:,:,:3]
        
        # transparency (4th column)
        newImArray[:,:,3] = mask*255
        
        # back to Image from numpy
        newIm = Image.fromarray(newImArray, "RGBA")

        # save copped image
        poly_x=[]
        poly_y=[]

        for pxy in polygon:
            poly_x.append(int(pxy[0]))
            poly_y.append(int(pxy[1]))

        croppedIm = newIm.crop((min(poly_x), min(poly_y), max(poly_x), max(poly_y)))
        cropped_buffer = BytesIO()
        croppedIm.save(cropped_buffer, format="PNG")
        jsondata['res_cropped'].append(base64.b64encode(cropped_buffer.getvalue()).decode("utf-8"))
        croppedIm.save("../img/cropped/" + str(num) + ".png")
    print(jsondata)
