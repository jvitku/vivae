package test.ros.simulatorServer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.*;

import vivae.ros.simulator.client.impl.AgentSpawnSynchronousClient;
import vivae.ros.simulator.server.Sim;
import vivae.ros.util.Util;
import ctu.nengoros.RosRunner;
/**
 * Further testing of functionalities of SimulatorServer: 
 * spawn agents (that can be controlled over the ROS network) in the simulator.
 * 
 * @author Jaroslav Vitku
 *
 */
public class SpawnAgents extends ctu.nengoros.nodes.RosCommunicationTest{

	public static final String server = "vivae.ros.simulator.server.SimulatorServer";
	public static final String requester = "vivae.ros.simulator.client.impl.AgentSpawnSynchronousClient";
	
	@Test
	public void oneAgent(){
		boolean resp;

		RosRunner s= runNode(server);		// server
		assertTrue(s.isRunning());
		RosRunner rr = runNode(requester);	// client
		assertTrue(rr.isRunning());
		
		AgentSpawnSynchronousClient cl = (AgentSpawnSynchronousClient)rr.getNode();
		
		resp = cl.callLoadMap(Sim.Maps.names[0]);
		System.out.println("map loaded OK? "+resp);
		assertTrue(resp);
		
		resp = cl.callSetVisibility(true);
		System.out.println("visibility set OK? "+resp);
		assertTrue(resp);
		
		// spawn agent 1
		vivae.SpawnResponse spr = cl.spawnAgent("testAgent1");
		System.out.println("visibility set OK? "+spr.getSpawnedOK());
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
	
	@Test
	public void moreAgents(){
		boolean resp;

		RosRunner s= runNode(server);		// server
		assertTrue(s.isRunning());
		
		//Util.waitLoop(1000);
		RosRunner rr = runNode(requester);	// client
		assertTrue(rr.isRunning());
		
		AgentSpawnSynchronousClient cl = (AgentSpawnSynchronousClient)rr.getNode();
		
		resp = cl.callLoadMap(Sim.Maps.names[0]);
		System.out.println("map loaded OK? "+resp);
		assertTrue(resp);
		
		resp = cl.callSetVisibility(true);
		System.out.println("visibility set OK? "+resp);
		assertTrue(resp);
		
		/////////////////////////////////////////
		
		// spawn agent 1
		vivae.SpawnResponse spr = cl.spawnAgent("testAgent1");
		System.out.println("agent spawned OK? "+spr.getSpawnedOK());
		assertTrue(spr.getSpawnedOK());
		
		// spawn agent 2		
		vivae.SpawnResponse spr2 = cl.spawnAgent("testAgent2");
		System.out.println("agent spawned OK? "+spr2.getSpawnedOK());
		assertTrue(spr2.getSpawnedOK());
		
		// agents names should not be the same!
		assertFalse(spr2.getName().equalsIgnoreCase(spr.getName()));

		/////////////////////////////////////////
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
	
	@Test
	public void identicalAndTooMuchAgents(){
		boolean resp;

		RosRunner s= runNode(server);		// server
		assertTrue(s.isRunning());
		RosRunner rr = runNode(requester);	// client
		assertTrue(rr.isRunning());
		
		AgentSpawnSynchronousClient cl = (AgentSpawnSynchronousClient)rr.getNode();
		
		resp = cl.callLoadMap(Sim.Maps.names[0]);
		System.out.println("map loaded OK? "+resp);
		assertTrue(resp);
		
		resp = cl.callSetVisibility(true);
		System.out.println("visibility set OK? "+resp);
		assertTrue(resp);
		
		/////////////////////////////////////////
		
		// spawn agent 1
		vivae.SpawnResponse spr = cl.spawnAgent("testAgent1");
		System.out.println("agent spawned OK? "+spr.getSpawnedOK());
		assertTrue(spr.getSpawnedOK());
		
		// spawn agent 2		
		vivae.SpawnResponse spr2 = cl.spawnAgent("testAgent2");
		System.out.println("agent spawned OK? "+spr2.getSpawnedOK());
		assertTrue(spr2.getSpawnedOK());
		
		// agents names should not be the same!
		assertFalse(spr2.getName().equalsIgnoreCase(spr.getName()));
		
		
		// arena will not spawn two agents with identical name
		vivae.SpawnResponse spr22 = cl.spawnAgent("testAgent2");
		System.out.println("agent spawned OK? "+spr22.getSpawnedOK());
		assertFalse(spr22.getSpawnedOK());
		
		// spawn agent 3
		vivae.SpawnResponse spr3 = cl.spawnAgent("testAgent3");
		System.out.println("agent spawned OK? "+spr3.getSpawnedOK());
		assertTrue(spr3.getSpawnedOK());
		
		// try to spawn agent 4
		// in this map, there are only 3 bodies for agents, so this should fail
		vivae.SpawnResponse spr4 = cl.spawnAgent("testAgent4");
		System.out.println("agent spawned OK? "+spr4.getSpawnedOK());
		assertFalse(spr4.getSpawnedOK());
		
		
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
