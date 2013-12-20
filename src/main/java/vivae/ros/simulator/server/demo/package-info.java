/**
 * 
 * Package for demos, for demo on starting simulation server/client, do this:
 * 
 * -installApp
 * 
 * 		cd vivae && ./gradlew installApp
 * 
 * -start a ROS master
 * 
 * 		cd jroscore && ./jroscore
 * 		
 * -start simulation server:
 * 
 * 		./run vivae.ros.simulator.server.SimulatorServer
 * 
 * -start any of vivae.ros.simulator.client.demo classes, e.g.:
 * 
 * 		./run vivae.ros.simulator.client.demo.AsynchronousClient
 * 
 * EFor example, the class MyAsynchronousClient requests from the SimulatorServer simulation 
 * with given map after pressing any key. The simulation is ran for several seconds and stopped.
 * 
 * 
 * Note: Asynchronous requesting over the ROS network did not prove to be good idea 
 * (concurrent), so SynchornousService class was implemented and MySynchrnousRequester tests this..
 * 
 */
/**
 * @author Jaroslav Vitku
 *
 */
package vivae.ros.simulator.server.demo;
