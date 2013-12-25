package ctu.nengoros.modules.vivae;


import java.util.HashMap;

import ca.nengo.model.StructuralException;
import vivae.ros.simulator.client.SynchornousClient;

/**
 * This integrates Viave simulator into the Nengoros. It is responsible for 
 * interacting with the Vivae simulator from the Nengoros. 
 *  
 * Specifically, it supports these operations:
 * 
 * -spawning agents in the Vivae simulator
 * -registering these agents in the Nengo network 
 * 		that is adding origins/terinations and throwing StructuralExceptions
 * 
 * -general controlling of Vivae simulator from the Nengo
 * 		that is callSimulationStart/stop/loadMap..
 * 
 * 
 * 
 * @author Jaroslav Vitku
 *
 */
public interface NengoSimulaitonClient extends SynchornousClient{

	/**
	 * Spawn agent in the Vivae with default values, which publishes/receives 
	 * data over the ROS network. Add the corresponding origin+termination to 
	 * the VivaeNauralModule modem and therefore register it into the Nengo network. 
	 * 
	 * After successful return of this method, agent should be ready and available in both simulators. 
	 *  
	 * @param name
	 * @throws StructuralException thrown mainly when the Nengo VivaeNeuralModule has already origin/termination registered din the network  
	 */
	public void addAgent(String name) throws StructuralException;
	
	/**
	 * @param name
	 * @param numSensors 
	 * @throws StructuralException
	 */
	public void addAgent(String name, int numSensors) throws StructuralException;
	
	public void addAgent(String name, int numSensors, float maxDistance) throws StructuralException;
	
	public void addAgent(String name, int numSensors, float maxDistance, float frictionSensor) 
			throws StructuralException;
	
	public void removeAgent(String name);
	
	/**
	 * Returns map of all agents registered by this client.
	 * @return
	 */
	public HashMap<String, VivaeAgent> getAgents();

}
