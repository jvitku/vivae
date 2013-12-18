package test.localSimulation;

import org.junit.Test;

import vivae.ros.simulator.Simulation;
import vivae.ros.simulator.SimulatorController;
import vivae.ros.simulator.demo.keycontrolled.KeyControlledVivaeSimulator;
import vivae.ros.util.ClasspathPrinter;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Run vivae simulation with graphics on
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


	// TODO this one does not work
	//@Test
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
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Stopping the arena..");
		sc.stop();
		sc.destroy();
	}
}
