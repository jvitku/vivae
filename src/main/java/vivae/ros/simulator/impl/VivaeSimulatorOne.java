package vivae.ros.simulator.impl;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;

import vivae.arena.Arena;
import vivae.arena.parts.Active;
import vivae.controllers.VivaeController;
import vivae.example.FRNNControlledRobot;
import vivae.fitness.FitnessFunction;
import vivae.ros.simulator.SimulatorController;
import vivae.ros.simulator.AgentRegisteringSimulation;
import vivae.ros.simulator.agents.RosAgent;
import vivae.ros.util.MapLoader;
import vivae.util.FrictionBuffer;

/**
 * Starts with empty list of agents, agents can be added from external source.
 * Each agent can be controlled across the ROS network.
 * 
 * Basic workflow:
 * 
 * 	-start SimulatorServer
 * 	-request loading the map
 * 	-add agents
 * 	-start simulation
 * 	-stop the simulation
 * 	-destroy the resources
 *  
 * 
 * @author Jaroslav Vitku
 *
 */
public class VivaeSimulatorOne implements AgentRegisteringSimulation{

	public final String me = "[VivaeSimulator] ";

	private String path;
	private boolean pathFound = false;

	private boolean visibility;

	FitnessFunction mot, avg;
	Thread arenaThread;

	// things copied from MyExperiment
	public Arena arena = null;
	JFrame f = null;
	Vector<Active> agents = null;

	// map of agents that are already spawned
	HashMap<String, RosAgent> agentMap;	

	// call this to control the simulation run please..
	public final SimulatorController sc;

	public VivaeSimulatorOne(){
		sc = new SimulatorController();
		sc.setMySimulator(this);
	}

	/**
	 * Just start the simulation asynchronously in a new Thread
	 */
	@Override
	public void startSimulation() {
		arenaThread = new Thread(arena);
		arenaThread.start();
		//  System.out.println("average speed fitness = "+ avg.getFitness());
		// System.out.println("average ontop fitness = "+ mot.getFitness());
	}

