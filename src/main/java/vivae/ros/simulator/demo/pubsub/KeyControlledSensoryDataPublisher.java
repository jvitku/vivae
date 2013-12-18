package vivae.ros.simulator.demo.pubsub;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import vivae.controllers.RobotWithSensorController;
import vivae.util.Util;
import vivae.example.FRNNControlledRobot;

/**
 * Derived from FRNNController and KeyboardViveController
 * 
 * This sensor is connected to the ROS nodes, it receives commands and publishes sensory data.
 * 
 * @author Jaroslav Vitku
 *
 */
public class KeyControlledSensoryDataPublisher extends RobotWithSensorController implements KeyListener {

	@Override
	public void moveControlledObject() {
		if (robot instanceof FRNNControlledRobot) {

			// here is collecting the sensory data
			double[] input = Util.flatten(((FRNNControlledRobot) robot).getSensoryData());
					System.out.println("data: "+toAr(input));

			// here is the control logic
			Speed s = this.collectKeyCommands();

			// wheels are placed wrong?
			moveRobot(s.rw, s.lw);
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

	/**
	 * moves with the robot by turning the wheels
	 * @param lWheel
	 * @param rWheel
	 */
	protected void moveRobot(double lWheel, double rWheel){
		double angle;
		double acceleration = 5.0 * (lWheel + rWheel);
		if (acceleration < 0) {
			acceleration = 0; // negative speed causes problems, why?
		}
		double speed = Math.abs(robot.getSpeed() / robot.getMaxSpeed());
		speed = Math.min(Math.max(speed, -1), 1);
		if (rWheel > lWheel) {
			angle = 10 * (1.0 - speed);
		}
		else if(rWheel == lWheel){
			angle = 0;
		} else {
			angle = -10 * (1.0 - speed);
		}
		//System.out.println("movind with ang "+angle+" acc "+acceleration +" lw "+lWheel+" rw "+rWheel);
		robot.rotate((float) angle);
		
		if(acceleration < 0)
			acceleration = 0;
		// It seems that there is a bug and robot cannot run backwards
		/*
		if(acceleration<0)
			robot.decelerate((float)acceleration);
		else
			robot.accelerate((float) acceleration);
			*/
		robot.accelerate((float)acceleration);
	}


	///////////////////// controller part, read key commands
	// taken from the KeyboardVivaeController
	protected boolean isLeftKeyDown = false,  
			isRightKeyDown = false,  
			isUpKeyDown = false,  
			isDownKeyDown = false;

	public class Speed{
		public double lw;
		public double rw;
		public Speed(){
			this(0,0);
		}
		public Speed(double l, double r){
			this.lw = l;
			this.rw = r;
		}
	}

	public Speed collectKeyCommands() {

		double increment = 0.5;
		Speed s = new Speed();
		if (isLeftKeyDown) {
			s.rw +=increment;
		} else if (isRightKeyDown) {
			s.lw += increment;
		}
		if (isDownKeyDown) {
			s.rw -= increment;
			s.lw -= increment;
		} else if (isUpKeyDown) {
			s.rw += increment;
			s.lw += increment;
		}
		/*
		if(s.lw != 0 || s.rw !=0)
			System.out.println("collected speed: "+s.lw+" "+s.rw);
			*/
		return s;
	}



	public void keyTyped(KeyEvent e) {
		//
	}

	@Override
	public void keyPressed(KeyEvent e) {
	//	System.out.println("pressed");

		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			isLeftKeyDown = true;
			isRightKeyDown = false;
			break;
		case KeyEvent.VK_RIGHT:
			isRightKeyDown = true;
			isLeftKeyDown = false;
			break;
		case KeyEvent.VK_DOWN:
			isDownKeyDown = true;
			isUpKeyDown = false;
			break;
		case KeyEvent.VK_UP:
			isUpKeyDown = true;
			isDownKeyDown = false;
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_LEFT:
			isLeftKeyDown = false;
			break;
		case KeyEvent.VK_RIGHT:
			isRightKeyDown = false;
			break;
		case KeyEvent.VK_DOWN:
			isDownKeyDown = false;
			break;
		case KeyEvent.VK_UP:
			isUpKeyDown = false;
			break;
		}
	}
}
