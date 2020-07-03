import os
from imutils import paths
import matplotlib.image as mimg
import matplotlib.pyplot as plt
import numpy as np
import tensorflow as tf
import cv2
from sklearn.utils import shuffle
from sklearn.preprocessing import LabelBinarizer
from tensorflow.keras.models import Model
from sklearn.model_selection import train_test_split
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import BatchNormalization
from tensorflow.keras.layers import AveragePooling2D
from tensorflow.keras.layers import MaxPooling2D
from tensorflow.keras.layers import Conv2D
from tensorflow.keras.layers import Activation
from tensorflow.keras.layers import Dropout
from tensorflow.keras.layers import Flatten
from tensorflow.keras.layers import Input
from tensorflow.keras.layers import Dense
from tensorflow.keras.layers import concatenate
from tensorflow.keras.preprocessing.image import ImageDataGenerator
from tensorflow.keras.optimizers import SGD

physical_devices = tf.config.experimental.list_physical_devices('GPU')
assert len(physical_devices) > 0, "Not enough GPU hardware devices available"
tf.config.experimental.set_memory_growth(physical_devices[0], True)

features = []
labels = []
root = "./data/"
files = list(paths.list_files(root))

for image in files:
    path = image.split("/")
    try:

        img = cv2.imread(image)
        b, g, r = cv2.split(img)  # get b, g, r
        img = cv2.merge([r, g, b])
        img = cv2.resize(img, dsize=(32, 32))
        features.append(img)
        labels.append(int(path[2]))
    except:
        print("cant read img")

features = np.array(features)

labels = np.array(labels)
# print(len(labels))

# plt.imshow(features[2100].astype("int"))
# print(labels[2100])
# plt.show()
features, labels = shuffle(features, labels)

features = features / 255.0
(trainX, testX, trainY, testY) = train_test_split(features, labels, test_size=0.1, random_state=42)

lb = LabelBinarizer()
trainY = lb.fit_transform(trainY)
testY = lb.fit_transform(testY)

model = Sequential()

width = 32
height = 32
classes = 10

shape = (width, height, 3)

model.add(Conv2D(32, (3, 3), padding="same", input_shape=shape))
model.add(Activation("relu"))
model.add(BatchNormalization())
model.add(Conv2D(32, (3, 3), padding="same"))
model.add(Activation("relu"))
model.add(BatchNormalization())
model.add(MaxPooling2D(pool_size=(2, 2)))
model.add(Conv2D(64, (3, 3), padding="same"))
model.add(Activation("relu"))
model.add(BatchNormalization())
model.add(Conv2D(64, (3, 3), padding="same"))
model.add(Activation("relu"))
model.add(BatchNormalization())
model.add(MaxPooling2D(pool_size=(2, 2)))
model.add(Flatten())
model.add(Dense(512))
model.add(Activation("relu"))
model.add(BatchNormalization())
model.add(Dense(classes))
model.add(Activation("softmax"))

# model.add(Conv2D(32, (3, 3), input_shape=shape, padding="SAME",
#                  activation='relu'))
# model.add(MaxPooling2D(pool_size=(2, 2), padding="SAME"))
# model.add(Conv2D(64, (3, 3), padding="SAME", activation='relu'))
# model.add(MaxPooling2D(pool_size=(2, 2), padding="SAME"))
# model.add(Dropout(0.25))
# model.add(Conv2D(128, (3, 3), padding="SAME", activation='relu'))
# model.add(MaxPooling2D(pool_size=(2, 2), padding="SAME"))
# model.add(Dropout(0.25))
# model.add(Flatten())
# model.add(Dense(units=512, activation='relu'))
# model.add(Dropout(0.5))
# model.add(Dense(units=classes, activation='softmax'))

# print(model.summary())
aug = ImageDataGenerator(rotation_range=0.15, zoom_range=0.15, width_shift_range=0.12, height_shift_range=0.12,
                         horizontal_flip=True, vertical_flip=True, brightness_range=[0.5, 2.0])
learning_rate = 0.001
epochs = 100
batch_size = 32

opt = SGD(learning_rate=learning_rate, momentum=0.9)
model.compile(optimizer=opt, loss="categorical_crossentropy", metrics=["accuracy"])

print("Start training")
H = model.fit_generator(aug.flow(trainX, trainY, batch_size=batch_size), validation_data=(testX, testY),
                        steps_per_epoch=trainX.shape[0] / batch_size, epochs=epochs, verbose=1)

model.save("khanhnet.h5")
