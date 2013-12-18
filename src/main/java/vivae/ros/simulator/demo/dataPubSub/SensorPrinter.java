package vivae.ros.simulator.demo.dataPubSub;

import vivae.ros.simulator.demo.pubsub.KeyControlledSensoryDataPublisher;
import vivae.util.Util;
import vivae.example.FRNNControlledRobot;

/**
 * Derived from FRNNController and KeyboardViveController
 * 
 * This just writes out the sensory data.
 * 
 * Note that the parent has registered keyboard listener.
 * 
 * @author Jaroslav Vitku
 *
 */
public class SensorPrinter extends KeyControlledSensoryDataPublisher{

	@Override
	public void moveControlledObject() {
		if (robot instanceof FRNNControlledRobot) {

			// here is collecting the sensory data
			double[] input = Util.flatten(((FRNNControlledRobot) robot).getSensoryData());
			print(input);
			// here is the control logic
			Speed s = this.collectKeyCommands();

			// wheels are placed wrong?
			super.moveRobot(s.rw, s.lw);
		}
	}

	// print sensory data
	protected String toAr(double[] input){
		String out = "";
		for(int i=0; i<input.length; i++){
			out = out+" "+input[i];
		}
		return out;
	}

	int modulo = 20;
	int poc = 0;
	
	private void print(double[] data){
		poc++;
		if(0==(poc % modulo)){
			System.out.println("data: "+toAr(data));
		}
	}
	
}
