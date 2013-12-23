package vivae.ros.simulator.engine;

import vivae.arena.Arena;
import vivae.ros.simulator.server.ExternalSimulation;


/**
 * Basic is externalSimulation, this can be used either locally or over the ROS network
 * provided by the SimulatorServer.
 * 
 * This is extension for local use.
 * 
 * @author Jaroslav Vitku
 *
 */
public interface Simulation extends ExternalSimulation{
	
	public SimulatorController getController();
		
    public Arena getArena();

    public boolean isVisible();
}