	@Override
	public void stopSimulation() {
		arena.shouldStop();
		System.out.println(me+"stopping, waiting for arena to stop..");
		try {
			arenaThread.join();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		System.out.println(me+"arena stopped OK");
	}

	/**
	 * Method init here just creates arena, waiting for adding the agents.
	 */
	@Override
	public boolean init() {

		if(!pathFound){
			try {
				this.path = MapLoader.locateMap(MapLoader.DEF_MAP);
				System.out.println(me+"Loading the default map named: "+MapLoader.DEF_MAP);
			} catch (FileNotFoundException e) {
				//e.printStackTrace();
				System.err.println(me+"Not even default map could be found!! Will not start simulation!");
				return false;
			}
		}
		createArena(path,visibility);
		arena.setInfiniteSimulation();				// do not stop automatically (wait for external signal)
		agentMap = new HashMap<String, RosAgent>(3);	// prepare the storage for agents	
		return true;

		/*

		if(!pathFound){
			if(!DataLoader.fileCanBeLocated(defPath)){
				System.err.println(me+"even the default map file not found, will not init!");
				return false;
			}
			path = defPath;
		}

	createArena(DataLoader.locateFile(path),visibility);
	arena.setInfiniteSimulation();				// do not stop automatically (wait for external signal)
	agentMap = new HashMap<String, RosAgent>(3);	// prepare the storage for agents	
	return true;
		 */
	}

	@Override
	public SimulatorController getController() { return sc;	}

	@Override
	public boolean loadMap(String path) {
		try {
			this.path = MapLoader.locateMap(path);
			this.pathFound = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
		/*

		// file cannot be found?
		if(!DataLoader.fileCanBeLocated(path)){
			System.err.println(me+"file containing the map "+path+" NOT FOUND! ");
			pathFound = false;
			return false;
		}
		this.path = path;
		//System.out.println(me+"map "+path+" exists, remembered..");
		pathFound = true;
		return true;
		 */
	}

	@Override
	public Arena getArena() {
		return arena;
	}

	private void createArena(String svgFilename, boolean visible) {
		if (visible) {
			f = new JFrame("Vivae Simulation Server");

			arena = new Arena(f);
			System.out.println("will load scenario on "+svgFilename);

			// here is the problem , in SVGLoader.. new Thread .join..
			arena.loadScenario(svgFilename);

			arena.setAllArenaPartsAntialiased(true);
			f.setBounds(50, 0, arena.getScreenWidth(), arena.getScreenHeight() + 30);
			f.setResizable(false);
			//	f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			f.getContentPane().add((JPanel)arena);

			f.setVisible(visible);
			arena.setVisibility(visible);

		} else {
			arena = new Arena(f);
			arena.loadScenario(svgFilename);
			arena.setVisibility(false);
			arena.setLoopSleepTime(0);
		}
		arena.setFrictionBuffer(new FrictionBuffer(arena));
		agents = arena.getActives();
		//System.out.println("Arena: loaded this nummer of agents: "+agents.size());
	}

	@Override
	public void destroy() {
		System.out.println(me+"releasing all resources");
		if(f!=null)
			f.dispose();
		arena.setVisible(false);
		arena = null;	
	}


	/**
	 * Setups the keyboard controlled agent 
	 * @param number how many sensors/2
	 * @param maxDistance if maxDistance==0, then no distance sensors are added
	 * @param frictionDistance if frictionDisrance==0, no friction sensors added
	 */
	private void setupROSAgent(RosAgent a, int number) {
		Active agent = agents.get(number);
		int snum = a.getNumSensors()-1;	// the more of this, the more sensors  (it is 2*numSensors+1)
		double sd = a.getMaxSensorDistance();
		double fd = a.getFrictionDistance();

		double sangle = -Math.PI / 2;
		double ai = Math.PI / (snum / 2 - 1);

		/// bad class hierarchy :(
		arena.registerController(agent, (VivaeController)a);	// register agent (controller) in the arena

		if (agent instanceof FRNNControlledRobot) {
			// the old way:
			((FRNNControlledRobot) agent).setSensors(snum / 2, sangle, ai, sd, fd);
			/*
			 TODO make this work:
			if(sd != 0)
				((FRNNControlledRobot) agent).setDistanceSensors(snum / 2, sangle, ai, sd);
			if(fd != 0)
				((FRNNControlledRobot) agent).setFrictionSensors(snum / 2, sangle, ai, fd);
			 */

		}
		int dataLen = snum/2;
		a.setSensoryDataLength(2*dataLen+1);	// distance and friction sensors
		/*
		if(sd==0 && fd==0){
			System.err.println("agents with no sensors not tested with ROS");
			a.setSensoryDataLength(0);	
		}else if(sd==0 || fd==0){
			a.setSensoryDataLength(dataLen);	
		}else{
			a.setSensoryDataLength(2*dataLen);	// distance and friction sensors
		}*/
	}

	public boolean canAddAgent(String name){
		if(!sc.isInited())
			sc.init();
		if(agentMap.containsKey(name))
			return false;
		if(agentMap.size() >= agents.size())
			return false;
		return true;
	}

	@Override
	public void reregisterAgents(HashMap<String, RosAgent> map){

		Iterator<Entry<String, RosAgent>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String,RosAgent> pairs = (Map.Entry<String,RosAgent>)it.next();
			this.registerAgent(pairs.getValue());
			pairs.getValue().reset();	// reset each agents state here
			it.remove(); 				// avoids a ConcurrentModificationException
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public HashMap<String, RosAgent> getAgentMap() {
		return (HashMap<String, RosAgent>) agentMap.clone();
	}

	@Override
	public void registerAgent(RosAgent agent) {
		if(!canAddAgent(agent.getName())){
			System.err.println("vivae: agent with this name already registered!");
			return;
		}
		setupROSAgent(agent, agentMap.size());
		agentMap.put(agent.getName(), agent);
	}

	@Override
	public void killAgent(String name) {
		// TODO Auto-generated method stub
		System.err.println("Simulation here: killing the Agents is not supported so far");
	}

	@Override
	public void setVisible(boolean visible){
		this.visibility = visible;
	}

	@Override
	public boolean isVisible() {
		return this.visibility;
	}

	@Override
	public void setAgentsReady() {
		Iterator<Entry<String, RosAgent>> it = agentMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String,RosAgent> pairs = (Map.Entry<String,RosAgent>)it.next();
			pairs.getValue().reset();	// reset each agents state here (reset speeds and set ready)
			//it.remove(); 				// avoids a ConcurrentModificationException
		}
	}
}
