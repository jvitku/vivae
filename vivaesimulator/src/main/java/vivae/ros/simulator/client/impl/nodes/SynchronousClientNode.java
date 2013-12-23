package vivae.ros.simulator.client.impl.nodes;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;

import vivae.ros.simulator.client.SynchornousClient;
import vivae.ros.simulator.client.impl.SynchronousClient;

/**
 * This is basically the Synchronous client which can be used as a stand-alone ROS node.
 * 
 * @author Jaroslav Vitku
 *
 */
public class SynchronousClientNode extends AbstractNodeMain implements SynchornousClient{

	private SynchronousClient sc;

	public final static String NAME = "SynchronousClientNode";
	public final String me = "["+NAME+"] ";

	private int slept;
	protected final int sleeptime = 10;
	protected final int maxSleep = 2000;	// max wait time to services to initialize

	@Override
	public GraphName getDefaultNodeName() { return GraphName.of(NAME); }

	@Override
	public void onStart(final ConnectedNode connectedNode) {

		// Just register all my services over the ROS network with the SImulatorServer
		sc = new SynchronousClient(connectedNode);
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
	public boolean callReset() { 
		this.awaitStarted();
		return sc.callReset(); 
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
