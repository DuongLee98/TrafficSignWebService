import flask
from flask import request, jsonify
import base64
import numpy as np
import cv2
import tensorflow as tf

# physical_devices = tf.config.experimental.list_physical_devices('GPU')
# assert len(physical_devices) > 0, "Not enough GPU hardware devices available"
# tf.config.experimental.set_memory_growth(physical_devices[0], True)

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

saved_model = tf.keras.models.load_model("mynet.h5")


def listDetection(img, link=None):
    if link is not None:
        imgr = cv2.imread(link, cv2.IMREAD_COLOR)
    else:

        imgr = img
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
                w = 2 * r
                h = 2 * r
                if x + w < imgr.shape[0] and x + h < imgr.shape[1]:
                    imgtmp = imgr[x:x + w + 1, y:y + h + 1, :]

                    imgtmp = cv2.resize(imgtmp, dsize=(32, 32))
                    arr.append(imgtmp)
                    bounding.append((x, y, w, h))

    return np.array(arr), np.array(bounding)


app = flask.Flask(__name__)
app.config["DEBUG"] = True

listUser = []
listLat = ""
listLon = ""
print(listUser)
print(listLat)
print(listLon)


@app.route("/")
def home():
    return "meo meo"


@app.route('/api/v1/trafficsign', methods=['POST'])
def api_trafficsign():
    content = request.get_json()
    # print(content)
    imgbase64 = content['nameValuePairs']['img']
    print(type(imgbase64))
    base64_decoded = base64.b64decode(imgbase64)
    image = np.frombuffer(base64_decoded, np.uint8)
    image_np = cv2.imdecode(image, cv2.IMREAD_COLOR)
    print(image_np.shape)
    limg, bounding = listDetection(image_np)
    print(limg.shape)
    print(bounding.shape)
    res = saved_model.predict(limg)
    sign = np.argmax(res, axis=1)
    pred = np.amax(res, axis=1)
    print(sign)
    print(pred)
    for i in range(len(sign)):
        if pred[i] > 0.8:
            b = bounding[i]
            image_np = cv2.rectangle(image_np, (b[1], b[0]), (b[1] + b[3], b[0] + b[2]), (36, 255, 12), 1)
            cv2.putText(image_np, classNames[sign[i]], (b[1], b[0]), cv2.FONT_HERSHEY_SIMPLEX, 0.75, (36, 255, 12), 2)

    image_np = cv2.cvtColor(image_np, cv2.COLOR_BGR2RGB)
    retval, buffer = cv2.imencode('.jpg', image_np)
    s = base64.b64encode(buffer).decode()
    # print(s)
    # print(jsonify({'img': s, 'info': [{'sign': "TurnLeft", 'img': 'icon image', 'Des': 'ok babe'}]}).get_data(as_text=True))
    return jsonify({'img': s, 'info': [{'sign': "TurnLeft", 'img': 'icon image', 'Des': 'ok babe'}]})


@app.route('/api/v1/postdiadiem', methods=['POST'])
def api_postdiadiem():
    content = request.get_json()
    user = content['nameValuePairs']['user']
    lat = content['nameValuePairs']['lat']
    lon = content['nameValuePairs']['lon']
    print(user)
    print(lat)
    print(lon)
    listUser.append(user)
    global listLat, listLon
    listLat += lat+','
    listLon += lon+','

    return jsonify({'result': True})


@app.route('/api/v1/getall', methods=['GET'])
def api_getdiadiem():
    content = request.get_json()

    print(listUser)
    print(listLat)
    print(listLon)

    return jsonify({'listUser': listUser, 'listLat': str(listLat), 'listLon': str(listLon)})


app.run(host='0.0.0.0', port=5000)
