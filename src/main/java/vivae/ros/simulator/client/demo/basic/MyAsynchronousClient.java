package vivae.ros.simulator.client.demo.basic;

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
import vivae.ros.simulator.server.Sim;
import vivae.ros.simulator.server.SimulatorServer;

/**
 * Start the client as a ROS node, press enter, this will load the simulation and run for 5 seconds.
 * 
 * To run this demo:
 * 
 * -Run the ROS core, e.g.:
 * 
 * 		cd jroscore && ./jroscore
 * 
 * -RUn the class SimulatorServer, e.g.:
 * 
 * 		./run vivae.ros.simulator.server.SimulatorServer
 * 
 * -Run this class:
 * 
 * 		./run vivae.ros.simulator.client.demo.basic.MySynchronousClient
 *  
 * Here, the requests are processes asynchronously (request is sent and control is passed back from the method).
 * 
 * Note: by default, the simulation is started as invisible!  
 * Note: that synchronous usage of services is strongly advised, for demo on this @see SynchronousClient .
 * 
 * @author Jaroslav Vitku
 *
 */
public class MyAsynchronousClient extends AbstractNodeMain {

	private final String me = "[MyAsynchronousClient] ";

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
			mapServiceClient = connectedNode.newServiceClient(Sim.Msg.LOAD, vivae.LoadMap._TYPE);
			simServiceClient = connectedNode.newServiceClient(Sim.Msg.CONTROL, vivae.SimController._TYPE);

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
				System.out.println(me+"Requesting this map: "+names[poc]);

				// set the request and call the srvice server (asynchronously)
				final vivae.LoadMapRequest req = mapServiceClient.newMessage();
				req.setName(names[poc]);
				mapServiceClient.call(req, srl);
				
				startSimulation();
				System.out.println(me+"waiting 5 seconds...");
				Thread.sleep(5000);
				System.out.println(me+"stopping the simulation now!");
				stopSimulation();
				destroySimulation();
				
				if(++poc>names.length-1)
					poc =0;
			}
		});
	}
	
	
	public void startSimulation(){

		final vivae.SimControllerRequest req = simServiceClient.newMessage();
		req.setWhat(Sim.Cmd.START);
		// set request for starting the simulation
		simServiceClient.call(req, simSrl);
		
	}
	
	public void stopSimulation(){

		final vivae.SimControllerRequest req = simServiceClient.newMessage();
		req.setWhat(Sim.Cmd.STOP);
		// set request for starting the simulation
		simServiceClient.call(req, simSrl);
		
	}
	
	public void destroySimulation(){

		final vivae.SimControllerRequest req = simServiceClient.newMessage();
		req.setWhat(Sim.Cmd.DESTROY);
		// set request for starting the simulation
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
