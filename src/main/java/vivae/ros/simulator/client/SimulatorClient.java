package vivae.ros.simulator.client;

/**
 * This client is purposed to connect to the SimulatorServer and request control the 
 * simulation, e.g.:
 * 
 * -loadMap
 * -start
 * -stop
 * -reset
 * -spawn agent..
 * 
 * @author Jaroslav Vitku
 *
 */
public interface SimulatorClient {
	
	public void callRequestMap(String name);

	public void callStartSimulation();

	public void callStopSimulation();
	
	public void callDestroySimulation();

	public void callSetVisible(boolean visible);

}

