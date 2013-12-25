package vivae.ros.simulator.client.impl.nodes;

import java.util.ArrayList;
import java.util.Random;

import org.ros.concurrent.CancellableLoop;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import vivae.ros.simulator.server.Sim;


/**
 * Can control the SImulatorServer,spawn agent and control it.
 * 
 * Is able to control one agent at a time, just for testing.
 * 
 * @author Jaroslav Vitku
 *
 */
public class AgentControlClientNode extends AgentSpawnClientNode {

	// node:
	public final static String NAME = "AgentControlClientNode";
	public final String me = "["+NAME+"] ";

	// agent communication:
	public final String name = "exampleAgent";
	
	protected final java.lang.String topicActuator = name+"_pub";
	Publisher<std_msgs.Float32MultiArray> actuator;

	protected final java.lang.String topicSensor = name+"_sub";
	Subscriber<std_msgs.Float32MultiArray> sensor;

	// agent config:
	public int numSensors = 8;
	public float maxDist = 20;
	public float frictionDist = 60;
	
	// util:
	protected ConnectedNode cn;
	Random r = new Random(); 
	protected ArrayList<float[]> sensoryData;
	
	// simulation:
	protected int sleeptime = 20;
	protected boolean isRunning = false;
	
	@Override
	public GraphName getDefaultNodeName() { return GraphName.of(NAME); }

	// register my client services
	@Override
	public void onStart(final ConnectedNode connectedNode) {
		super.onStart(connectedNode);

		cn = connectedNode;
		
		// Call e.g. this externally: @see vivae.ros.simulator.client.demo.agent.*
		//vivae.SpawnResponse agent = this.prepareVivaeSimulation();
		//this.connectToAgent(agent.getPubTopicName(), agent.getSubTopicName());
		//this.runTheSimulationFor(runtime);//ms
	}
	
	public vivae.SpawnResponse prepareVivaeSimulation(){

		System.out.println(me+"loading the default map");
		callLoadMap(Sim.Maps.DEFAULT);
		callSetVisibility(true);

		System.out.println(me+"Requesting this agent: "+name);
		vivae.SpawnResponse sr = spawnAgent(name,numSensors,maxDist,frictionDist);
		//vivae.SpawnResponse sr = spawnAgent(name,8);
		//vivae.SpawnResponse sr = spawnAgent(name,8,10);

		System.out.println(me+"agent registered OK? "+sr.getSpawnedOK());

		System.out.println("Reading this num of sensors:  "+ sr.getNumSensors());
		return sr;
	}
	
	public void connectToAgent(String publishedTopic, String subscribedTopic){
		actuator = cn.newPublisher(subscribedTopic, std_msgs.Float32MultiArray._TYPE);
		sensor = cn.newSubscriber(publishedTopic, std_msgs.Float32MultiArray._TYPE);

		sensoryData = new ArrayList<float[]>(20);

		sensor.addMessageListener(new MessageListener<std_msgs.Float32MultiArray>() {
			// print messages to console
			@Override
			public void onNewMessage(std_msgs.Float32MultiArray message) {

				float[] data = message.getData();
				System.out.println("----RECEIVED message with these sensory data:"+toAr(data));

				sensoryData.add(data.clone());
			}
		});
	}
	
	public void runTheSimulationFor(final int ms){

		isRunning = true;
		
		cn.executeCancellableLoop(new CancellableLoop() {
			private int poc;

			@Override
			protected void setup() {
				poc = 0;
				System.out.println(me+"Starting the sumilation");
				callStartSimulation();
			}

			@Override
			protected void loop() throws InterruptedException {

				Thread.sleep(sleeptime);

				System.out.println(me+"Simulation step no."+poc+
						", local time:"+(poc*sleeptime)+" of complete:"+ms);

				moveActuator();

				if(++poc*sleeptime > ms){
					System.out.println(me+"Stopping the simulaiton on SimulatorServer");
					callStopSimulation();

					System.out.println(me+"Destroying the simulaiton on the server");
					callDestroySimulation();

					isRunning = false;

					// cancel the loop (this will stop the loop, not the node)
					this.cancel();
					// causes exceptions and SynchronousService problems, TODO: solve this 
					//cn.shutdown();
				}
			}
		});
	}

	public void setSleepTime(int sleeptime){
		this.sleeptime = sleeptime;
	}
	
	
	public boolean isRunning(){
		return this.isRunning;
	}

	public ArrayList<float[]> getSensoryData(){
		return sensoryData;
	}

	private void moveActuator(){
		std_msgs.Float32MultiArray mess = actuator.newMessage();	

		float[] data = generateData();

		System.out.println(me+"Sending these actuator data "+toAr(data));

		mess.setData(data);		
		actuator.publish(mess);
	}

	private float[] generateData(){

		float[] out = new float[2];
		for(int i=0;i<out.length; i++)
			out[i] = 145*r.nextFloat();

		return out;
	}

	private String toAr(float[] f){
		String out = "";
		for(int i=0;i<f.length; i++)
			out = out+"  "+f[i];
		return out;		
	}
}
