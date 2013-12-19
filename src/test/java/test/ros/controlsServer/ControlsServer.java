package test.ros.controlsServer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ctu.nengoros.RosRunner;
import vivae.ros.simulator.Simulation;
import vivae.ros.simulator.SimulatorController;
import vivae.ros.simulator.demo.keycontrolled.KeyControlledVivaeSimulator;

/**
 * RosCommunicationTest auto-starts and auto-shuts down the core and
 * is able to start arbitrary ROS-java nodes.
 *  
 * @author Jaroslav Vitku
 *
 */
public class ControlsServer extends ctu.nengoros.nodes.RosCommunicationTest{

	public static final String server = "vivae.ros.simulatorControlsServer.ControlsServer";
	
	
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
	
	@Test
	public void testVivaeRunner(){

		// launch server and use its services
		RosRunner simServer = runNode(server);
		assertTrue(simServer.isRunning());

		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*
		assertTrue(sc.isInited());
		assertTrue(sc.isRunning());

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Stopping the arena..");
		sc.stop();
		sc.destroy();*/
	}

}
