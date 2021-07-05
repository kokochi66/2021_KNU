import os
from PIL import Image, ImageFilter

path = 'D:/image/arranged_image/'
os.chdir(path)
files = os.listdir(path)



filter_list = [ImageFilter.BLUR, ImageFilter.CONTOUR, ImageFilter.DETAIL,
                ImageFilter.EDGE_ENHANCE, ImageFilter.EDGE_ENHANCE_MORE, ImageFilter.EMBOSS, ImageFilter.FIND_EDGES, 
                ImageFilter.SHARPEN, ImageFilter.SMOOTH, ImageFilter.SMOOTH_MORE]

for file in files:
    if '.jpg' in file:
        name = file.replace(".jpg", "")
        img = Image.open(file)
        for i in range(len(filter_list)):
            try:
                filter_img = img.filter(filter_list[i])
                filter_img.save("D:/image/augmented_image/{}-{}.jpg".format(name, i+1))
            except:
                img = img.convert('RGB')
                filter_img = img.filter(filter_list[i])
                filter_img.save("D:/image/augmented_image/{}-{}.jpg".format(name, i+1))


