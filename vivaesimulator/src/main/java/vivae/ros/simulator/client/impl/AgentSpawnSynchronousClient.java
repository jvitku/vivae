package vivae.ros.simulator.client.impl;

import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;

import ctu.nengoros.service.synchornous.SynchronousService;
import vivae.SpawnResponse;
import vivae.ros.simulator.client.AgentSpawningSynchronousClient;
import vivae.ros.simulator.server.Sim;

/**
 * Client for SimulatorServer which is able to control the simulation state and spawn agents in it.
 *  
 * @author Jaroslav Vitku
 *
 */
public class AgentSpawnSynchronousClient extends SynchronousClient 
implements AgentSpawningSynchronousClient{

	public final static String NAME = "AgentSpawnSynchronousClient";
	public final String me = "["+NAME+"] ";

	SynchronousService<vivae.SpawnRequest, vivae.SpawnResponse> spawn;

	public AgentSpawnSynchronousClient(ConnectedNode connectedNode){
		super(connectedNode);

		this.registerMyServices(connectedNode);
	}

	private void registerMyServices(ConnectedNode connectedNode) throws RosRuntimeException{

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


	/**
	 * Try to spawn agent in the Vivae SimulatorServer with given name. 
	 * Return the spawnResponse which contains information about agent just spawned. 
	 */
	@Override
	public SpawnResponse spawnAgent(String name) {
		this.awaitServicesReady();

		vivae.SpawnRequest req = this.makeBasicRequest(name);

		return this.callAgentSpawnService(req);
	}

	@Override
	public SpawnResponse spawnAgent(String name, int numSensors) {
		vivae.SpawnRequest req = this.makeBasicRequest(name);
		req.setNumSensors(numSensors);
		return this.callAgentSpawnService(req);
	}

	@Override
	public SpawnResponse spawnAgent(String name, int numSensors, float frictionDist) {
		vivae.SpawnRequest req = this.makeBasicRequest(name);
		req.setNumSensors(numSensors);
		req.setFrictionDistance(frictionDist);
		return this.callAgentSpawnService(req);
	}


	@Override
	public SpawnResponse spawnAgent(String name, int numSensors, float frictionDist, float maxDist) {
		vivae.SpawnRequest req = this.makeBasicRequest(name);
		req.setNumSensors(numSensors);
		req.setFrictionDistance(frictionDist);
		req.setMaxDistance(maxDist);
		return this.callAgentSpawnService(req);
	}

	private vivae.SpawnRequest makeBasicRequest(String name){
		vivae.SpawnRequest req = spawn.getRequest();
		req.setName(name);
		return req;
	}

	/**
	 * Call the service, receive response and return the result.
	 * 
	 * @param req angentSpawn request
	 * @return agentSpawn response with information about the agent
	 */
	private vivae.SpawnResponse callAgentSpawnService(vivae.SpawnRequest req){
		vivae.SpawnResponse resp = spawn.callService(req);

		if(resp==null){
			System.err.println(me+"Error spawning agent! Probably network error!");
			return null;
		}

		if(!resp.getSpawnedOK()){
			System.err.println(me+"Error spawning agent named "+resp.getName()+"! "
					+ "SimulatorServer probably denied to spawn this agent.");
			return resp;
		}

		System.out.println(me+"agent named "+resp.getName() +" spawned OK");
		System.out.println(me+"His new publisher and subscriber topics are:" +
				" Publisher=\""+resp.getPubTopicName()+"\" Subscriber=\""+resp.getSubTopicName()+"\"");
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
