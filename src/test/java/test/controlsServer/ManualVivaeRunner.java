package test.controlsServer;

import java.net.URL;
import java.net.URLClassLoader;

import org.junit.Test;

import vivae.ros.simulator.Simulation;
import vivae.ros.simulator.SimulatorController;
import vivae.ros.simulator.demo.keycontrolled.KeyControlledVivaeSimulator;
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


	@Test
	public /**/void testVivaeRunner(){

		System.out.println("Working Directory = " +
				System.getProperty("user.dir"));

		String classpath = System.getProperty("java.class.path");
		System.out.println("claaaaaaaaaaaaaaaaaaa "+classpath);

		ClassLoader cl = ClassLoader.getSystemClassLoader();

		URL[] urls = ((URLClassLoader)cl).getURLs();

		for(URL url: urls){
			System.out.println(url.getFile());
		}
		
		///////////////////////////////////////////////

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

		System.out.println("stopping the arena..");
		sc.stop();
		sc.destroy();
	}
}
