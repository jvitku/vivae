ViVae Plugin into Nengoros
=============================================

The [ViVae](http://cig.felk.cvut.cz/projects/robo/) simulator was developed on CTU in Prague by [Computational Intelligence Group](http://cig.felk.cvut.cz/).

This is a plugin for integration with the Nengoros simulator. 

The Nengoros simulator uses jython scripts to launch the ROS components (NeuralModules representing ROS nodes). Therefore the Nengoros has runtime dependency on this project (which includes custom NeuralModule for the Vivae simulator). 

Author of the interface: Jaroslav Vitku. 

# Description

The SimulatorServer holds the Vivae simulator core and provides ROS services, such as:

* loadMap
* start
* stop
* reset
* spawnAgent

etc. The folder `vivaeplugin/python` contains helper scripts and examples of usage in the Nengoros system.

The Nengoros is able to initialize the Vivae simulation on the SimulationServer (`vivae.ros.simulator.server.SimulatorServer`) over the ROS network. The loaded map contains agents bodies. 

New agents with customizable sensors can be spawned into these bodies. This is done by the method `addAgent(..)`. This method does:

* connects controls to the first free agent body in the current map
* provides agents controls (subscribe) and sensors (publish) over the ROS network
* adds corresponding terminations (to agents actuators) and origins (from agents sensors) locally into the VivaeNeuralModule
* these Nengo connections can be then connected by means the Nengo simulator normally

## Sensors / Actuators

Agent in the Vivae is controlled by two float values defining speeds on two wheels. 
Agent can sense three main things in the current state:

* friction - friction of sensed surface. Friction sensors are marged as white squares. Road has the smallest friction.
* distace - distance meaures distance to the nearest obstacle. 
* speed - publishes value of the current speed
	
Agents sensory data are initialized in the following way (@see ``)

The method `String name, int numSensors, float frictionDist, float maxDist)` will spawn agent with numSensors/2 of distance (obstacle) sensors and numSensors/2 of friction sensors. 

Parameter numSensors supports even numbers > 4, the agent will use two sensors: distance (lines) and friction (rectangles). The agent will publish array of values of the following structure:

* [distanceSensorData, frictionSensorData, currentSpeed]

and dimensions of data are:

* [numSensors/2, numSensors/2, 1]

## Example of use:

The call of the method `spawnAgent("test",8,10,30)`, will spawn the agent with the following properties:

* 4 distance sensors measuring distance to an obstacle with the range `maxDist`
* 4 friction sensors placed `frictionDist` from the agents body
* 1 sensor of actual speed


# Usage

This project consists of two parts: `simulator` and `plugin`. Simulator implements the vivae simulator and its ROS communication interface, while the Plugin implements integration with the Nengoros simulator (Neural Module, communication utilities etc.).

## Interating with Nengoros

First: link this project to the Nengoros simulator:

	cd demonodes && linkdata -cf ../vivae/vivaeplugin
	
This copies all scripts (`vivaeplugin/python`) and GUI data into the `nengo/simulator-ui/nr-demo`.

## Using the Vivae from Nengoros Scripting interface

Examples how to use this project from the scripting interface are contained in the folder `nengo/simulator-ui/nr-demo/vivaeplugin`, to start a simple example, open Nengo GUI:

	cd nengo/simulator-ui/
	./nengo
	
Open the Nengo scripting interface and load the script by typing:

	run nr-demo/vivaeplugin/01PRegulator.py
	
This example will do the following:

* Create the Vivae NeuralModule (node representing the simulator)
* Start the vivae SimulatorServer externally (launches the simulator)
* Load the map into the simulator
* spawn agent (add a ROS control interface to a agents body in the Vivae simulator), this creates ROS publisher/subscriber (sensors/actuators)
* register this agent into the Nengoros simulator (add corresponding origin & termination with modem to the NeuralModule)
* register simple proporcial controller to the agent
* start the simulation and simulate several seconds of the system (the agent should attempt to drive along the road)
* stop the simulation
* show the Nengo interactive simulation window (this resets the system (resets the Nengoros network and Vivae simulaiton))


# TODO

* Destroy() : When deleting VivaeNeuralModule by the Jython script (e.g. net.add with the same name), it is OK. However, when deleting the node from Nengo gui, the j.dispose() in the destroy() method is not released until the synchronousService from the Nengoros crashes. It seems as some concurrency problem. Probably the problem is that the Simulator core (Jframe) contains also SimulatorControls, which communicate with Nengo (concurency) and therefore cannot be destroyed. 

* Decide the question whether support connecting multidimensional terminations/origins in the Nengoros or leave it as TODO