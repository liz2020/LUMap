import tensorflow as tf

from tensorflow.python.keras import backend as K
from tensorflow.python.keras.utils import CustomObjectScope

# In keras 2.2.4, relu6 is removed from keras_applications.mobilenet
# Note that relu6 is just relu with max value euqal to 6, thus fix this issue by define a function.
# besides, instead of import from keras, import from python.keras.
# https://github.com/tensorflow/tensorflow/issues/17191
def relu6(x):
  return K.relu(x, max_value=6)

with CustomObjectScope({'relu6': relu6}):
    converter = tf.lite.TFLiteConverter.from_keras_model_file("weights-improvement-acc0.9845-loss0.0970-epoch0007.hdf5")
    tflite_model = converter.convert()
    open("converted_model.tflite", "wb").write(tflite_model)