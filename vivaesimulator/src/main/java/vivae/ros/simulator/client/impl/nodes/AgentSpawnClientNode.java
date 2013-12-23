package vivae.ros.simulator.client.impl.nodes;


import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;

import vivae.SpawnResponse;
import vivae.ros.simulator.client.AgentSpawningSynchronousClient;
import vivae.ros.simulator.client.impl.AgentSpawnSynchronousClient;
/**
 * ROS node which is able to spawn agents in the SImulatorServer.
 * 
 * @author Jaroslav Vitku
 *
 */
public class AgentSpawnClientNode extends AbstractNodeMain implements AgentSpawningSynchronousClient{

	private AgentSpawnSynchronousClient sc;

	public final static String NAME = "AgentSpawnSynchronousClientNode";
	public final String me = "["+NAME+"] "; 

	private int slept;
	protected final int sleeptime = 10;
	protected final int maxSleep = 2000;	// max wait time to services to initialize

	@Override
	public GraphName getDefaultNodeName() { return GraphName.of(NAME); }

	@Override
	public void onStart(final ConnectedNode connectedNode) {
		// Connect into the ROS network, register services with the SimulatorServer
		sc = new AgentSpawnSynchronousClient(connectedNode);
	}

	@Override
	public boolean callLoadMap(String name) {
		this.awaitStarted();
		return sc.callLoadMap(name); 	
	}

	@Override
	public boolean callStartSimulation() { 
		this.awaitStarted();
		return sc.callStartSimulation(); 
	}

	@Override
	public boolean callStopSimulation() { 
		this.awaitStarted();
		return sc.callStopSimulation(); 
	}

	@Override
	public boolean callDestroySimulation() {
		this.awaitStarted();
		return sc.callDestroySimulation(); 
	}

	@Override
	public boolean callSetVisibility(boolean visible) { 
		this.awaitStarted();
		return sc.callSetVisibility(visible); 
	}

	@Override
	public SpawnResponse spawnAgent(String name) { 
		this.awaitStarted();
		return sc.spawnAgent(name); 
	}

	@Override
	public SpawnResponse spawnAgent(String name, int numSensors) {
		this.awaitStarted();
		return sc.spawnAgent(name, numSensors);
	}

	@Override
	public SpawnResponse spawnAgent(String name, int numSensors, float frictionDist) {
		this.awaitStarted();
		return sc.spawnAgent(name, numSensors, frictionDist);
	}

	@Override
	public SpawnResponse spawnAgent(String name, int numSensors, float frictionDist, float maxDist) {
		this.awaitStarted();
		return sc.spawnAgent(name, numSensors, frictionDist, maxDist);
	}

	private void awaitStarted(){
		slept=0;
		while(sc==null){
			try {
				Thread.sleep(sleeptime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(sleeptime*slept++ >maxSleep){
				System.err.println(me+"my ROS node not started within " +
						"max. time of "+maxSleep+"ms, giving up !!!");
				return;
			}
		}
	}
}