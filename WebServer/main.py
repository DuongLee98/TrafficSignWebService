import flask
from flask import request, jsonify
import base64
import io
import numpy as np
from PIL import Image
import cv2

app = flask.Flask(__name__)
app.config["DEBUG"] = True


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


app.run(host='0.0.0.0', port=5000)
