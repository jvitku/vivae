package vivae.ros.simulator.client.demo.agent;

import java.io.IOException;

import org.ros.concurrent.CancellableLoop;
import org.ros.node.ConnectedNode;

import ctu.nengoros.service.synchornous.SynchronousService;
import vivae.ros.simulator.client.impl.AgentSpawnSynchronousClient;
import vivae.ros.util.MapLoader;
import vivae.ros.util.Util;

/**
 * Press enter and this thing will request loading vivae with selected map.
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
 * 		./run vivae.ros.simulator.client.demo.agent.MySpawnRequestingClient
 * 
 *  ..and press enter
 *  
 * 
 * 
 * @author Jaroslav Vitku
 *
 */
public class MySpawnRequestingClient extends AgentSpawnSynchronousClient {

	public final static String NAME = "MyAgentSpawnSynchronousClient";
	public final String me = "["+NAME+"] ";
	
	SynchronousService<vivae.SpawnRequest, vivae.SpawnResponse> spawn;
	
	// names for some agents
	String[] names = new String[]{"a", "b", "c", "overagented" };


	@Override
	public void onStart(final ConnectedNode connectedNode) {
		super.onStart(connectedNode);
		
		
		callLoadMap(MapLoader.DEF_MAP);
		callSetVisibility(true);
		
		// create classical while(true) loop, but this loop can be cancelled by the others..
		connectedNode.executeCancellableLoop(new CancellableLoop() {
			private int poc;

			@Override
			protected void setup() {
				poc = 0;
			}

			@Override
			protected void loop() throws InterruptedException {
				
				if(poc==0)
					System.out.println(me+" --------- press any key to request agent and that is it");
				else
					System.out.println(me+" --------- press any key to register another agent!");
				
				try {
					System.in.read();
				} catch (IOException e) { e.printStackTrace(); }
				System.out.println("requesting this agent: "+names[poc]);
				
				vivae.SpawnResponse sr = spawnAgent(names[poc]);
				System.out.println(me+"agent registered OK? "+sr.getSpawnedOK());
				
				if(!sr.getSpawnedOK() && poc>2){
					System.out.println(me+"\n\nWARNING: You tried to add "+(poc+1)+
							"th agent to the simulation, "+
							"but the map contains only 3 bodies for agents!\n\n");
					System.out.println("......... !!!!!!!!!!!!!!!!!!!!!!!!!!!................\n\n");
				}
				
				System.out.println(me+"Starting the sumilation");
				
				callStartSimulation();
				
				Util.waitLoop(2000);
				
				System.out.println(me+"Stopping the simulation");
				callStopSimulation();
				
				if(++poc>names.length-1)
					poc =0;
			}
		});
	}
}
