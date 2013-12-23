package vivae.ros.simulator.client.demo.basic;

import java.io.IOException;

import org.ros.concurrent.CancellableLoop;
import org.ros.node.ConnectedNode;

import vivae.ros.simulator.client.impl.nodes.SynchronousClientNode;
import vivae.ros.util.Util;

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
 * @author Jaroslav Vitku
 *
 */
public class MySynchronousClient extends SynchronousClientNode{

	public static final String NAME = "MySynchronousClient";
	
	String[] names = new String[]{
			"data/scenarios/arena1.svg", 
			"data/scenarios/arena2.svg",
	"data/scenarios/ushape.svg" };

	@Override
	public void onStart(final ConnectedNode connectedNode) {
		
		// register services
		super.onStart(connectedNode);

		connectedNode.executeCancellableLoop(new CancellableLoop() {
			private int poc;
			boolean req;

			@Override
			protected void setup() {
				poc = 0;
			}

			@Override
			protected void loop() throws InterruptedException {
				System.out.println(me+" --------- press any key to request a map and start new simulation");
				try {
					System.in.read();
				} catch (IOException e) { e.printStackTrace(); }
				
				System.out.println(me+"Requesting to load this map: "+names[poc]+"   ....");
				req = callLoadMap(names[poc]);
				System.out.println(".... done, success? "+req+"\n");
				
				System.out.println(me+"Requesting to set the map visible....");
				req = callSetVisibility(true);
				System.out.println(".... done, success? "+req+"\n");
				
				System.out.println(me+"Starting the simulation ....");
				req = callStartSimulation();
				System.out.println(".... done, success? "+req+"\n");
				
				Util.waitLoop(5000);
				
				System.out.println(me+"Stopping the simulation...");
				req = callStopSimulation();
				System.out.println(".... done, success? "+req+"\n");
				
				
				System.out.println(me+"Destroying the simulation...");
				req = callDestroySimulation();
				System.out.println(".... done, success? "+req+"\n");

				if(++poc>names.length-1)
					poc =0;
			}
		});
	}

}
