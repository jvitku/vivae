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
	 * @param name
	 * @return
	 */
	public vivae.SpawnResponse spawnAgent(String name);
	
	/**
	 * 
	 * @param name
	 * @param numSensors specify number of agents sensors
	 * @return
	 */
	public vivae.SpawnResponse spawnAgent(String name, int numSensors);
	
	/**
	 * 
	 * @param name
	 * @param numSensors number of agents sensors
	 * @param frictionDist specify range of agents friction sensors
	 * @return
	 */
	public vivae.SpawnResponse spawnAgent(String name, int numSensors, float frictionDist);
	
	/**
	 * 
	 * @param name
	 * @param numSensors number of sensors
	 * @param frictionDist range of friction sensors
	 * @param maxDist maximum distance of obstacle sensors
	 * @return
	 */
	public vivae.SpawnResponse spawnAgent(String name, int numSensors, float frictionDist, float maxDist);

}
