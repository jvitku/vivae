package vivae.ros.simulatorControlsServer.demo;

import java.io.IOException;
import org.ros.concurrent.CancellableLoop;
import org.ros.exception.RemoteException;
import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;
import vivae.LoadMapResponse;
import vivae.SimControllerResponse;
import vivae.ros.simulatorControlsServer.ControlsServer;

/**
 * -Run the roscore
 * -RUn the class ControlsServer, e.g.:
 * 		./run vivae.ros.simulatorControlsServer.ControlsServer
 * -Run this class:
 * 		./run vivae.ros.simulatorControlsServer.demo.MyRequester
 *  
 * -Press enter and this thing will request loading vivae with selected map from the ControlsServer.
 * 
 * @author Jaroslav Vitku
 *
 */
public class MyRequester extends AbstractNodeMain {

	private final String me = "MyRequester: ";

	/**
	 * default name of the Node
	 */
	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("myPublisher");
	}

	ServiceClient<vivae.LoadMapRequest, vivae.LoadMapResponse> mapServiceClient; // load maps
	ServiceClient<vivae.SimControllerRequest, vivae.SimControllerResponse> simServiceClient; // control the simulation

	// names of some known maps..
	String[] names = new String[]{"data/scenarios/arena1.svg", "data/scenarios/arena2.svg", "data/scenarios/ushape.svg" };

	SimControlServiceResponseListener simSrl;
	
	
	@Override
	public void onStart(final ConnectedNode connectedNode) {

		// try to subscribe to the service for requesting the maps..		
		try {
			mapServiceClient = connectedNode.newServiceClient(ControlsServer.loadSrv, vivae.LoadMap._TYPE);
			simServiceClient = connectedNode.newServiceClient(ControlsServer.controlSrv, vivae.SimController._TYPE);

		} catch (ServiceNotFoundException e) {
			throw new RosRuntimeException(e);
		}

		// create classical while(true) loop, but this loop can be cancelled by the others..
		connectedNode.executeCancellableLoop(new CancellableLoop() {
			private int poc;
			ServiceResponseListener<vivae.LoadMapResponse> srl;
			
			@Override
			protected void setup() {
				poc = 0;
				srl = new MyServiceResponseListener();
				simSrl = new SimControlServiceResponseListener();
			}

			@Override
			protected void loop() throws InterruptedException {
				System.out.println(me+" --------- press any key to request a map and start new simulation");
				try {
					System.in.read();
				} catch (IOException e) { e.printStackTrace(); }
				System.out.println("requesting this: "+names[poc]);

				// set the request and call the srvice server (asynchronously)
				final vivae.LoadMapRequest req = mapServiceClient.newMessage();
				req.setName(names[poc]);
				mapServiceClient.call(req, srl);
				
				startSimulation();
				System.out.println(me+"eaiting 5 seconds and then stopping the simulation");
				Thread.sleep(5000);
				
				stopSimulation();
				destroySimulation();
				
				if(++poc>names.length-1)
					poc =0;
			}
		});
	}
	
	
	public void startSimulation(){

		final vivae.SimControllerRequest req = simServiceClient.newMessage();
		req.setWhat("start");
		// set reques for starting the simulation
		simServiceClient.call(req, simSrl);
		
	}
	
	public void stopSimulation(){

		final vivae.SimControllerRequest req = simServiceClient.newMessage();
		req.setWhat("stop");
		// set reques for starting the simulation
		simServiceClient.call(req, simSrl);
		
	}
	
	public void destroySimulation(){

		final vivae.SimControllerRequest req = simServiceClient.newMessage();
		req.setWhat("destroy");
		// set reques for starting the simulation
		simServiceClient.call(req, simSrl);
	}
	

	/**
	 * Request these things from the vivae simulator:
	 * init
	 * start 
	 * stop
	 * destroy
	 * 
	 * @author Jaroslav Vitku
	 *
	 */
	private class SimControlServiceResponseListener implements ServiceResponseListener<vivae.SimControllerResponse>{

		@Override
		public void onFailure(RemoteException e) {
			System.err.println(me+"request failed..");
		}

		@Override
		public void onSuccess(SimControllerResponse resp) {
			boolean ok = resp.getOk();
			System.out.println("vivae says that my request was executed: "+ok);
		}
	}


	/**
	 * Request for loading map by filename
	 * @author Jaroslav Vitku
	 */
	private class MyServiceResponseListener implements 
	ServiceResponseListener<vivae.LoadMapResponse> {

		public MyServiceResponseListener(){
		}

		@Override
		public void onFailure(RemoteException e) {
			throw new RosRuntimeException(e);
		}

		@Override
		public void onSuccess(LoadMapResponse resp) {
			boolean ok = resp.getLoadedOK();
			System.out.println("vivae says that it was laoded " +ok);
		}
	}

}
