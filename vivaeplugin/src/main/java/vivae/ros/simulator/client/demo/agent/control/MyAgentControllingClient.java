package vivae.ros.simulator.client.demo.agent.control;

import org.ros.node.ConnectedNode;

import vivae.ros.simulator.client.impl.AgentSpawnSynchronousClient;

/**
 * 
 * This demo shows how this client can:
 * -connect to the simulatorServer
 * -initialize the simulation
 * -spawn an agent in the simulation
 * -connect to the agent over the ROS network
 * -start the simulation
 * -control the agent
 * 
 * 
 * To run this demo:
 * 
 * -Run the ROS core, e.g.:
 * 		cd jroscore && ./jroscore
 * 
 * -RUn the class SimulatorServer, e.g.:
 * 		./run vivae.ros.simulator.server.SimulatorServer
 * 
 * -Run this class:
 * 		./run vivae.ros.simulator.client.demo.agent.control.MyAgentControllingClient
 * 
 *  ..and press enter
 *  
 * 
 * 
 * @author Jaroslav Vitku
 *
 */
public class MyAgentControllingClient extends AgentSpawnSynchronousClient {

	public MyAgentControllingClient(ConnectedNode connectedNode) {
		super(connectedNode);
		// TODO Auto-generated constructor stub
	}
	
	

}
