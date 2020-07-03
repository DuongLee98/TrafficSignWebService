import cv2
import numpy as np
import tensorflow as tf

physical_devices = tf.config.experimental.list_physical_devices('GPU')
assert len(physical_devices) > 0, "Not enough GPU hardware devices available"
tf.config.experimental.set_memory_growth(physical_devices[0], True)

classNames = {
    1: 'Dung lai',
    2: 'Duoc phep re trai',
    3: 'Duoc phep re phai',
    4: 'Cam re trai',
    5: 'Cam re phai',
    6: 'Cam di nguoc chieu',
    7: 'Gioi han toc do(40km/h)',
    8: 'Toc do toi thieu (30km/h)',
    9: 'Khong di 30km/h',
    10: 'Sign10'}

self.saved_model = tf.keras.models.load_model("mynet.h5")


def listDetection(self, img, link=None):
    if link is not None:
        imgr = cv2.imread(link, cv2.IMREAD_COLOR)
    else:

        imgr = img

    # imgr = cv2.cvtColor(imgr, cv2.COLOR_BGR2RGB)
    arr = []
    bounding = []
    gray = cv2.cvtColor(imgr, cv2.COLOR_BGR2GRAY)
    gray_blurred = cv2.blur(gray, (3, 3))
    detected_circles = cv2.HoughCircles(gray_blurred, cv2.HOUGH_GRADIENT, 2, 50, param1=80, param2=160, minRadius=20,
                                        maxRadius=150)
    imgr = cv2.cvtColor(imgr, cv2.COLOR_BGR2RGB)
    if detected_circles is not None:
        detected_circles = np.uint16(np.around(detected_circles))
        for pt in detected_circles[0, :]:
            a, b, r = int(pt[0]), int(pt[1]), int(pt[2])

            y = int(a - r)
            x = int(b - r)

            if x >= 0 and y >= 0 and r >= 0:
                w = 2*r
                h = 2*r
                if x+w < imgr.shape[0] and x+h < imgr.shape[1]:
                    imgtmp = imgr[x:x+w+1, y:y+h+1, :]

                    imgtmp = cv2.resize(imgtmp, dsize=(32, 32))
                    arr.append(imgtmp)
                    bounding.append((x, y, w, h))

    return np.array(arr), np.array(bounding)
