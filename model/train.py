from keras.callbacks import TensorBoard, ModelCheckpoint
from keras.optimizers import SGD, Adam, RMSprop
import fire
import glob
import importlib
import os
import re
import sys
from keras.preprocessing.image import ImageDataGenerator

class Classifier(object):
	def __init__(self, network_config, path):
		self.config = network_config
		self.model = self.config.build_model()
		if path != None:
			self.model.load_weights(path)
		print(self.model.summary())

	def train(self, train_datagen, validation_datagen, optimizer, lr, decay, description, initial_epoch):
		optimizer_list = {
		'sgd': SGD(lr=lr, momentum=0.9, decay=decay),
		'adam': Adam(lr=lr, decay=decay),
		'rmsprop': RMSprop(lr=lr, decay=decay)}

		select_optimizer = optimizer_list[optimizer]


		self.model.compile(optimizer = optimizer, metrics=['acc'], loss='categorical_crossentropy')
		log_folder = os.path.join('log', '{}_{}_{}_{}_{}'.format(self.config.name, optimizer, lr, decay, description))
		tensorboard = TensorBoard(log_dir=log_folder, histogram_freq=0, write_graph=True, write_images=True)
		checkpoint_folder = os.path.join('checkpoints', '{}_{}_{}_{}_{}'.format(self.config.name, optimizer, lr, decay, description))
		if not os.path.exists(checkpoint_folder):
			os.makedirs(checkpoint_folder)
		filepath = os.path.join(
			checkpoint_folder, 'weights-improvement-acc{acc:.4f}-loss{loss:.4f}-epoch{epoch:04d}.hdf5')
		checkpoint = ModelCheckpoint(filepath, monitor='loss', verbose=2, save_best_only=True, mode='min')

		self.model.fit_generator(
			train_datagen,
			validation_data=validation_datagen,
			epochs=100,
                        # steps_per_epoch=10575/self.config.batch_size,
			callbacks=[checkpoint, tensorboard],
			initial_epoch=initial_epoch
			)


def main(network=None, GPU='', optimizer='sgd', lr=0.01, decay=0, 
	dataset_root = 'data', description='',
	model_path = None ):
	
	os.environ['CUDA_VISIBLE_DEVICES'] = str(GPU)

	if network == None:
		sys.exit('Please specify the network.')
		
	config = importlib.import_module("networks.{}".format(network))

	if hasattr(config, 'preprocess_input'):
		preprocess_func = config.preprocess_input
	else:
		preprocess_func = None

	image_datagen = ImageDataGenerator(preprocessing_function=preprocess_func,validation_split=0.2)

	train_generator = image_datagen.flow_from_directory(
		dataset_root,
		subset="training",
		shuffle=True,
		batch_size=config.batch_size,
		color_mode=config.color_mode,
		class_mode='categorical',
		seed=7,
		target_size=(config.input_shape[0], config.input_shape[1])
		)

	test_generator = image_datagen.flow_from_directory(
		dataset_root,
		subset="validation",
		shuffle=True,
		batch_size=config.batch_size,
		color_mode=config.color_mode,
		class_mode='categorical',
		seed=7,
		target_size=(config.input_shape[0], config.input_shape[1])
		)

	if model_path == None:
		path = None
		initial_epoch = 0
	else:
		path = model_path
		initial_epoch = int(re.findall(r'epoch(\d+)', path)[0])

	classifier = Classifier(config, path)
	classifier.train(train_generator, test_generator, optimizer, lr, decay, description, initial_epoch)

if __name__ == '__main__':
	fire.Fire(main)	
