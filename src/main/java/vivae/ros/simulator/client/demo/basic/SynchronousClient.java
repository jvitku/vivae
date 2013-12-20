package vivae.ros.simulator.client.demo.basic;

import java.io.IOException;

import org.ros.concurrent.CancellableLoop;
import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;

import ctu.nengoros.service.synchornous.SynchronousService;
import vivae.ros.simulator.server.SimCommands;
import vivae.ros.simulator.server.SimulatorServer;

/**
 * -Run the roscore
 * -RUn the class SimulatorServer, e.g.:
 * 
 * 		./run vivae.ros.simulator.server.SimulatorServer
 * 
 * -Run this class:
 * 
 * 		./run vivae.ros.simulator.client.demo.basic.SynchronousClient
 *  
 * -Press enter and this thing will request loading vivae with selected map from the SimulatorServer.
 * 
 * Here, the requests are processes asynchronously (request is sent and control is passed back from the method).
 *  
 * Note that synchronous usage of services is strongly advised, for demo on this @see SynchronousClient .

 * 
 * @author Jaroslav Vitku
 *
 */
public class SynchronousClient extends AbstractNodeMain {

	private final String me = "AsynchronousClient: ";

	/**
	 * default name of the Node
	 */
	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("myPublisher");
	}

	SynchronousService<vivae.LoadMapRequest,vivae.LoadMapResponse> map;
	SynchronousService<vivae.SimControllerRequest,vivae.SimControllerResponse> controls;
	
	// names of some known maps..
	String[] names = new String[]{"data/scenarios/arena1.svg", "data/scenarios/arena2.svg", "data/scenarios/ushape.svg" };

	
	@Override
	public void onStart(final ConnectedNode connectedNode) {

		// try to subscribe to the service for requesting the maps..		
		try {
			// service for map loading
			ServiceClient<vivae.LoadMapRequest, vivae.LoadMapResponse> mapServiceClient =
					connectedNode.newServiceClient(SimulatorServer.srvLOAD, vivae.LoadMap._TYPE);
			
			// service for controlling the simulation
			ServiceClient<vivae.SimControllerRequest, vivae.SimControllerResponse> simServiceClient =
					connectedNode.newServiceClient(SimulatorServer.srvCONTROL, vivae.SimController._TYPE);

			controls = new SynchronousService<vivae.SimControllerRequest, vivae.SimControllerResponse>(simServiceClient);
			map      = new SynchronousService<vivae.LoadMapRequest, vivae.LoadMapResponse>(mapServiceClient);
			
		} catch (ServiceNotFoundException e) {
			throw new RosRuntimeException(e);
		}

		// create classical while(true) loop, but this loop can be cancelled by the others..
		connectedNode.executeCancellableLoop(new CancellableLoop() {
			private int poc;
			
			@Override
			protected void setup() {
				poc = 0;
			}

			@Override
			protected void loop() throws InterruptedException {
				System.out.println(me+" --------- press any key to request a map and start new simulation");
				try {
					System.in.read();
				} catch (IOException e) { e.printStackTrace(); }
				System.out.println("requesting this: "+names[poc]);

				System.out.println("---------------\nMap Request Calling ....");
				loadMap(names[poc]);
				System.out.println(".... map loaded\n---------");
				
				System.out.println("smulation staaar....");
				startSimulation();
				System.out.println("...ted");
				System.out.println(me+"eaiting 5 seconds and then stopping the simulation");
				Thread.sleep(5000);
				
				System.out.println("simulation stoooooop...");
				stopSimulation();
				System.out.println("...ped");
				destroySimulation();
				
				if(++poc>names.length-1)
					poc =0;
			}
		});
	}
	
	public void loadMap(String name){
		vivae.LoadMapRequest req = map.getRequest();
		req.setName(name);
		map.callService(req);

	}
	
	public void startSimulation(){
		vivae.SimControllerRequest req = controls.getRequest();
		req.setWhat(SimCommands.START);
		controls.callService(req);
	}
	
	public void stopSimulation(){
		vivae.SimControllerRequest req = controls.getRequest();
		req.setWhat(SimCommands.STOP);
		controls.callService(req);
	}
	
	public void destroySimulation(){
		vivae.SimControllerRequest req = controls.getRequest();
		req.setWhat(SimCommands.DESTROY);
		controls.callService(req);
	}
}
