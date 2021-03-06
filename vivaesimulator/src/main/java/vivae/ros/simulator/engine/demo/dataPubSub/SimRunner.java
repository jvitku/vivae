package vivae.ros.simulator.engine.demo.dataPubSub;

import vivae.ros.simulator.engine.Simulation;
import vivae.ros.simulator.engine.SimulatorController;

/**
 * Runs the simulation for 15 seconds
 * 
 * @author Jaroslav Vitku
 */
public class SimRunner {
	public static void main(String[] args){
		
		int time = 20000;
		testVivaeRunner(time);
	}
	
	public static void testVivaeRunner(int time){
		Simulation vs = new KeyControlledDataPublishingVivaeSim();
		SimulatorController sc = vs.getController();

		sc.init();
		sc.start();
		
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		sc.stop();
		sc.destroy();
	}
}
