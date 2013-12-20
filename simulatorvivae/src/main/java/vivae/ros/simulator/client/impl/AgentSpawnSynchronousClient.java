package vivae.ros.simulator.client.impl;

import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;

import ctu.nengoros.service.synchornous.SynchronousService;
import vivae.SpawnResponse;
import vivae.ros.simulator.client.AgentSpawningSynchronousSimulationClient;
import vivae.ros.simulator.server.Sim;

/**
 * Basic ROS node which supports spawning agents in the ViVae simulator.
 *  
 * @author Jaroslav Vitku
 *
 */
public class AgentSpawnSynchronousClient extends SynchronousClient 
implements AgentSpawningSynchronousSimulationClient{

	public final static String NAME = "AgentSpawnSynchronousClient";
	public final String me = "["+NAME+"] ";

	SynchronousService<vivae.SpawnRequest, vivae.SpawnResponse> spawn;

	@Override
	public void onStart(final ConnectedNode connectedNode){
		super.onStart(connectedNode);

		// try to connect to the service for requesting the agents..		
		try {
			ServiceClient<vivae.SpawnRequest, vivae.SpawnResponse> serviceClient 
			= connectedNode.newServiceClient(Sim.Msg.SPAWN, vivae.Spawn._TYPE);

			// make it synchronous service
			spawn= new SynchronousService<vivae.SpawnRequest, vivae.SpawnResponse>(serviceClient);

		} catch (ServiceNotFoundException e) {
			throw new RosRuntimeException(e);
		}
	}

	@Override
	public SpawnResponse spawnAgent(String name) {
		vivae.SpawnRequest req = spawn.getRequest();
		req.setName(name);
		vivae.SpawnResponse resp = spawn.callService(req);

		if(resp.getSpawnedOK()){
			System.out.println(me+"agent named "+resp.getName() +" spawned OK");
			System.out.println(me+"His new publisher and subscriber topics are:" +
					" Publisher=\""+resp.getPubTopicName()+"\" Subscriber=\""+resp.getSubTopicName()+"\"");
			return resp;
		}
		System.err.println(me+"Error spawning agent named "+name+"!");
		return resp;
	}

}
