ViVae Simulator Equipped with ROS Support
=============================================

The [ViVae](http://cig.felk.cvut.cz/projects/robo/) simulator was developed on CTU in Prague by [Computational Intelligence Group](http://cig.felk.cvut.cz/).

This simulator was equipped with a [ROS](http://www.ros.org/) interface for the main purpose of use in the [Nengoros](http://nengoros.wordpress.com/) neural simulator.

Now, the simulation can be controlled over the ROS network. 

Author of the interface: Jaroslav Vitku. 

# Usage

## Start the Controls Server

Start the 'vivae.ros.simulatorControlsServer.ControlsServer' as a ROS node, then it provides services ( @see messages in `vivae/ros/vivae`). These services are:

* `SimController.srv`:

	init,start,stop,destroy,reset,setvisible,setinvisible

* `LoadMap.srv`
	
	pass the string with the path to map

* `Spawn.srv`

	spawns new agent in the environment (spawns agents until the number all agents placed in the map are connected)
	
* `Kill.srv`

	kills an agent connected to the simulator

Agents are then controlled by the message of type `Velocity.msg`. 

## Start the Simulator Controller

Start the `vivae.ros.simulator.SimulatorController` on the other side (e.g. from the Nengoros simulator) and control the simulation.


## Spawn new Agents in the Simulation

Spawn agents and control them. TODO: describe this more.


# Known Issues



1. Reloading Vivae window on reset:

	* while resetting the simulation: (several times when openning the Nengo interactive window) the simulator is entirely reloaded
	
	* error:
		-set in vivaeneuralmodule method "reset" to call only start/stop requests on vivae
		-launch nengo, launch vivae and connect agent in the simulator, open interactive window and start script (agent moves slowly and unresponsively)
		-call vivae.reset(), agent moves much faster and signal is better