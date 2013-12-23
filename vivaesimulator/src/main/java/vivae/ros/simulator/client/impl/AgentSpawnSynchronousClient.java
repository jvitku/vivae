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

		ServiceClient<vivae.SpawnRequest, vivae.SpawnResponse> serviceClient = null;
		int sleptAlready = 0;

		while(true){
			try {
				// try to connect to the service for requesting the agents..
				this.registerService(connectedNode, serviceClient);
				break;

				// problem? retry several times (simulatorServer may not be started)
			} catch (ServiceNotFoundException e) {

				if(servicesleeptime*sleptAlready++ > maxWaitForServer){
					System.err.println(me+"Error while initializing agent spawn service!");
					throw new RosRuntimeException(e);
				}
				System.out.println(me+"Could not register service.. retrying");
			}
		}
	}

	/**
	 * Register agent spawning service to the simulatorServer
	 * 
	 * @param connectedNode
	 * @param serviceClient
	 * @throws ServiceNotFoundException server probably not managed to initialize the service yet
	 */
	private void registerService(ConnectedNode connectedNode,
			ServiceClient<vivae.SpawnRequest, vivae.SpawnResponse> serviceClient)
					throws ServiceNotFoundException{

		serviceClient = connectedNode.newServiceClient(Sim.Msg.SPAWN, vivae.Spawn._TYPE);
		spawn= new SynchronousService<vivae.SpawnRequest, vivae.SpawnResponse>(serviceClient);

	}

	@Override
	public SpawnResponse spawnAgent(String name) {
		this.awaitServicesReady();

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

	/**
	 * Wait also for agent spawning service to be registered.
	 */
	@Override
	protected void awaitServicesReady(){
		super.awaitServicesReady();
		while(spawn==null){
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
