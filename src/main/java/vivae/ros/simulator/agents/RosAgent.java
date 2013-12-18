package vivae.ros.simulator.agents;


public interface RosAgent {

	/**
	 * This accesses the sensory data from the environment
	 * and moves with the agent in Vivae.
	 */
	public abstract void moveControlledObject();

	// setters and getters..
	public abstract double getMaxSensorDistance();

	public abstract void setMaxSensorDistance(double maxDistance);

	public abstract double getFrictionDistance();

	public abstract void setFrictionDistance(double frictionDistance);

	public abstract int getNumSensors();

	public abstract void setNumSensors(int numSensors);

	public abstract String getPubTopic();

	public abstract String getSubTopic();

	public abstract String getName();

	/**
	 * Set how many floats will be published
	 * @param len
	 */
	public abstract void setSensoryDataLength(int len);

	public abstract int getSensoryDataLength();

	/**
	 * For stopping the simulator
	 * 
	 * @return true if simulaiton can continue
	 */
	public boolean isReady();
	
	public void discardReady();

	/**
	 * set robots speeds to 0 etc..
	 */
	public void reset();
	
}