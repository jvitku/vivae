package vivae.ros.simulatorServer.demo;

import java.io.IOException;

import org.ros.concurrent.CancellableLoop;
import org.ros.exception.RosRuntimeException;
import org.ros.exception.ServiceNotFoundException;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.service.ServiceClient;

import vivae.ros.simulatorServer.SimulatorServer;

/**
 * Press enter and this thing will request loading vivae with selected map.
 * 
 * @author Jaroslav Vitku
 *
 */
public class SpawnRequester extends AbstractNodeMain {

	private final String me = "SpawnRequester: ";

	/**
	 * default name of the Node
	 */
	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("SpawnRequester");
	}

	SynchronousService<vivae.SpawnRequest, vivae.SpawnResponse> spawn;
	
	// names of some known maps..
	String[] names = new String[]{"a", "b", "c", "overagented" };


	@Override
	public void onStart(final ConnectedNode connectedNode) {

		// try to subscribe to the service for requesting the agents..		
		try {
			ServiceClient<vivae.SpawnRequest, vivae.SpawnResponse> serviceClient 
			= connectedNode.newServiceClient(SimulatorServer.srvSPAWN, vivae.Spawn._TYPE);
			
			spawn= new SynchronousService<vivae.SpawnRequest, vivae.SpawnResponse>(serviceClient);
		
		} catch (ServiceNotFoundException e) {
			throw new RosRuntimeException(e);
		}

		// create classical while(true) loop, but this loop can be cancelled by the others..
		connectedNode.executeCancellableLoop(new CancellableLoop() {
			private int poc;

			@Override
			protected void setup() {
				poc = 0;
			}

			@Override
			protected void loop() throws InterruptedException {
				
				System.out.println(me+" --------- press any key to request agent and that is it");
				try {
					System.in.read();
				} catch (IOException e) { e.printStackTrace(); }
				System.out.println("requesting this agent: "+names[poc]);
				
				// get agents name and so..
				vivae.SpawnRequest req = spawn.getRequest();
				req.setName(names[poc]);
				vivae.SpawnResponse resp = spawn.callService(req);
				
				if(resp.getSpawnedOK()){
					System.out.println("agent named "+resp.getName() +" spawned OK");
					System.out.println(" You can now make publisher and subscriber to him" +
							"data are: "+resp.getPubTopicName()+" sub "+resp.getSubTopicName());
				}
				
				if(++poc>names.length-1)
					poc =0;
			}
		});
	}
}
