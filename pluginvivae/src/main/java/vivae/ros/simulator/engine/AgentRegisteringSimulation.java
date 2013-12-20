package vivae.ros.simulator.engine;

import java.util.HashMap;

import vivae.ros.simulator.engine.agents.RosAgent;

/**
 * This simulation is able to spawn new agents and add them into the simulation.
 * Currently only spawning agents BEFORE the simulation start is supported. 
 * Deleting agents also is not supported so far.
 * 
 * @author Jaroslav Vitku
 *
 */
public interface AgentRegisteringSimulation extends Simulation {

	public boolean canAddAgent(String name);
	
	/**
	 * Register agent in the simulation
	 * @param agent
	 */
	public void registerAgent(RosAgent agent);
	
	/**
	 * After reseting the arena (turn off & on) all agents
	 * have to be registered again
	 */
	public void reregisterAgents(HashMap<String, RosAgent> agents);
	
	/**
	 * could be called e.g. after start/stopping the simulation
	 */
	public void setAgentsReady();
	
	/**
	 * The map is deleted when destroying the simulation somewhere
	 */
	public HashMap<String,RosAgent> getAgentMap();
	
	/**
	 * TODO this is not supported so far
	 * @param name
	 */
	public void killAgent(String name);
	
}
