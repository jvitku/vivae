package vivae.ros.simulatorServer;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;

import vivae.ros.simulator.AgentRegisteringSimulation;
import vivae.ros.simulator.SimulatorController;
import vivae.ros.simulator.impl.VivaeSimulatorOne;
import vivae.ros.simulatorServer.services.AgentSpawnServiceResponseBuilder;
import vivae.ros.simulatorServer.services.LoadMapServiceResponseBuilder;
import vivae.ros.simulatorServer.services.SimControlServiceResponseBuilder;

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
public class SimulatorServer extends AbstractNodeMain {

	// ROS topic names of provided services
	public static final java.lang.String srvSPAWN = "spawnService";
	public static final java.lang.String srvCONTROL = "simControlSerice";
	public static final java.lang.String srvLOAD = "loadMapSerice";
	
	public static final java.lang.String V2N = "vivae2nengo";
	public static final java.lang.String N2V = "nengo2vivae";


	public final String me = "SimulatorServer ";

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
		connectedNode.newServiceServer(srvLOAD, vivae.LoadMap._TYPE, mapSrb);

		// simulation controller over the ROS network
		SimControlServiceResponseBuilder srb = new SimControlServiceResponseBuilder(sc);
		connectedNode.newServiceServer(srvCONTROL, vivae.SimController._TYPE,srb);

		// agent spawner over the ROS network
		AgentSpawnServiceResponseBuilder asp = new AgentSpawnServiceResponseBuilder(vs,
				connectedNode);
		connectedNode.newServiceServer(srvSPAWN, vivae.Spawn._TYPE,asp);
	}


}
