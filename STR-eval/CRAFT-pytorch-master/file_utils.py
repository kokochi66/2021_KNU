# -*- coding: utf-8 -*-
import os
import numpy as np
import cv2
import json
import imgproc
from PIL import Image, ImageDraw 

data = {}

# change working directory
cur_path = os.path.dirname(os.path.abspath(__file__))
os.chdir(cur_path)


# borrowed from https://github.com/lengstrom/fast-style-transfer/blob/master/src/utils.py
def get_files(img_dir):
    imgs, masks, xmls = list_files(img_dir)
    return imgs, masks, xmls

def list_files(in_path):
    img_files = []
    mask_files = []
    gt_files = []
    for (dirpath, dirnames, filenames) in os.walk(in_path):
        for file in filenames:
            filename, ext = os.path.splitext(file)
            ext = str.lower(ext)
            if ext == '.jpg' or ext == '.jpeg' or ext == '.gif' or ext == '.png' or ext == '.pgm':
                img_files.append(os.path.join(dirpath, file))
            elif ext == '.bmp':
                mask_files.append(os.path.join(dirpath, file))
            elif ext == '.xml' or ext == '.gt' or ext == '.txt':
                gt_files.append(os.path.join(dirpath, file))
            elif ext == '.zip':
                continue
    # img_files.sort()
    # mask_files.sort()
    # gt_files.sort()
    return img_files, mask_files, gt_files

def saveResult(img_file, img, boxes, dirname='./result/', verticals=None, texts=None):
        """ save text detection result one by one
        Args:
            img_file (str): image file name
            img (array): raw image context
            boxes (array): array of result file
                Shape: [num_detections, 4] for BB output / [num_detections, 4] for QUAD output
        Return:
            None
        """
        img = np.array(img)

        # make result file list
        filename, file_ext = os.path.splitext(os.path.basename(img_file))

        # result directory
        #res_file = dirname + "res_" + filename + '.txt'
        #res_img_file = dirname + "res_" + filename + '.jpg'

        if not os.path.isdir(dirname):
            os.mkdir(dirname)
            
        data[filename] = []

        for i, box in enumerate(boxes):
            poly = np.array(box).astype(np.int32).reshape((-1))
                
            for px, py in zip(poly[::2], poly[1::2]):
                data[filename].append({
                "x": str(px),
                "y": str(py)
            })
            
            with open('./result.json', 'w') as outfile:
                json.dump(data, outfile, indent=4)
                
    # image crop
    
        im = Image.open(img_file).convert("RGBA")
        imArray = np.asarray(im)
    
        for num in range (0, len(boxes)) :
    
        # create mask
            polygon = boxes[num]
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
            croppedIm.save('../img/' + filename + '-' + str(num) + ".png")

                #strResult = ','.join([str(p) for p in poly]) + '\r\n'
                #f.write(strResult)

                #poly = poly.reshape(-1, 2)
                #cv2.polylines(img, [poly.reshape((-1, 1, 2))], True, color=(0, 0, 255), thickness=2)
                #ptColor = (0, 255, 255)
                #if verticals is not None:
                #    if verticals[i]:
                #        ptColor = (255, 0, 0)


                #if texts is not None:
                    #font = cv2.FONT_HERSHEY_SIMPLEX
                    #font_scale = 0.5
                    #cv2.putText(img, "{}".format(texts[i]), (poly[0][0]+1, poly[0][1]+1), font, font_scale, (0, 0, 0), thickness=1)
                    #cv2.putText(img, "{}".format(texts[i]), tuple(poly[0]), font, font_scale, (0, 255, 255), thickness=1)

        # Save result image
        #cv2.imwrite(res_img_file, img) 

