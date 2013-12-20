ViVae Plugin into Nengoros
=============================================

The [ViVae](http://cig.felk.cvut.cz/projects/robo/) simulator was developed on CTU in Prague by [Computational Intelligence Group](http://cig.felk.cvut.cz/).

This is a plugin for integration with the Nengoros simulator. 

The Nengoros simulator uses jython scripts to launch the ROS components (NeuralModules representing ROS nodes). Therefore the Nengoros has runtime dependency on this project (which includes custom NeuralModule for the Vivae simulator). 

Author of the interface: Jaroslav Vitku. 

# Usage

This project consists of two parts: `simulator` and `plugin`. Simulator implements the vivae simulator and its ROS communication interface, while the Plugin implements integration with the Nengoros simulator (Neural Module, communication utilities etc.).

