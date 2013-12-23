package ctu.nengoros.modules.vivae;

import java.util.HashMap;

import ctu.nengoros.modules.AbsNeuralModule;

import org.ros.node.ConnectedNode;

import vivae.ros.simulator.client.impl.AgentSpawnSynchronousClient;
import ctu.nengoros.modules.impl.vivae.impl.SimpleControlledAgent;
import ca.nengo.model.StructuralException;

/**
 * This handles the Vivae simulation over the ROS network. 
 * The important things are:
 * 	-loadMap
 * 	-start
 * 	-stop
 *  -destroy
 *  
 *  -addAgent	 (adds Origin and Termination to the parent Neuron)
 *  -removeAgent (removes Origins and Terminations)
 *  
 * @author Jaroslav Vitku
 * 
 */
public class SimulationControls implements NengoSimulaitonClient{

	public static final String NAME = "SimulationControls";
	public final String me = "["+NAME+"] ";

	// map of agents in the network, do not try to add duplicate names please...
	private final HashMap<String,VivaeAgent> agents;

	public final int DEFNUMSENSORS = 2;
	public final float DEFFRD = -1;		// -1 means default value provided by SimulatorServer
	public final float DEFMAXDIST = -1;
	
	protected int slept;
	protected final int sleeptime = 10;
	protected final int maxSleep = 2000;	// max wait time to services to initialize

	AgentSpawnSynchronousClient serviceClient;

	// add decoders and encoders to this neuron
	private final AbsNeuralModule an;

	public SimulationControls(VivaeNeuralModule vivaeNeuralModule, ConnectedNode cn){
		//this.cn = cn;
		an = vivaeNeuralModule;
		agents = new HashMap<String, VivaeAgent>(3);

		serviceClient = new AgentSpawnSynchronousClient(cn);
	}

	private void add(String name, int numSensors, float maxDistance, float frictionSensor) 
			throws StructuralException{

		this.awaitStarted();

		if(agents.containsKey(name)){
			System.err.println(me+"already have agent with this name, names must be unique! "+name);
			throw new StructuralException(me+"already have agent with this name, names must be unique! "+name);
		}

		// spawn agent remotely in the ViVae simulator
		vivae.SpawnResponse resp = 
				serviceClient.spawnAgent(name, numSensors, frictionSensor, maxDistance);

		if(resp==null){
			System.out.println(me+"ERROR: could not spawn agent "+name+" probably network error");
			throw new StructuralException(me+"ERROR: could not spawn agent "+name+" probably network error");
		}

		if(!resp.getSpawnedOK()){
			System.out.println(me+"ERROR: Vivae denied to spawn this agent "+name);
			throw new StructuralException(me+"ERROR: Vivae denied to spawn this agent "+name);
		}

		if(agents.containsKey(resp.getName())){
			System.err.println(me+"local list of agents already contains this name, ignoring! "+resp.getName());
			throw new StructuralException(me+"local list of agents already contains this name, ignoring! "+
					resp.getName());
		}

		// read received information about the agent and create Nengo "communication interface" 
		String n = resp.getName();
		String p = resp.getPubTopicName();
		String s = resp.getSubTopicName();

		int sensoryDataLength = resp.getNumSensors();	
		
		// build the agent, register encoders and decoders, add it to the list of agents, we are done
		SimpleControlledAgent a = new SimpleControlledAgent(an, n, p, s, sensoryDataLength);
		agents.put(name, (VivaeAgent) a);			
		System.out.println(me+"agent named: "+n+" successfuly connected to both networks");
	}

	@Override
	public void addAgent(String name, int numSensors) throws StructuralException{
		this.add(name, numSensors, DEFMAXDIST, DEFFRD);
	}

	@Override
	public void addAgent(String name) throws StructuralException {
		this.add(name, DEFNUMSENSORS, DEFMAXDIST, DEFFRD);
	}

	@Override
	public void addAgent(String name, int numSensors, float maxDistance)
			throws StructuralException {
		
		this.add(name, numSensors, maxDistance, DEFFRD);

	}
	@Override
	public void addAgent(String name, int numSensors, float maxDistance,
			float frictionSensor) throws StructuralException {
		
		this.add(name, numSensors, maxDistance, frictionSensor);
	}
	
	@Override
	public void removeAgent(String name) {
		this.awaitStarted();

		if(!agents.containsKey(name)){
			System.err.println(me+"cannot find this agent in the map: "+name);
			return;
		}
		System.out.println(me+"removing agent named: "+name+" Note: this is TODO");
		//TODO delete terminations and origins..
		agents.remove(name);
	}

	@Override
	public boolean callLoadMap(String name) {
		this.awaitStarted();
		return serviceClient.callLoadMap(name);
	}

	@Override
	public boolean callStartSimulation() {
		this.awaitStarted();
		return serviceClient.callStartSimulation();
	}

	@Override
	public boolean callStopSimulation() {
		this.awaitStarted();
		return serviceClient.callStopSimulation();
	}

	@Override
	public boolean callDestroySimulation() {
		this.awaitStarted();
		return serviceClient.callDestroySimulation();
	}

	@Override
	public boolean callSetVisibility(boolean visible) {
		this.awaitStarted();
		return serviceClient.callSetVisibility(visible);
	}

	@Override
	public boolean callReset() {
		this.awaitStarted();
		return serviceClient.callReset();
	}

	private void awaitStarted(){
		slept=0;
		while(serviceClient==null){
			try {
				Thread.sleep(sleeptime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(sleeptime*slept++ >maxSleep){
				System.err.println(me+"my ServiceClient not started within " +
						"max. time of "+maxSleep+"ms, giving up !!!");
				return;
			}
		}
	}

	@Override
	public HashMap<String, VivaeAgent> getAgents() {
		return this.agents;
	}
}

