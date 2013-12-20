package vivae.ros.simulator.client.impl;

import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;

import ctu.nengoros.service.synchornous.SynchronousService;
import vivae.ros.simulator.client.SynchronousSimulationClient;
import vivae.ros.simulator.server.SimCommands;
import vivae.ros.simulator.server.SimulatorServer;

public class SynchronousClient extends AbstractNodeMain implements SynchronousSimulationClient{

	public final static String NAME = "SynchronousClient";
	public final String me = "["+NAME+"] "; 
	
	protected SynchronousService<vivae.LoadMapRequest,vivae.LoadMapResponse> map;
	protected SynchronousService<vivae.SimControllerRequest,vivae.SimControllerResponse> controls;

	@Override
	public GraphName getDefaultNodeName() { return GraphName.of(NAME); }
	
	
	@Override
	public void onStart(final ConnectedNode connectedNode) {

		// try to subscribe for the ROS services..		
		try {
			// service for map loading
			ServiceClient<vivae.LoadMapRequest, vivae.LoadMapResponse> mapServiceClient =
					connectedNode.newServiceClient(SimulatorServer.srvLOAD, vivae.LoadMap._TYPE);
			
			// service for controlling the simulation
			ServiceClient<vivae.SimControllerRequest, vivae.SimControllerResponse> simServiceClient =
					connectedNode.newServiceClient(SimulatorServer.srvCONTROL, vivae.SimController._TYPE);

			// make the services synchronous
			controls = new SynchronousService<vivae.SimControllerRequest, vivae.SimControllerResponse>(simServiceClient);
			map      = new SynchronousService<vivae.LoadMapRequest, vivae.LoadMapResponse>(mapServiceClient);
			
		} catch (ServiceNotFoundException e) {
			System.err.println(me+"Error while initializing the ROS services!");
			throw new RosRuntimeException(e);
		}
	}
	
	@Override
	public boolean callLoadMap(String name) {
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
		return this.callControlsService(SimCommands.START);
	}

	@Override
	public boolean callStopSimulation() {
		return this.callControlsService(SimCommands.STOP);
	}

	@Override
	public boolean callDestroySimulation() {
		return this.callControlsService(SimCommands.DESTROY);
	}

	@Override
	public boolean callSetVisibility(boolean visible) {
		if(visible)
			return this.callControlsService(SimCommands.SETVISIBLE);
		else
			return this.callControlsService(SimCommands.SETINVISIBLE);
	}

	private boolean callControlsService(String command){
		vivae.SimControllerRequest req = controls.getRequest();
		req.setWhat(command);
		vivae.SimControllerResponse resp = controls.callService(req);
		
		if(resp==null)
			return false;
		
		return resp.getOk();
	}
}
