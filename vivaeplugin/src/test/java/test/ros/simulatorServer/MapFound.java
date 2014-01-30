package test.ros.simulatorServer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.*;

import vivae.ros.simulator.client.impl.nodes.SynchronousClientNode;
import vivae.ros.simulator.server.Sim;
import vivae.ros.util.Util;
import ctu.nengoros.RosRunner;
import ctu.nengoros.network.node.testsuit.RosCommunicationTest;
/**
 * Further testing of functionalities of SimulatorServer: 
 * spawn agents (that can be controlled over the ROS network) in the simulator.
 * 
 * @author Jaroslav Vitku
 *
 */
public class MapFound extends RosCommunicationTest{

	public static final String server = "vivae.ros.simulator.server.SimulatorServer";
	public static final String requester = "vivae.ros.simulator.client.impl.nodes.SynchronousClientNode";
	
	/**
	 * This test just checks whether the default map can be found (dependencies OK).
	 * The error can occur while the project is linked e.g. to jar file of
	 * vivaesimulator located on unexpected place (e.g. under ~/.m2) 
	 */
	@Test
	public void testMapCanBeFound(){
		boolean resp;

		RosRunner s= runNode(server);		// server
		assertTrue(s.isRunning());
		
		RosRunner rr = runNode(requester);	// client
		assertTrue(rr.isRunning());
		
		SynchronousClientNode cl = (SynchronousClientNode)rr.getNode();		
		
		Util.waitLoop(500);
		
		resp = cl.callLoadMap(Sim.Maps.DEFAULT);
		System.out.println("map loaded OK? "+resp);
		assertTrue(resp);
		

		s.stop();
		assertFalse(s.isRunning());
		
		rr.stop();
		assertFalse(rr.isRunning());
	}
	
}

