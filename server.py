from flask import Flask, abort, request, jsonify
import json
import cv2
import numpy as np
import base64
from PIL import Image
import io
import os

app = Flask(__name__)


@app.route('/getparams', methods=['POST']) 
def getparams():
    if not request.json:
        abort(400)

    req = request.get_json(force=True)
    image = req['image']
    image_name = req['image_name']

    binary_data = base64.b64decode(image)

    with open(image_name, 'wb') as f_output:
        f_output.write(binary_data)

    image = cv2.imread(image_name)
    
    width = image.shape[1]
    height = image.shape[0]
    
    if(height > 1000 or width> 1000 ):
        image = cv2.resize(image,(width//10, height//10))

    area, width = getArea(image, image.shape[1], image.shape[0])

    os.remove(image_name)
    return jsonify(area=area, width=width)


def ellipsity(contour): 
    (x, y), (lmax, lmin), angle = cv2.fitEllipse(contour)
    return lmax/lmin

def getPixelValue(w,h):
    #Importing reference image
    ref = cv2.imread('ref.jpeg',0)
    ref = cv2.resize(ref,(w,h))

    #Extracting the countours in the reference object to determine the pixel area of the reference object in the reference image
    _, refcontours, refhierarchy = cv2.findContours(ref, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)

    pixel_count = 0
    
    #Getting the outer contour surrounding the coin in the image i.e getting the contour with max area.
    #The contour associated with entire image should be neglected. 
    #Hence filtering it using contour area < 95 percent of image area.
    
    pixel_count = cv2.contourArea(refcontours[0])
    """ for i in range(len(refcontours)):
        refcontourArea = cv2.contourArea(refcontours[i])
        if(refcontourArea > pixel_count) and (refcontourArea < 0.95*h*w):
            pixel_count = cv2.contourArea(refcontours[i]) """

    #3.77717 sq.cm is the actual area of the coin in the reference image.
    pixel_value = 3.77717/pixel_count
    
    return pixel_value

def getArea(img, width, height):
    
    #Get pixel value i.e contour area of the coin in the reference image.
    pixel_value = getPixelValue(width,height)

    #Grayscaling the image
    image = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    
    #Blurring the image to remove noise
    image = cv2.medianBlur(image,5)
    
    #Binarization using thresholding
    image = cv2.adaptiveThreshold(image,255, cv2.ADAPTIVE_THRESH_MEAN_C, cv2.THRESH_BINARY,11,2)
    
    #Morphological operations - Opening and closing
    kernel = np.ones((7,7),np.uint8)
    
    image = cv2.erode(image, kernel, iterations=1)
    image = cv2.dilate(image, kernel, iterations=1)
    image = cv2.dilate(image, kernel, iterations=1)
    image = cv2.erode(image, kernel, iterations=1)
  
    #Finding Contours
    _, contours, hierarchy = cv2.findContours(image, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
    
    #Defining a hull
    hull = []
    
    for i in range(len(contours)):
        hull.append(cv2.convexHull(contours[i], True))
    
    #Defining a list for storing all the areas calculated
    areas = []

    #Defining a list for storing all the widths calculated
    wid=[]
        
    #Determining area of the identified contours
    for i in range(len(hull)):
        carea = cv2.contourArea(hull[i])
        
        
        #For fitEllipse function, the no. of points in the contour should be greater than 5.
        #Any contour which has a parent contour i.e if it lies inside of the parent contour should be neglected. 
        #Thus only those contours should be selected whose parent is null.
        #Area constraint ensures that the considered contours have area between 200 and 70 percent of image's area.
        
        if(len(contours[i]) > 5):
            if (hierarchy[0][i][3] == -1)  and ( carea >= 200) and (carea < 0.7*height*width) and (ellipsity(hull[i]) >= 0.2):    
                a = carea*pixel_value
                x,y,w,h = cv2.boundingRect(contours[i])
                areas.append(a)
                wid.append(w)

    if(len(areas)==0):
        return 0
    else:
        m = max(areas)
        i =  areas.index(m)       
        return m, wid[i]

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True, threaded=True)

