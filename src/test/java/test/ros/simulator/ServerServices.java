package test.ros.simulator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import vivae.ros.simulator.Simulation;
import vivae.ros.simulator.SimulatorController;
import vivae.ros.simulator.demo.keycontrolled.KeyControlledVivaeSimulator;

public class ServerServices {
	
	@Test
	public void testVivaeRunner(){


		//Simulation vs = new 
		/**
		 * One robot is controlled by keyboard
		 */
		Simulation vs = new KeyControlledVivaeSimulator();
		SimulatorController sc = vs.getController();

		assertFalse(sc.isInited());
		assertFalse(sc.isRunning());

		sc.init();

		assertTrue(sc.isInited());
		assertFalse(sc.isRunning());

		sc.start();

		assertTrue(sc.isInited());
		assertTrue(sc.isRunning());

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Stopping the arena..");
		sc.stop();
		sc.destroy();
	}

}
