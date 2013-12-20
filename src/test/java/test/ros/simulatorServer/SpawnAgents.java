package test.ros.simulatorServer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import vivae.ros.simulator.client.impl.AgentSpawnSynchronousClient;
import vivae.ros.util.Util;
import ctu.nengoros.RosRunner;
/**
 * Further testing of functionalities of SimulatorServer: 
 * spawn controlled agents in the simulator.
 * 
 * TODO 
 * 
 * @author Jaroslav Vitku
 *
 */
public class SpawnAgents extends ctu.nengoros.nodes.RosCommunicationTest{

	public static final String server = "vivae.ros.simulator.server.SimulatorServer";
	public static final String requester = "vivae.ros.simulator.client.impl.AgentSpawnSynchronousClient";
	
	public String[] names = new String[]{"data/scenarios/arena1.svg", 
			"data/scenarios/arena2.svg", 
			"data/scenarios/ushape.svg" };

	String[] agentNames = new String[]{"a", "b", "c", "overagented" };
	
	@Test
	public void oneAgent(){
		boolean resp;

		RosRunner s= runNode(server);		// server
		assertTrue(s.isRunning());
		
		//Util.waitLoop(1000);
		RosRunner rr = runNode(requester);	// client
		assertTrue(rr.isRunning());
		
		
		// This must be here to initialize the services TODO improve this
		Util.waitLoop(10);
		
		AgentSpawnSynchronousClient cl = (AgentSpawnSynchronousClient)rr.getNode();
		
		resp = cl.callLoadMap(names[0]);
		System.out.println("map loaded OK? "+resp);
		assertTrue(resp);
		
		resp = cl.callSetVisibility(true);
		System.out.println("visibility set OK? "+resp);
		assertTrue(resp);
		
		vivae.SpawnResponse spr = cl.spawnAgent("testAgent1");
		System.out.println("visibility set OK? "+spr.getSpawnedOK());
		assertTrue(spr.getSpawnedOK());
		
		resp = cl.callStartSimulation();
		System.out.println("simulation started OK? "+resp);
		assertTrue(resp);
		
		Util.waitLoop(100);
		
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
