package test.localSimulation;

import org.junit.Test;

import vivae.ros.simulator.Simulation;
import vivae.ros.simulator.SimulatorController;
import vivae.ros.simulator.demo.keycontrolled.KeyControlledVivaeSimulator;
import vivae.ros.util.ClasspathPrinter;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Run local vivae simulation with graphics turned on. 
 * 
 * @author Jaroslav Vitku
 *
 */
public class ManualVivaeRunner {

	public static int wait = 50;
	
	public static void main(String[] args){
		ManualVivaeRunner mvr = new ManualVivaeRunner(); 

		mvr.testVivaeRunner();
		System.out.println("ended");
	}

	@Test
	public void testVivaeRunner(){

		System.out.println("Working Directory = " + System.getProperty("user.dir"));
		ClasspathPrinter.printListFiles();

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
			Thread.sleep(wait*20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Stopping the arena..");
		sc.stop();
		sc.destroy();
	}
	
	/**
	 * Something between demo and test case. This shows how the simulation can be
	 * stopped and resumed from the last state, or destroyed completely. 
	 */
	@Test
	public void startStopStartStopDestroy(){
		
		Simulation vs = new KeyControlledVivaeSimulator();
		SimulatorController sc = vs.getController();
		
		sc.start();
		assertTrue(sc.isInited());
		assertTrue(sc.isRunning());
		
		try {
			Thread.sleep(wait*10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("\n\nStopping the simulation");
		sc.stop();
		assertTrue(sc.isInited());
		assertFalse(sc.isRunning());
		try {
			Thread.sleep(wait*20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("\n\n Resuming the simulation");
		sc.start();
		try {
			Thread.sleep(wait*30);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		sc.stop();
		sc.destroy();
		
		try {
			Thread.sleep(wait*10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("\n\n Restarting simulation from the scratch\n");
		sc.start();
		

		try {
			Thread.sleep(wait*5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("\nCLosing the simulation end exiting the process..");
		sc.destroy();
	}
}
