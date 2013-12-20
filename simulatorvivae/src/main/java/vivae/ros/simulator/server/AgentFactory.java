package vivae.ros.simulator.server;

import org.ros.node.ConnectedNode;

import vivae.ros.simulator.engine.AgentRegisteringSimulation;
import vivae.ros.simulator.engine.agents.RosAgent;
import vivae.ros.simulator.engine.agents.impl.SynchronousROSAgent;

/**
 * Here the Vivae agents are born..
 * 
 * @author Jaroslav Vitku
 *
 */
public class AgentFactory {

	private final AgentRegisteringSimulation mysim;

	public final String pub = "_pub";
	public final String sub = "_sub";
	ConnectedNode cn;
	
	public final boolean DEFSYNCHRONOUS = true;

	public AgentFactory(AgentRegisteringSimulation sim, ConnectedNode cn){
		mysim = sim;
		this.cn = cn;
	}

	private boolean check(String name){
		if(!mysim.canAddAgent(name)){
			System.err.println("agent cannot be added to the simulaiton");
			return false;
		}
		return true;
	}

	public RosAgent buildAgent(String name){
		if(!check(name))
			return null;

		SynchronousROSAgent a = new SynchronousROSAgent(name, name+pub, name+sub, cn,DEFSYNCHRONOUS);
		mysim.registerAgent(a);

		return a;
	}

	public RosAgent buildAgent(String name, int numSensors){
		if(!check(name))
			return null;

		SynchronousROSAgent a = new SynchronousROSAgent(name, name+pub, name+sub, cn,DEFSYNCHRONOUS);
		if(numSensors>=0);
			a.setNumSensors(numSensors);
		mysim.registerAgent(a);
		return a;
	}

	public RosAgent buildAgent(String name, int numSensors, 
			double maxDistance, double frictionDistance){
		if(!check(name))
			return null;
		
		RosAgent a = new SynchronousROSAgent(name, name+pub, name+sub, cn, DEFSYNCHRONOUS);
		if(numSensors>=0)
			a.setNumSensors(numSensors);
		if(maxDistance >=0)
			a.setMaxSensorDistance(maxDistance);
		if(frictionDistance>=0)
			a.setFrictionDistance(frictionDistance);
		mysim.registerAgent(a);
		return a;
	}
}
