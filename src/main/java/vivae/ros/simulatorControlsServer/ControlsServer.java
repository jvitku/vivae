package vivae.ros.simulatorControlsServer;

import org.ros.exception.ServiceException;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceResponseBuilder;
import vivae.LoadMapRequest;
import vivae.LoadMapResponse;
import vivae.SimControllerRequest;
import vivae.SimControllerResponse;
import vivae.ros.simulator.AgentRegisteringSimulation;
import vivae.ros.simulator.Simulation;
import vivae.ros.simulator.SimulatorController;
import vivae.ros.simulator.agents.RosAgent;
import vivae.ros.simulator.impl.VivaeSimulatorOne;

/**
 * 
 * This is the main entry point for controlling the Vivae simulation over the ROS 
 * network. This node registers into the network and provides two main services:
 * loading map and simulation controls (start, stop, init, destroy).
 * 
 * Also, before the simulation starts, user can add as many agents as is in the 
 * loaded environment.
 * 
 * So in order to use the Vivae over the network, RosRun this node (server) and 
 * then RosRun another node (or smart neuron from Nengo script) in order to 
 * control the simulation. 
 * 
 * @author Jaroslav Vitku
 */
public class ControlsServer extends AbstractNodeMain {

	public static final String [] COMMANDS = new String[]{
		"init", 
		"start",
		"stop", 
		"reset",
		"destroy",
		"setvisible",
		"setinvisible"};
	
	public static final String INIT = COMMANDS[0];
	public static final String START = COMMANDS[1];
	public static final String STOP = COMMANDS[2];
	public static final String RESET = COMMANDS[3];
	public static final String DESTROY = COMMANDS[4];
	public static final String SETVISIBLE = COMMANDS[5];
	public static final String SETINVISIBLE = COMMANDS[6];
	
	
	public static final java.lang.String spwn = "spawnService";
	public static final java.lang.String controlSrv = "simControlSerice";
	public static final java.lang.String loadSrv = "loadMapSerice";
	public static final java.lang.String v2n = "vivae2nengo";
	public static final java.lang.String n2v = "nengo2vivae";

	public final String me = "ControlsServer ";

	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("VivaeControlsServer");
	}

	@Override
	public void onStart(ConnectedNode connectedNode) {

		// here is selected actual simulator (this should be general case enough..)
		AgentRegisteringSimulation vs = new VivaeSimulatorOne();
		SimulatorController sc = vs.getController();

		// loading the maps to vivae server
		LoadMapServiceResponseBuilder mapSrb = new LoadMapServiceResponseBuilder(vs);
		connectedNode.newServiceServer(loadSrv, vivae.LoadMap._TYPE, mapSrb);

		// simulation controller over the ROS network
		SimControlServiceResponseBuilder srb = new SimControlServiceResponseBuilder(sc);
		connectedNode.newServiceServer(controlSrv, vivae.SimController._TYPE,srb);

		// agent spawner over the ROS network
		AgentSpawnServiceResponseBuilder asp = new AgentSpawnServiceResponseBuilder(vs,
				connectedNode);
		connectedNode.newServiceServer(spwn, vivae.Spawn._TYPE,asp);
	}

	private class AgentSpawnServiceResponseBuilder implements 
	ServiceResponseBuilder<vivae.SpawnRequest, vivae.SpawnResponse>{

		AgentFactory af;

		public AgentSpawnServiceResponseBuilder(AgentRegisteringSimulation sim,
				ConnectedNode connectedNode){
			af = new AgentFactory(sim, connectedNode);
		}

		@Override
		public void build(vivae.SpawnRequest req, vivae.SpawnResponse resp) 
				throws ServiceException {

			java.lang.String name = req.getName();	// get name of required agent
			int numSensors = req.getNumSensors();
			float maxDist = req.getMaxDistance();
			float frictionDist = req.getFrictionDistance();
			//System.out.println("Getting request to spawn this: "+name+" agent");

			// here we will add also speed sensor (last float)
			RosAgent a = af.buildAgent(name, numSensors+1, maxDist, frictionDist);

			if(a!= null){
				//System.out.println("Agent "+a.getName()+" spawned");
				resp.setSpawnedOK(true);
				resp.setName(a.getName());
				resp.setPubTopicName(a.getPubTopic());
				resp.setSubTopicName(a.getSubTopic());
				resp.setNumSensors(a.getSensoryDataLength());

			}else{
				System.out.println("Error spawning agent..");
				resp.setSpawnedOK(false);
				resp.setName(name);
				resp.setPubTopicName("-");
				resp.setSubTopicName("-");
			}
		}
	}


	private class SimControlServiceResponseBuilder implements 
	ServiceResponseBuilder<vivae.SimControllerRequest, vivae.SimControllerResponse>{

		private final SimulatorController sc;

		public SimControlServiceResponseBuilder(SimulatorController sc){
			this.sc = sc;
		}

		@Override
		public void build(SimControllerRequest req, SimControllerResponse resp)
				throws ServiceException {
			//System.out.println(me+"requested this: "+req.getWhat());

			if(req.getWhat().equalsIgnoreCase(INIT)){
				boolean result = sc.init();
				//System.out.println(me+"called request for init, responding, result: "+result);
				resp.setOk(result);

			}else if(req.getWhat().equalsIgnoreCase(START)){
				sc.start();
				//System.out.println(me+"called request for start, responding ok");
				resp.setOk(true);

			}else if(req.getWhat().equalsIgnoreCase(STOP)){
				sc.stop();
				//System.out.println(me+"called request for stop, responding ok");
				resp.setOk(true);

			}else if(req.getWhat().equalsIgnoreCase(DESTROY)){
				sc.destroy();
				//System.out.println(me+"called request for destroy, responding ok");
				resp.setOk(true);

			}else if(req.getWhat().equalsIgnoreCase(RESET)){
				sc.reset();
				resp.setOk(true);

			}else if(req.getWhat().equalsIgnoreCase(SETVISIBLE)){
				sc.setVisible(true);
				resp.setOk(true);	
			}else if(req.getWhat().equalsIgnoreCase(SETINVISIBLE)){
				sc.setVisible(false);
				resp.setOk(true);
			}else{
				System.err.println(me+"This request: \""+req.getWhat()+"\" on SimControlService "
						+ "not recognized, only the following commands are supported: \n"
						+"-----------\n"+getSupportedCommands()+"\n----------");
			}
		}
	}
	
	public String getSupportedCommands(){
		String out = COMMANDS[0];
		for(int i=1; i<COMMANDS.length; i++)
			out = out +"\n"+COMMANDS[i];
		return out;
	}

	private class LoadMapServiceResponseBuilder implements 
	ServiceResponseBuilder<vivae.LoadMapRequest, vivae.LoadMapResponse>{

		private final Simulation sim;

		public LoadMapServiceResponseBuilder(Simulation sim){
			this.sim = sim;
		}

		@Override
		public void build(LoadMapRequest req, LoadMapResponse resp)
				throws ServiceException {

			java.lang.String name = req.getName();	// get name of map
			//	System.out.println("Getting request to load this: "+name+" loading the map");

			if(sim.loadMap(name)){
				resp.setLoadedOK(true);

			}else{
				System.err.println("Vivae node: could not load map named: "+name);
				//System.out.println("My classpath is: "+)  directory
				System.out.println("--------------- Working Directory = " +
						System.getProperty("user.dir"));
				resp.setLoadedOK(false);
			}
		}
	}
}
