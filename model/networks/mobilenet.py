from keras.applications.mobilenet import MobileNet
from keras.layers import GlobalAveragePooling2D, Dense, Dropout, Conv2D
from keras.models import Model, Sequential

name = 'mobilenet'

input_shape = (128, 128, 3)
batch_size = 32
color_mode = 'rgb'

def build_model():
	base_model = MobileNet(input_shape=input_shape, include_top=False)
	x = GlobalAveragePooling2D(name='avg_pool')(base_model.output)
	x = Dropout(1e-3, name='dropout')(x)
	# x = layers.Activation('softmax', name='act_softmax')(x)
	# x = Conv2D(121, (1, 1),padding='same', name='conv_preds')(x)
	x = Dense(14, activation='softmax', name='predictions')(x)
	return Model(inputs=[base_model.input], outputs=[x])

# model = build_model()
# print(model.summary())
