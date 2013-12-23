package vivae.ros.simulator.client;

import org.ros.exception.RosRuntimeException;

/**
 * This client is purposed to connect to the SimulatorServer and call 
 * controls of the simulator, e.g.:
 * 
 * -loadMap
 * -start
 * -stop
 * -reset
 * -spawn agent..
 * 
 * The synchronous version means that methods wait for response 
 * (until response is received or until time expires) and return the result
 * of their request. True means that request was processed ok.
 * 
 * @author Jaroslav Vitku
 *
 */
public interface SynchornousClient {

	/**
	 * This has to be called before any of other methods, this registers
	 * all services in the ROS network.
	 * 
	 * The method should be called from the onStart method or from the class constructor
	 * 
	 * @throws RosRuntimeException if some of my services was not found/connected
	 */
	//public void registerMyServices(ConnectedNode connectedNode) throws RosRuntimeException;
	
	public boolean callLoadMap(String name);

	public boolean callStartSimulation();

	public boolean callStopSimulation();
	
	public boolean callDestroySimulation();

	public boolean callSetVisibility(boolean visible);


}

