package test.ros.simulatorServer;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import vivae.ros.simulator.client.impl.nodes.AgentControlClientNode;
import vivae.ros.util.Util;
import ctu.nengoros.RosRunner;
import ctu.nengoros.network.node.testsuit.RosCommunicationTest;

public class SpawnAndControlAgents extends RosCommunicationTest{

	public static final String server = "vivae.ros.simulator.server.SimulatorServer";
	public static final String requester = "vivae.ros.simulator.client.impl.nodes.AgentControlClientNode";

	private int sleeptime = 10;
	private int maxsleep = 30000; 	// give up waiting after 30 sec

	@Test
	public void oneAgent(){

		RosRunner s= runNode(server);		// server
		assertTrue(s.isRunning());
		RosRunner rr = runNode(requester);	// client
		assertTrue(rr.isRunning());

		AgentControlClientNode cl = (AgentControlClientNode)rr.getNode();

		// setup the SimulatorServer, spawn and agent, 
		// connect to the agents remote ROS interface
		// start the simulation, control the agents actuators
		// and read data, stop the simulation and check sensors
		vivae.SpawnResponse agent = cl.prepareVivaeSimulation();
		cl.connectToAgent(agent.getPubTopicName(), agent.getSubTopicName());
		cl.runTheSimulationFor(500);//ms

		this.waitForSimulationToStop(cl);

		System.out.println("measured data are: ");
		this.printData(cl.getSensoryData());

		boolean result = this.speedNonZero(cl.getSensoryData());
		System.out.println("agent had non-zero speed? "+result);
		assertTrue(result);
		
		result = this.sensoryDataChange(cl.getSensoryData());
		System.out.println("data on agents sensors changed? "+result);
		assertTrue(result);

		System.out.println("Stopping nodes");

		s.stop();
		assertFalse(s.isRunning());
		rr.stop();
		assertFalse(rr.isRunning());
	}

	/**
	 * Check whether the agent moved in the environment
	 * 
	 * @return true if at least one sample from the speed sensor
	 * had nonzero value
	 */
	private boolean speedNonZero(ArrayList<float[]> data){
		for(int i=0; i<data.size(); i++){
			if(data.get(i)[data.get(i).length-1] != 0)
				return true;
		}
		return false;
	}

	/**
	 * Return true if a value on at least one sensor changed during
	 * the simulation (speed sensor exclusive). 
	 * 
	 * @param data measured data
	 * @return
	 */
	private boolean sensoryDataChange(ArrayList<float[]> data){

		float[] initial = data.get(0);

		for(int i=1; i<data.size(); i++){
			for(int j=0; j<initial.length; j++){
				if(initial[j] != data.get(i)[j])
					return true;
			}
		}
		return false;
	}


	private void waitForSimulationToStop(AgentControlClientNode cl){
		int poc = 0;
		System.out.println("Waiting for simulaiton to end");

		while(cl.isRunning()){	
			Util.waitLoopQuiet(sleeptime);

			if(sleeptime*poc++ > maxsleep){
				System.err.println("Waited too long for the simulation to finish!");
				fail();
			}
		}
		System.out.println("OK, simulaiton stopped");
	}

	private void printData(ArrayList<float[]> data){
		String tmp;
		for(int i=0; i<data.size(); i++){

			float[] d = data.get(i);
			tmp = "no:"+i +"\t\t";
			for(int j=0; j<d.length; j++){
				tmp = tmp+","+d[j]; 
			}
			System.out.println(tmp);
		}
	}
}
