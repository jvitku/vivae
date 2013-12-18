package vivae.ros.simulator.demo.keycontrolled;

import vivae.ros.simulator.Simulation;
import vivae.ros.simulator.SimulatorController;

/**
 * This demo shows how the vivae agent can be controlled by keyboard
 * 
 * @author Jaroslav Vitku
 *
 */
public class SimulationRunner {
	
	public static void main(String[] args){
		System.out.println("you can control one agent by keyboard");
		testVivaeRunner();
		System.out.println("ended");
	}
	
	public static void testVivaeRunner(){
		Simulation vs = new KeyControlledVivaeSimulator();
		SimulatorController sc = vs.getController();
		
		sc.start();
		
		System.out.println("\n\nletting the simulation to run 20 seconds, then closing \n\n");
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		sc.stop();
		sc.destroy();
	}
}
