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
		
	void startSimulation();
	
	void stopSimulation();
	
	boolean init();
	
	void destroy();
	
	boolean loadMap(String path);
	
    public Arena getArena();

    public void setVisible(boolean visible);
    
    public boolean isVisible();
}
