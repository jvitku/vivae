package test.ros.controlsServer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import vivae.ros.simulator.client.impl.SynchronousClient;
import ctu.nengoros.RosRunner;

import vivae.ros.util.Util;

/**
 * RosCommunicationTest auto-starts and auto-shuts down the core and
 * is able to start arbitrary ROS-java nodes.
 *  
 * @author Jaroslav Vitku
 *
 */
public class ControlsServer extends ctu.nengoros.nodes.RosCommunicationTest{

	public static final String server = "vivae.ros.simulator.server.SimulatorServer";
	public static final String requester = "vivae.ros.simulator.client.impl.SynchronousClient";
	
	public String[] names = new String[]{"data/scenarios/arena1.svg", 
			"data/scenarios/arena2.svg", 
			"data/scenarios/ushape.svg" };

	@Ignore
	@Test
	public void startStopServer(){
		RosRunner rr = runNode(server);
		//NodeMain node = rr.getNode();
		assertTrue(rr.isRunning());
		
		sleep(100);
		
		assertTrue(rr.isRunning());
		rr.stop();
		assertFalse(rr.isRunning());
	}

	@Ignore
	@Test
	public void startStopClientServer(){
		
		RosRunner s= runNode(server);		// server
		assertTrue(s.isRunning());
		
		RosRunner rr = runNode(requester);	// client
		assertTrue(rr.isRunning());
		
		sleep(100); // cannot shut down the server immediately
		
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
		
		Util.waitLoop(1000);
		RosRunner rr = runNode(requester);	// client
		assertTrue(rr.isRunning());
		
		Util.waitLoop(1000);
		
		SynchronousClient cl = (SynchronousClient)rr.getNode();
		
		resp = cl.callLoadMap(names[0]);
		System.out.println("map loaded OK? "+resp);
		
		resp = cl.callSetVisibility(true);
		System.out.println("visibility set OK? "+resp);
		
		resp = cl.callStartSimulation();
		System.out.println("simulation started OK? "+resp);
		
		
		Util.waitLoop(2000);
		
		resp = cl.callStopSimulation();
		System.out.println("simulation stopped OK? "+resp);
		
		resp = cl.callDestroySimulation();
		System.out.println("simulation destroyed OK? "+resp);
		
		Util.waitLoop(100);
		
		s.stop();
		assertFalse(s.isRunning());
		
		rr.stop();
		assertFalse(rr.isRunning());
	}
	
}
