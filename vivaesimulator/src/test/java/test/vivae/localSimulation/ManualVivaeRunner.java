package test.vivae.localSimulation;

import org.junit.Test;

import vivae.ros.simulator.engine.Simulation;
import vivae.ros.simulator.engine.SimulatorController;
import vivae.ros.simulator.engine.demo.keycontrolled.KeyControlledVivaeSimulator;
import vivae.ros.util.Util;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Run local vivae simulation with graphics turned on. 
 * 
 * @author Jaroslav Vitku
 *
 */
public class ManualVivaeRunner {

	public static void main(String[] args){
		ManualVivaeRunner mvr = new ManualVivaeRunner(); 

		mvr.testVivaeRunner();
		System.out.println("ended");
	}

	@Test
	public void testVivaeRunner(){

		//System.out.println("Working Directory = " + System.getProperty("user.dir"));
		//ClasspathPrinter.printListFiles();

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

		Util.waitLoop(10);

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
		
		Util.waitLoop(10);

		System.out.println("\n\nStopping the simulation");
		sc.stop();
		assertTrue(sc.isInited());
		assertFalse(sc.isRunning());
		
		Util.waitLoop(10);

		System.out.println("\n\n Resuming the simulation");
		sc.start();
		
		Util.waitLoop(10);

		sc.stop();
		sc.destroy();

		System.out.println("\n\n Restarting simulation from the scratch\n");
		sc.start();
		
		Util.waitLoop(10);

		System.out.println("\nCLosing the simulation end exiting the process..");
		sc.destroy();
	}
}
