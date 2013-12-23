package vivae.ros.simulator.client;


/**
 * This client is purposed to connect to the SimulatorServer and call 
 * controls of the simulator, e.g.:
 * 
 * -loadMap
 * -start
 * -stop
 * -reset (destroys the current simulation and starts the new one)
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

	public boolean callLoadMap(String name);

	public boolean callStartSimulation();

	public boolean callStopSimulation();
	
	public boolean callDestroySimulation();

	public boolean callSetVisibility(boolean visible);
	
	public boolean callReset();

}

