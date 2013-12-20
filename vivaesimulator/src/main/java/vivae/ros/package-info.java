/**
 * 
 * First, launch node called VIvaeSimulator, then other nodes are
 * created by spawning new robots in the simulator.
 * 
 * This package holds communication interface for ROS.
 * In order to run a simulation with remotely-controlled agents,
 * there should be two ROS nodes launched. One for sensory system, 
 * one for actuator system. Each sensor/actuator should be 
 * published/subscribed in its own topic. So sensory data can be
 * added and removed freely.  
 * 
 */
/**
 * @author Jaroslav Vitku
 *
 */
package vivae.ros;
