package vivae.ros.simulator.demo.keycontrolled;

import java.io.FileNotFoundException;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;

import vivae.arena.Arena;
import vivae.arena.parts.Active;
import vivae.controllers.KeyboardVivaeController;
import vivae.example.FRNNControlledRobot;
import vivae.example.FRNNController;
import vivae.fitness.AverageSpeed;
import vivae.fitness.FitnessFunction;
import vivae.fitness.MovablesOnTop;
import vivae.ros.simulator.Simulation;
import vivae.ros.simulator.SimulatorController;
import vivae.ros.util.DataLoader;
import vivae.ros.util.MapLoader;
import vivae.util.FrictionBuffer;
import vivae.util.Util;

/**
 * This starts the simulation with one manually controlled agent (0),
 * the rest two with random weights.
 * 
 * Does not use ROS.
 * 
 * @author Jaroslav Vitku
 *
 */
public class KeyControlledVivaeSimulator implements Simulation{

	public final String me = "VivaeSimulator ";

	private final String defPath = "data/scenarios/arena1.svg";
	private String path;
	private boolean pathFound = false;

	FitnessFunction mot, avg;
	Thread arenaThread;

	// things copied from MyExperiment
	public Arena arena = null;
	JFrame f = null;
	Vector<Active> agents = null;
//	HashMap<String, Agent> agentMap;
	
	// call this to control the simulation run please..
	public final SimulatorController sc;

	public KeyControlledVivaeSimulator(){
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
	 * load map, make other initialization-like things and return true
	 * if all OK
	 */
	@Override
	public boolean init() {
		System.out.println(me+"loading the simulation.. ");
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
		createArena(path,true);
		/*
		if(!pathFound){
			if(!DataLoader.fileCanBeLocated(defPath)){
				System.err.println(me+"even the default map file not found, will not init!");
				return false;
			}
			path = defPath;
		}
		createArena(DataLoader.locateFile(path),true);
		*/
		// random weight matrices as 3D array
		// 3 robots,
		int sensors=5; // 5 for distance and 5 for surface
		int neurons=2;
		
		int robots=2;

		double[][][] wm = Util.randomArray3D(robots,neurons,2*sensors+neurons+1,-5,5);
		setupExperimentII(wm,50,25);
		mot  = new MovablesOnTop(arena);//initialize fitness
		avg = new AverageSpeed(arena);
		
		arena.setInfiniteSimulation();	// do not stop automatically (wait for external signal)
		return true;
	}

	@Override
	public SimulatorController getController() { return sc;	}

	@Override
	public boolean loadMap(String path) {
		/*
		// file cannot be found?
		if(!DataLoader.fileCanBeLocated(path)){
			System.err.println(me+"file containing the map "+path+" NOT FOUND! ");
			pathFound = false;
			return false;
		}
		this.path = path;
		System.out.println(me+"map "+path+" exists, remembered..");
		pathFound = true;
		return true;*/
		try {
			this.path = MapLoader.locateMap(path);
			this.pathFound = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public Arena getArena() {
		return arena;
	}

	private void createArena(String svgFilename, boolean visible) {
		if (visible) {
			f = new JFrame("Third Experiment");
			arena = new Arena(f);
			arena.loadScenario(svgFilename);
			arena.setAllArenaPartsAntialiased(true);
			f.setBounds(50, 0, arena.getScreenWidth(), arena.getScreenHeight() + 30);
			f.setResizable(false);
			f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
		System.out.println("loaded this num of agents: "+agents.size());
	}

	public void setupExperimentII(double[][][] wm, double maxDistance, double frictionDistance) {
		
		// setup key controlled one
		setupAgentK(0,maxDistance, frictionDistance);
		
		// setup the rest of agents
		int agentnum = agents.size();
		for (int i = 1; i < agentnum; i++) {
			setupAgent(i, wm[i % wm.length], maxDistance, frictionDistance);
		}
	}
	
	/**
	 * Setups the keyboard controlled agent 
	 * @param number
	 * @param maxDistance
	 * @param frictionDistance
	 */
	public void setupAgentK(int number, /*double[][] wm,*/ double maxDistance, double frictionDistance) {
		Active agent = agents.get(number);
		int snum = 15;	// the more, the more sensors  (2*numSensors)
		double sangle = -Math.PI / 2;
		double ai = Math.PI / (snum / 2 - 1);
		KeyboardVivaeController kc = new KeyboardVivaeController();
		arena.registerController(agent, kc);	// register controller in the arena
		if (agent instanceof FRNNControlledRobot) {
			System.out.println("Registering keyboard controlled agent");
			((FRNNControlledRobot) agent).setSensors(snum / 2, sangle, ai, maxDistance, frictionDistance);
		}
	}

	/**
	 *
	 * @param number number of the agent/robot
	 * @param wm composed weight matrix of size neurons*(inputs+neurons+1)
	 * @param maxDistance maximum distance of distance sensors
	 * @param frictionDistance distance of friction point sensors
	 *
	 * Number of sensors as well as number of neurons is determined from the size
	 * of the weight matrix. You can use either this function called number of agents time, or
	 * use setupExperiment function, which distributes the weight matrices evenly.
	 */
	private void setupAgent(int number, double[][] wm, double maxDistance, double frictionDistance) {
		Active agent = agents.get(number);
		int neurons = wm.length;
		int snum = (wm[0].length - neurons - 1);
		double sangle = -Math.PI / 2;
		double ai = Math.PI / (snum / 2 - 1);
		FRNNController frnnc = new FRNNController();
		frnnc.initFRNN(Util.subMat(wm, 0, snum - 1),
				Util.subMat(wm, snum, snum + neurons - 1),
				Util.flatten(Util.subMat(wm, snum + neurons, snum + neurons)));
		arena.registerController(agent, frnnc);
		if (agent instanceof FRNNControlledRobot) {
			((FRNNControlledRobot) agent).setSensors(snum / 2, sangle, ai, maxDistance, frictionDistance);
		}
	}

	@Override
	public void destroy() {
		System.out.println(me+"releasing all resources");
		f.dispose();
	}
	

	@Override
	public void setVisible(boolean visible) {
		arena.setVisibility(visible);
	}
	

	@Override
	public boolean isVisible() {
		return arena.isVisible;
	}

}
