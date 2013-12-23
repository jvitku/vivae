package test.ros.simulatorServer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.*;

import vivae.ros.simulator.client.impl.nodes.SynchronousClientNode;
import vivae.ros.simulator.server.Sim;
import ctu.nengoros.RosRunner;
import vivae.ros.util.Util;

/**
 * RosCommunicationTest auto-starts and auto-shuts down the core and
 * is able to start arbitrary ROS-java nodes.
 * 
 * This tests basic functionalities of SimulatorServer and SimulatorClient: e.g.
 * loading the map over the ROS network and starting/stopping the simulation.
 *  
 * @author Jaroslav Vitku
 *
 */
public class BasicClientServer extends ctu.nengoros.nodes.RosCommunicationTest{

	public static final String server = "vivae.ros.simulator.server.SimulatorServer";
	public static final String requester = "vivae.ros.simulator.client.impl.nodes.SynchronousClientNode";
	
	@Test
	public void startStopServer(){
		RosRunner rr = runNode(server);
		//NodeMain node = rr.getNode();
		assertTrue(rr.isRunning());
		
		sleep(10);
		
		assertTrue(rr.isRunning());
		rr.stop();
		assertFalse(rr.isRunning());
	}

	@Test
	public void startStopClientServer(){
		
		RosRunner s= runNode(server);		// server
		assertTrue(s.isRunning());
		
		RosRunner rr = runNode(requester);	// client
		assertTrue(rr.isRunning());
		
		sleep(200); // cannot shut down the server immediately
		
		s.stop();
		assertFalse(s.isRunning());
		
		rr.stop();
		assertFalse(rr.isRunning());
	}
	
	@Test
	public void testVivaeRunner(){
		boolean resp;

		RosRunner s= runNode(server);		// server
		assertTrue(s.isRunning());
		RosRunner rr = runNode(requester);	// client
		assertTrue(rr.isRunning());
		
		SynchronousClientNode cl = (SynchronousClientNode)rr.getNode();
		
		resp = cl.callLoadMap(Sim.Maps.names[0]);
		assertTrue(resp);
		
		resp = cl.callSetVisibility(true);
		System.out.println("visibility set OK? "+resp);
		assertTrue(resp);
		
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

