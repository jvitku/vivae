package test.ros.controlsServer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import ctu.nengoros.RosRunner;

/**
 * RosCommunicationTest auto-starts and auto-shuts down the core and
 * is able to start arbitrary ROS-java nodes.
 *  
 * @author Jaroslav Vitku
 *
 */
public class ControlsServer extends ctu.nengoros.nodes.RosCommunicationTest{

	public static final String server = "vivae.ros.simulator.server.SimulatorServer";
	public static final String requester = "test.ros.controlsServer.Requester";

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

		RosRunner s= runNode(server);		// server
		assertTrue(s.isRunning());
		
		sleep(1000); 
		
		RosRunner rr = runNode(requester);	// client
		assertTrue(rr.isRunning());
		
		sleep(1000); // cannot shut down the server immediately
		
		Requester req = (Requester)rr.getNode();
		req.callRequestMap("data/scenarios/arena1.svg");
		req.callSetVisible(true);
		/*
		try {
			Thread.sleep(1);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}*/
		req.callStartSimulation();
		/*
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		*/
		req.callStopSimulation();
		req.callDestroySimulation();
		
		/* 
		assertTrue(sc.isInited());
		assertTrue(sc.isRunning());
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Stopping the arena..");
		*/
		s.stop();
		assertFalse(s.isRunning());
		
		rr.stop();
		assertFalse(rr.isRunning());
	}

}
