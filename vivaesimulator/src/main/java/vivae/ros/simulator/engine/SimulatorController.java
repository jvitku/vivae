package vivae.ros.simulator.engine;

import java.util.HashMap;

import vivae.ros.simulator.engine.agents.RosAgent;

/**
 * Controls the state of the simulator. 
 * This class is mainly for testing how the simulator can 
 * be controlled over the ROS network.
 * 
 * @author Jaroslav Vitku
 */
public class SimulatorController{

	private final String me="[SimulatorControls] ";
	private volatile boolean running = false;
	private volatile boolean inited = false;
	private Simulation mySim;


	public void setMySimulator(Simulation s){
		mySim = s;
	}

	public boolean isRunning() {
		return running;
	}

	public boolean start() {
		if(!inited){
			init();
		}
		if(running){
			System.err.println(me+"I am already running");
			return false;
		}
		running = true;
		System.out.println(me+"OK, starting simulation");
		
		//TODO: @see myReadme.txt
		//HashMap<String, RosAgent> map =((AgentRegisteringSimulation)mySim).getAgentMap();
		//mySim.init();
		//((AgentRegisteringSimulation)mySim).reregisterAgents(map);
		return mySim.startSimulation();
	}

	public boolean stop(){
		if(!running){
			System.err.println(me+"Simulator already stopped");
			return false;
		}
		running = false;
		return mySim.stopSimulation();
	}

	public boolean isInited() {	return inited; }

	public boolean init() {
		boolean result = mySim.init();
		this.inited = result;
		return result;
	}

	/**
	 * stop the simulation and close the simulation window
	 */
	public boolean destroy(){
		boolean result = mySim.destroy();
		running = false;
		inited = false;
		return result;
	}

	/**
	 * Resets the simulation to the original state, that is:
	 * 	-close the current simulation
	 *  -reload new simulation with the current map
	 *  -register all agents again with the same ROS connections
	 *  -start simulation
	 *  
	 *  if some of the above tasks failed, return false
	 */
	public boolean reset(){
		
		System.out.println(me+"Reseting the simulation!");
		boolean visibility = mySim.isVisible();
		this.stop();
		
		if(!(mySim instanceof AgentRegisteringSimulation)){
			System.err.println(me+"Cannot re-register agents: simulation does not support this");
			return false;
		}
		HashMap<String, RosAgent> map =((AgentRegisteringSimulation)mySim).getAgentMap();
		
		while(this.isRunning()){
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) { e.printStackTrace(); }
			System.out.println(me+"waiting until arena stops");
		}
		if(!this.destroy())
			return false;
		
		if(!mySim.setVisible(visibility))
			return false;
		
		if(!this.init())
			return false;
		
		((AgentRegisteringSimulation)mySim).reregisterAgents(map);
		
		return this.start();
	}
	
	
	public boolean setVisible(boolean visible){
		return mySim.setVisible(visible);
	}
}
