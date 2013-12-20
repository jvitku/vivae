package vivae.ros.simulator.server.services;

import org.ros.exception.ServiceException;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceResponseBuilder;

import vivae.ros.simulator.engine.AgentRegisteringSimulation;
import vivae.ros.simulator.engine.agents.RosAgent;
import vivae.ros.simulator.server.AgentFactory;

/**
 * This spawns agents in the ViVae simulation. After loading map, the arena contains
 * agent bodies which are not controlled. In order to control agent in the arena, the 
 * agent needs to be registered. In order to control the agent over the ROS network,
 * the agent has to communicate over the ROS network (publish/subscribe here). 
 * 
 * This service tries to register agent in the arena, if success, it opens the ROS
 * communication channels for the agent and returns all necessary information.
 *
 * @author Jaroslav Vitku
 *
 */
public class AgentSpawnServiceResponseBuilder implements 
ServiceResponseBuilder<vivae.SpawnRequest, vivae.SpawnResponse>{

	public static String me = "[AgentSpawnService] ";
	
	AgentFactory af;

	public AgentSpawnServiceResponseBuilder(AgentRegisteringSimulation sim,
			ConnectedNode connectedNode){
		af = new AgentFactory(sim, connectedNode);
	}

	@Override
	public void build(vivae.SpawnRequest req, vivae.SpawnResponse resp) 
			throws ServiceException {

		java.lang.String name = req.getName();	// get name of required agent
		int numSensors = req.getNumSensors();
		float maxDist = req.getMaxDistance();
		float frictionDist = req.getFrictionDistance();
		//System.out.println("Getting request to spawn this: "+name+" agent");

		// here we will add also speed sensor (last float)
		RosAgent a = af.buildAgent(name, numSensors+1, maxDist, frictionDist);

		if(a!= null){
			//System.out.println("Agent "+a.getName()+" spawned");
			resp.setSpawnedOK(true);
			resp.setName(a.getName());
			resp.setPubTopicName(a.getPubTopic());
			resp.setSubTopicName(a.getSubTopic());
			resp.setNumSensors(a.getSensoryDataLength());

		}else{
			System.out.println(me+"Error spawning agent..");
			resp.setSpawnedOK(false);
			resp.setName(name);
			resp.setPubTopicName("-");
			resp.setSubTopicName("-");
		}
	}
}