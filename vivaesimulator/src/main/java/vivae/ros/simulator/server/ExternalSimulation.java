package vivae.ros.simulator.server;

/**
 * These services should be provided by the SimulatorServer. 
 * 
 * But the interface which should be used is: vivae.simulator.client.SynchronousClient
 * or vivae.simulator.client.AgentSpawningSynchronousClient
 *  
 * 
 * @author Jaroslav Vitku
 *
 */
public interface ExternalSimulation {

	public boolean startSimulation();

	public boolean stopSimulation();

	public boolean init();

	public boolean destroy();

	public boolean loadMap(String path);

	public boolean setVisible(boolean visible);

}
