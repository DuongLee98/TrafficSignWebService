import tensorflow as tf
import tensorflow.keras.models
import matplotlib.image as mimg
import numpy as np
import cv2

classNames = {
    1: 'Cam dung',
    2: 'Duoc phep re trai',
    3: 'Duoc phep re phai',
    4: 'Cam re trai',
    5: 'Cam re phai',
    6: 'Cam di nguoc chieu',
    7: 'Gioi han toc do(40km/h)',
    8: 'Toc do toi thieu (30km/h)',
    9: 'Khong di 30km/h',
    10: 'Sign10'}


saved_model = tf.keras.models.load_model("test.h5")

img = []
image = cv2.imread("00364_00000.ppm")
b, g, r = cv2.split(image)  # get b, g, r
image = cv2.merge([r, g, b])
image = cv2.resize(image, dsize=(32, 32))
img.append(image)
img = np.asarray(img, dtype=float)
res = saved_model.predict(img)
sign = np.argmax(res)
print(classNames[int(sign)])
