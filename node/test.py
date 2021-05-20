from PIL import Image
from io import BytesIO
import base64
import sys
import pytesseract

pytesseract.pytesseract.tesseract_cmd = r'.\Tesseract-OCR\tesseract'


image_string = base64.b64decode(sys.argv[1])
img = Image.open(BytesIO(image_string))

print(pytesseract.image_to_string(img))