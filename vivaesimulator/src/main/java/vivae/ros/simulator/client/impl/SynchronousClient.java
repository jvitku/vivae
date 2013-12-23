package vivae.ros.simulator.client.impl;

import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;

import ctu.nengoros.service.synchornous.SynchronousService;
import vivae.ros.simulator.client.SynchornousClient;
import vivae.ros.simulator.server.Sim;

/**
 * This is general synchronous client for the SimulatorServer which is able to control 
 * the state of the simulation.  
 * 
 * @author Jaroslav Vitku
 *
 */
public class SynchronousClient implements SynchornousClient{

	public final static String NAME = "SynchronousClient";
	public final String me = "["+NAME+"] "; 

	protected SynchronousService<vivae.LoadMapRequest,vivae.LoadMapResponse> map;
	protected SynchronousService<vivae.SimControllerRequest,vivae.SimControllerResponse> controls;

	protected int slept;
	protected final int sleeptime = 10;
	protected final int maxSleep = 2000;	// max wait time to services to initialize

	protected final int servicesleeptime = 200;
	protected final int maxWaitForServer= 5000;	// max time to wait for server to launch
	
	public SynchronousClient(ConnectedNode connectedNode){
		this.registerMyServices(connectedNode);
	}
	
	private void registerMyServices(ConnectedNode connectedNode) throws RosRuntimeException{
		int sleptAlready = 0;
		ServiceClient<vivae.LoadMapRequest, vivae.LoadMapResponse> mapServiceClient = null;
		ServiceClient<vivae.SimControllerRequest, vivae.SimControllerResponse> simServiceClient = null;
		
		while(true){

			try {
				// try to subscribe for the ROS services..
				this.registerServices(connectedNode, mapServiceClient, simServiceClient);
				break;

				// problem? retry several times (simulatorServer may not be started yet)
			} catch (ServiceNotFoundException e) {

				if(servicesleeptime*sleptAlready++ > maxWaitForServer){
					System.err.println(me+"Error while initializing the ROS services!");
					throw new RosRuntimeException(e);
				}
				System.out.println(me+"Could not register service.. retrying");
			}
		}
	}

	/**
	 * Register services or throw an exception if one of them is not found. 
	 * @param connectedNode
	 * @throws ServiceNotFoundException
	 */
	private void registerServices(ConnectedNode connectedNode,
			ServiceClient<vivae.LoadMapRequest, vivae.LoadMapResponse> mapServiceClient,
			ServiceClient<vivae.SimControllerRequest, vivae.SimControllerResponse> simServiceClient)
					throws ServiceNotFoundException{

		// service for map loading
		if(mapServiceClient==null){
			mapServiceClient = connectedNode.newServiceClient(Sim.Msg.LOAD, vivae.LoadMap._TYPE);
			map      = new SynchronousService<vivae.LoadMapRequest, vivae.LoadMapResponse>(mapServiceClient);
		}

		// service for controlling the simulation
		if(simServiceClient==null){
			simServiceClient = connectedNode.newServiceClient(Sim.Msg.CONTROL, vivae.SimController._TYPE);
			controls = new SynchronousService<vivae.SimControllerRequest, vivae.SimControllerResponse>(simServiceClient);
		}
	}

	@Override
	public boolean callLoadMap(String name) {

		this.awaitServicesReady();

		vivae.LoadMapRequest req = map.getRequest();
		req.setName(name);
		vivae.LoadMapResponse resp = map.callService(req);

		// timeout?
		if(resp==null)
			return false;

		// return the value of ROS message - response 
		return resp.getLoadedOK();
	}

	@Override
	public boolean callStartSimulation() {
		this.awaitServicesReady();
		return this.callControlsService(Sim.Cmd.START);
	}

	@Override
	public boolean callStopSimulation() {
		this.awaitServicesReady();
		return this.callControlsService(Sim.Cmd.STOP);
	}

	@Override
	public boolean callDestroySimulation() {
		this.awaitServicesReady();
		return this.callControlsService(Sim.Cmd.DESTROY);
	}

	@Override
	public boolean callSetVisibility(boolean visible) {
		this.awaitServicesReady();

		if(visible)
			return this.callControlsService(Sim.Cmd.SETVISIBLE);
		else
			return this.callControlsService(Sim.Cmd.SETINVISIBLE);
	}

	private boolean callControlsService(String command){
		this.awaitServicesReady();

		vivae.SimControllerRequest req = controls.getRequest();
		req.setWhat(command);
		vivae.SimControllerResponse resp = controls.callService(req);

		if(resp==null)
			return false;

		return resp.getOk();
	}

	/**
	 * This method waits until the node is initialized
	 * and the services are registered in the ROS network! 
	 * 
	 */
	protected void awaitServicesReady(){
		slept=0;
		while(map==null || controls==null){
			try {
				Thread.sleep(sleeptime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(sleeptime*slept++ >maxSleep){
				System.err.println(me+"Services not registered within " +
						"max. time of "+maxSleep+"ms, giving up !!!");
				return;
			}
		}
	}

}
