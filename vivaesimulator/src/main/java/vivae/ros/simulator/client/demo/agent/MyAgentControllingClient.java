package vivae.ros.simulator.client.demo.agent;

import org.ros.node.ConnectedNode;

import vivae.ros.simulator.client.impl.nodes.AgentControlClientNode;
import vivae.ros.simulator.server.Sim;

/**
 * This client is able to:
 * 
 * -load map (on the simulatorServer)
 * -spawn agent
 * -connect to the agent over the ROS network
 * -start the simulation
 * -control the agent and read sensory data
 * -stop the simulation
 * 
 * To run this demo run:
 * 
 * (may need to run ./gradlew installApp)
 * 
 * -core:
 * 		jroscore
 * 
 * -SImulatorServer:
 * 		
 * 		cd vivae/vivaesimulator && ./vivae
 * 
 * -This node:
 * 
 * 		cd vivae/vivaesimulator && ./run vivae.ros.simulator.client.demo.agent.MyAgentControllingClient
 * 
 * @author Jaroslav Vitku
 *
 */
public class MyAgentControllingClient extends AgentControlClientNode{

	protected int runtime = 10000;
	
	// register my client services
	@Override
	public void onStart(final ConnectedNode connectedNode) {
		super.onStart(connectedNode);

		cn = connectedNode;
		
		// Call e.g. this externally:
		vivae.SpawnResponse agent = this.prepareVivaeSimulation();
		super.connectToAgent(agent.getPubTopicName(), agent.getSubTopicName());
		super.runTheSimulationFor(runtime);//ms
	}

	@Override
	public vivae.SpawnResponse prepareVivaeSimulation(){

		System.out.println(me+"loading the default map");
		callLoadMap(Sim.Maps.DEFAULT);
		callSetVisibility(true);

		System.out.println(me+"Requesting this agent: "+name);
		
		vivae.SpawnResponse sr = spawnAgent(name,numSensors,maxDist,frictionDist);
		//vivae.SpawnResponse sr = spawnAgent(name,numSensors,0,frictionDist);
		//vivae.SpawnResponse sr = spawnAgent(name,8);
		//vivae.SpawnResponse sr = spawnAgent(name,8,10);

		System.out.println(me+"agent registered OK? "+sr.getSpawnedOK());

		System.out.println("Reading this num of sensors:  "+ sr.getNumSensors());
		return sr;
	}
}

