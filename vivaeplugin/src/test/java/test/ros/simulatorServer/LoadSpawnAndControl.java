package test.ros.simulatorServer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import vivae.ros.simulator.client.impl.nodes.AgentSpawnClientNode;
import vivae.ros.simulator.server.Sim;
import vivae.ros.util.Util;
import ctu.nengoros.RosRunner;

/**
 * Tests the entire communication over the ROS network:
 * -start simulatorServer
 * -start simulatorClient
 * -ask for loading the map
 * -spawn an agent
 * -connect to the agent
 * -control the agent
 * 
 * @author Jaroslav Vitku
 *
 */
public class LoadSpawnAndControl extends ctu.nengoros.nodes.RosCommunicationTest{

	public static final String server = "vivae.ros.simulator.server.SimulatorServer";
	public static final String requester = "vivae.ros.simulator.client.impl.nodes.AgentSpawnClientNode";

	@Test
	public void oneAgent(){
		boolean resp;

		RosRunner s= runNode(server);		// server
		assertTrue(s.isRunning());
		RosRunner rr = runNode(requester);	// client
		assertTrue(rr.isRunning());

		System.out.println("building cl ");
		AgentSpawnClientNode cl = (AgentSpawnClientNode)rr.getNode();

		System.out.println("cl  done, calling");
		resp = cl.callLoadMap(Sim.Maps.names[0]);
		System.out.println("calling done, the..");
		
		System.out.println("map loaded OK? "+resp);
		assertTrue(resp);

		resp = cl.callSetVisibility(true);
		System.out.println("visibility set OK? "+resp);
		assertTrue(resp);
		
		//Util.waitLoop(100);

		// spawn agent 1
		vivae.SpawnResponse spr = cl.spawnAgent("testAgent1");
		System.out.println("spawned OK? "+spr.getSpawnedOK());
		assertTrue(spr.getSpawnedOK());

		resp = cl.callStartSimulation();
		System.out.println("simulation started OK? "+resp);
		assertTrue(resp);

		Util.waitLoop(1000);

		resp = cl.callStopSimulation();
		System.out.println("simulation stopped OK? "+resp);
		assertTrue(resp);

		resp = cl.callDestroySimulation();
		System.out.println("simulation destroyed OK? "+resp);
		assertTrue(resp);

		s.stop();
		assertFalse(s.isRunning());

		rr.stop();
		assertFalse(rr.isRunning());
	}

}