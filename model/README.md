# machine learning

## Set up environment
first install Anaconda form its website, then run the following commands (remember to replace the environment name on the first and second commands):
```
	conda create -n NewEnvironmentName python=3.6
	source activate NewEnvironmentName
	pip install tensorflow-gpu==1.9.0
	conda install cudatoolkit==9.0
	conda install cudnn==7.3.1
	pip install keras=2.2.0
	pip install pydot graphviz
	pip install requests fire opencv-python matplotlib
	conda install pillow
	pip install tqdm
```

## Train models
basic format:
```
	python train.py --network=[model_name] --GPU=0
```
The network attribute is required, and if the model python file is named mobilenet.py, the command need to contain "--network=mobilenet". Type "htop" command to check if GPU usage. Don't use GPU when others are using it. 

We can specify other attributes:
```
	GPU				(Which GPU is available)
	optimizer  		(sgd/adam/rmsprop)
	lr 				(learning)
	decay
	dataset_root  	(where the root of data folder is)
	description  	(appended to the name of checkpoint folder)
    model_path      (Optional, the path to checkpoints)
```
Example:
```
	python train.py --network=mobilenet --GPU=0 --optimizer=sgd --lr=0.01 --decay=0.0001 --dataset_root='data' --description='' --model_path=None
```

## Deploy the model
create a new environment and install keras==2.2.4 and tensorflow==1.13. Then install tf_nightly to use TFLiteConverter.
```
pip install keras==2.2.4 tensorflow==1.13
pip install tf_nightly
```