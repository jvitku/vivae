package vivae.ros.simulator.client;

/**
 * This is synchronous simulation client which is able to spawn agents in the SimulatorServer.
 * 
 * From: vivae.ros.simulator.server.services.AgentSpawnServiceResponseBuilder:
 * 
 * This spawns agents in the ViVae simulation. After loading map, the arena contains
 * agent bodies which are not controlled. In order to control agent in the arena, the 
 * agent needs to be registered. In order to control the agent over the ROS network,
 * the agent has to communicate over the ROS network (publish/subscribe here). 
 * 
 * This service tries to register agent in the arena, if success, it opens the ROS
 * communication channels for the agent and returns all necessary information.
 * 
 *   
 * @author Jaroslav Vitku
 *
 */
public interface AgentSpawningSynchronousClient extends SynchornousClient {
	
	/**
	 * Spawns agent with the default values (num.sensors, friction distance etc..)
	 * 
	 * Not recommended to use, will not initialize sensors correctly.
	 * 
	 * @param name
	 * @return
	 */
	public vivae.SpawnResponse spawnAgent(String name);
	
	/**
	 * Not recommended to use, will spawn agent with all sensor parameters
	 * set to zero. Obstacle sensors will return NaN and friction 
	 * sensors will measure only friction directly under the agent. 
	 * 
	 * Parameter numSensors supports even numbers > 4, the agent will use
	 * two sensors: distance (lines) and friction (rectangles). The agent
	 * will publish array of values of the following structure:
	 * [distanceSensorData, frictionSensorData, currentSpeed]
	 * and dimensions of data:
	 * [numSensors/2, numSensors/2, 1]
	 * 
	 * 
	 * @param name
	 * @param numSensors supported even numbers > 4
	 * @return
	 */
	public vivae.SpawnResponse spawnAgent(String name, int numSensors);
	
	/**
	 * WIll spawn agent with no distance sensors (distance sensors return NaN),
	 * and with numSensors/2 of friction sensors placed on the specified distance 
	 * from agents body.
	 * 
	 * Parameter numSensors supports even numbers > 4, the agent will use
	 * two sensors: distance (lines) and friction (rectangles). The agent
	 * will publish array of values of the following structure:
	 * [distanceSensorData, frictionSensorData, currentSpeed]
	 * and dimensions of data:
	 * [numSensors/2, numSensors/2, 1]
	 * 
	 * @param name
	 * @param numSensors number of agents sensors, see above
	 * @param frictionDist distance of friction sensors from agent
	 * @return
	 */
	public vivae.SpawnResponse spawnAgent(String name, int numSensors, float frictionDist);
	
	/**
	 * Will spawn agent with numSensors/2 of distance (obstacle) sensors
	 * and numSensors/2 of friction sensors. 
	 * 
	 * Parameter numSensors supports even numbers > 4, the agent will use
	 * two sensors: distance (lines) and friction (rectangles). The agent
	 * will publish array of values of the following structure:
	 * [distanceSensorData, frictionSensorData, currentSpeed]
	 * and dimensions of data:
	 * [numSensors/2, numSensors/2, 1]
	 * 
	 * Example of use:
	 * spawnAgent("test",8,10,30); 
	 * will spawn agent with:
	 * -4 distance sensors measuring distance to 
	 * obstacle with the range maxDist,
	 * -4 friction sensors placed frictionDist from the agents body
	 * -1 sensor of actual speed 
	 * 
	 * @param name
	 * @param numSensors number of sensors
	 * @param frictionDist range of friction sensors
	 * @param maxDist maximum distance of obstacle sensors
	 * @return
	 */
	public vivae.SpawnResponse spawnAgent(String name, int numSensors, float frictionDist, float maxDist);

}
