import flask
from flask import request, jsonify
import base64
import numpy as np
import cv2

app = flask.Flask(__name__)
app.config["DEBUG"] = True

listUser = []
listLat = []
listLon = []
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
    listLat.append(lat)
    listLon.append(lon)
    return jsonify({'result': True})

@app.route('/api/v1/getall', methods=['GET'])
def api_getdiadiem():
    content = request.get_json()

    print(listUser)
    print(listLat)
    print(listLon)
    return jsonify({'listUser': listUser, 'listLat': listLat, 'listLon':listLon})

app.run(host='0.0.0.0', port=5000)
