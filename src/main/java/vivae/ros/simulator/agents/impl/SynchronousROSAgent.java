package vivae.ros.simulator.agents.impl;

import org.ros.message.MessageListener;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;
import vivae.controllers.RobotWithSensorController;
import vivae.example.FRNNControlledRobot;
import vivae.ros.simulator.agents.RosAgent;
import vivae.ros.util.impl.SyncedUnit;
import vivae.util.Util;

/**
 * Holds agent with ROS communication utils and vivae utils..
 * 
 * @author Jaroslav Vitku
 *
 */
public class SynchronousROSAgent extends RobotWithSensorController implements RosAgent{
	
		public String name;
		public String pub;
		public String sub;
		
		private double maxDistance =50;
		private double frictionDistance = 25;
		private int numSensors = 6; // note that this may not be the num of sensors..
		
		private volatile float lw;
		private volatile float rw;
		private volatile double speed;
		
		private final Publisher<std_msgs.Float32MultiArray> publisher;
		private int dataLength;
		
		private class SU extends SyncedUnit{
			public SU(String name, boolean synchronous) {
				super(synchronous,name);
			}
		}
		
		private final SU su;
		
		/**
		 * 
		 * @param name
		 * @param pubTopic
		 * @param subTopic
		 * @param cn
		 * @param synchronous whether simulator should wait after each step for velocity commands 
		 */
		public SynchronousROSAgent(String name, String pubTopic, String subTopic, ConnectedNode cn, boolean synchronous){
			
			su = new SU(name, synchronous); //in case of synchronous communication, SyncedUnit is always ready
			this.name = name;
			pub =pubTopic;
			sub =subTopic;
			
		    // register sensory data publisher
			publisher = cn.newPublisher(pub, std_msgs.Float32MultiArray._TYPE);
			
			// register listener for motor commands
		    Subscriber<std_msgs.Float32MultiArray> subscriber = cn.newSubscriber(sub, std_msgs.Float32MultiArray._TYPE);
		    subscriber.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
		        @Override
		        public void onNewMessage(std_msgs.Float32MultiArray message) {
		        	
		        	float[] data = message.getData();
		        	if(data.length != 2){
		        		System.err.println("Aget: received data have has to contain 2 values!");
		        		return;
		        	}
		        	// set the motor commands
		        	lw = data[1];
		        	rw = data[0];
		        	//System.out.println("agent: received");
		        	su.setReady(true);	// velocity command received, set agent as ready
		        }
		      });
		}
	
		/* (non-Javadoc)
		 * @see vivae.ros.simulator.agents.RosAgentt#moveControlledObject()
		 */
		@Override
		public void moveControlledObject() {
			if (robot instanceof FRNNControlledRobot) {

				// here is collecting the sensory data
				double[] input = Util.flatten(((FRNNControlledRobot) robot).getSensoryData());
				double speed = ((FRNNControlledRobot)robot).getSpeed();
				double[] sensoryData = addSpeed(input,speed);
				
				// send sensory data over the network
		        std_msgs.Float32MultiArray out = publisher.newMessage();
		        out.setData(tofloat(sensoryData));
		        publisher.publish(out);
		        
				// turn the wheels
				moveRobot(lw, rw);
				//System.out.println("agent: publishing!");
				
				su.setReady(false);		// robot moved, sensory data published, waiting for new commands
			}
		}

		private double[] addSpeed(double[] input, double speed){
			double [] sensoryData = new double[input.length+1];
			for(int i=0; i<input.length; i++)
				sensoryData[i] = input[i];
			sensoryData[sensoryData.length-1] = speed;
			return sensoryData;
		}
		
		private void moveRobot(double lWheel, double rWheel){
			double angle;
			double acceleration = 5 * (lWheel + rWheel);	// acceleration coefficient?
			if (acceleration < 0) {
				acceleration = 0; // negative speed causes problems, why?
			}
			speed = Math.abs(robot.getSpeed() / robot.getMaxSpeed());
			speed = Math.min(Math.max(speed, -1), 1);
			if (rWheel > lWheel) {
				angle = 15 * (1.0 - speed);
			}
			else if(rWheel == lWheel){
				angle = 0;
			} else {
				angle = -15 * (1.0 - speed);
			}
			//System.out.println("moving with ang "+angle+" acc "+acceleration +" lw "+lWheel+" rw "+rWheel);
			robot.rotate((float) angle);
			robot.accelerate((float)acceleration);
		}
		
		protected String toAr(double[] input){
			String out = "";
			for(int i=0; i<input.length; i++){
				out = out+" "+input[i];
			}
			return out;
		}
		
		private float[] tofloat(double[] data){
			float[] out = new float[data.length];
			for(int i=0; i<out.length; i++){
				out[i]=(float)data[i];
			}
			return out;
		}
		
		// setters and getters..
		/* (non-Javadoc)
		 * @see vivae.ros.simulator.agents.RosAgentt#getMaxSensorDistance()
		 */
		@Override
		public double getMaxSensorDistance() {
			return maxDistance;
		}

		/* (non-Javadoc)
		 * @see vivae.ros.simulator.agents.RosAgentt#setMaxSensorDistance(double)
		 */
		@Override
		public void setMaxSensorDistance(double maxDistance) {
			this.maxDistance = maxDistance;
		}

		/* (non-Javadoc)
		 * @see vivae.ros.simulator.agents.RosAgentt#getFrictionDistance()
		 */
		@Override
		public double getFrictionDistance() {
			return frictionDistance;
		}

		/* (non-Javadoc)
		 * @see vivae.ros.simulator.agents.RosAgentt#setFrictionDistance(double)
		 */
		@Override
		public void setFrictionDistance(double frictionDistance) {
			this.frictionDistance = frictionDistance;
		}

		/* (non-Javadoc)
		 * @see vivae.ros.simulator.agents.RosAgentt#getNumSensors()
		 */
		@Override
		public int getNumSensors() {
			return numSensors;
		}

		/* (non-Javadoc)
		 * @see vivae.ros.simulator.agents.RosAgentt#setNumSensors(int)
		 */
		@Override
		public void setNumSensors(int numSensors) {
			this.numSensors = numSensors;
		}
		
		/* (non-Javadoc)
		 * @see vivae.ros.simulator.agents.RosAgentt#getPubTopic()
		 */
		@Override
		public String getPubTopic(){
			return pub;
		}
		
		/* (non-Javadoc)
		 * @see vivae.ros.simulator.agents.RosAgentt#getSubTopic()
		 */
		@Override
		public String getSubTopic(){
			return sub;
		}

		/* (non-Javadoc)
		 * @see vivae.ros.simulator.agents.RosAgentt#getName()
		 */
		@Override
		public String getName(){ return name; }

		/* (non-Javadoc)
		 * @see vivae.ros.simulator.agents.RosAgentt#setSensoryDataLength(int)
		 */
		@Override
		public void setSensoryDataLength(int len){
			this.dataLength = len;
		}
		
		/* (non-Javadoc)
		 * @see vivae.ros.simulator.agents.RosAgentt#getSensoryDataLength()
		 */
		@Override
		public int getSensoryDataLength(){
			return dataLength;
		}

		@Override
		public boolean isReady() {
			return su.isReady();
		}

		@Override
		public void discardReady() {
			su.discardChildsReady();
		}

		@Override
		public void reset() {
			this.lw = 0;
			this.rw = 0;
			su.setReady(true);
		}		
}

