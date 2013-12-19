package vivae.ros.simulatorControlsServer.demo;

import java.io.IOException;
import org.ros.concurrent.CancellableLoop;
import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import vivae.ros.simulatorControlsServer.ControlsServer;

/**
 * Press enter and this thing will request loading vivae with selected map.
 * 
 * Note: this sends requests to vivae simulator: ControlsServer, 
 * so this must be running in the ROS network (e.g. in Nengo)
 * 
 * @author Jaroslav Vitku
 *
 */
public class MySynchronousRequester extends AbstractNodeMain {

	private final String me = "MyRequester: ";

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
					connectedNode.newServiceClient(ControlsServer.loadSrv, vivae.LoadMap._TYPE);
			
			// service for controlling the simulation
			ServiceClient<vivae.SimControllerRequest, vivae.SimControllerResponse> simServiceClient =
					connectedNode.newServiceClient(ControlsServer.controlSrv, vivae.SimController._TYPE);

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

				// set the request and call the service server (SYNchornously)
				vivae.LoadMapRequest req = map.getRequest();
				req.setName(names[poc]);
				System.out.println("map caaaaaaaaal....");
				map.callService(req);
				System.out.println("....ed");
				
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
	
	public void startSimulation(){
		vivae.SimControllerRequest req = controls.getRequest();
		req.setWhat("start");
		controls.callService(req);
	}
	
	public void stopSimulation(){
		vivae.SimControllerRequest req = controls.getRequest();
		req.setWhat("stop");
		controls.callService(req);
	}
	
	public void destroySimulation(){
		vivae.SimControllerRequest req = controls.getRequest();
		req.setWhat("destroy");
		controls.callService(req);
	}
}
