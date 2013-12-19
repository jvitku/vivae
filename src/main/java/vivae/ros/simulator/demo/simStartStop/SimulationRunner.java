package vivae.ros.simulator.demo.simStartStop;

import vivae.ros.simulator.Simulation;
import vivae.ros.simulator.SimulatorController;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * This demo shows how the modified vivae simulator can be launched in 
 * separate thread and stopped from the outside.
 * 
 * Simulator can be then restarted from the state where stopped, if you want 
 * new simulation, call destroy and start again.
 * 
 * Method destroy() closes the window and releases all resources (process exits)
 * 
 * @author Jaroslav Vitku
 *
 */
public class SimulationRunner {
	
	public static void main(String[] args){
		testVivaeRunner();
		System.out.println("ended");
	}
	
	public static void testVivaeRunner(){
		Simulation vs = new KeyControlledVivaeSimulator();
		SimulatorController sc = vs.getController();
		
		sc.start();
		assertTrue(sc.isInited());
		assertTrue(sc.isRunning());
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		sc.stop();
		assertTrue(sc.isInited());
		assertFalse(sc.isRunning());
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("\n\n Reauming the simulation");
		sc.start();
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		sc.stop();
		sc.destroy();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("\n\n Restarting simulation from the scratch\n");
		sc.start();
		

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("\nCLosing the simulation end exiting the process..");
		sc.destroy();
	}
}
