package vivae.ros.simulator.engine;

import vivae.arena.Arena;


/**
 * All simulations should be controlled through the simulation controller..
 * 
 * @author Jaroslav Vitku
 *
 */
public interface Simulation {
	
	public SimulatorController getController();
		
	public boolean startSimulation();
	
	public boolean stopSimulation();
	
	public boolean init();
	
	public boolean destroy();
	
	public boolean loadMap(String path);
	
    public Arena getArena();

    public boolean setVisible(boolean visible);
    
    public boolean isVisible();
}
