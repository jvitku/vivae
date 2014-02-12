package ctu.nengoros.modules.vivae;

import java.util.HashMap;

import ca.nengo.model.Node;
import ca.nengo.model.StructuralException;
import ca.nengo.util.ScriptGenException;
import ctu.nengoros.comm.nodeFactory.NodeGroup;
import ctu.nengoros.exceptions.ConnectionException;
import ctu.nengoros.modules.impl.DefaultNeuralModule;
import ctu.nengoros.network.common.exceptions.StartupDelayException;

public class VivaeNeuralModule extends DefaultNeuralModule{

	private static final long serialVersionUID = 1L;
	public static final String me = "[VivaeNeuralModule] ";

	private SimulationControls sc;			// control the Vivae simulator over the ROS network

	protected int slept;
	protected final int sleeptime = 10;
	protected final int maxSleep = 2000;	// max wait time to services to initialize

	
	public VivaeNeuralModule(String name, NodeGroup group) throws ConnectionException,
	StartupDelayException {
		super(name, group);

		// connect the simulation controller to the ROS network
		try {
			sc = new SimulationControls(this, super.mc.getModem().getConnectedNode());
			
		} catch (ConnectionException e) {
			sc=null;
			System.err.println(me+"my modem was not connected. Probably ROS communication error!!");
			e.printStackTrace();
		}
	}
	
	public SimulationControls getControls(){
		this.awaitStarted();
		return sc;
	}
	
	public void addAgent(String name) throws StructuralException{
		this.awaitStarted();
		sc.addAgent(name);
	}
	
	public VivaeAgent getAgent(String name){
		this.awaitStarted();
		
		if(sc.getAgents().containsKey(name)){
			return (VivaeAgent) sc.getAgents().get(name);
		}
		System.err.println("VivaeNeuron: error: agent named: "+name+
				" is not found in this neuron !!");
		return null;
	}
	
	public void removeAgent(String name){
		this.awaitStarted();
		sc.removeAgent(name);
	}
	

	/**
	 * is called from Nengo while opening the simulation window
	 */
	@Override
	public void reset(boolean randomize) {
		this.awaitStarted();
		// TODO: Vivae has to be reloaded in order to work correctly with the Nengo.. ?
		sc.callReset(); 
		//mc.resetModem();
		mc.reset(randomize);
	}
	
	/**
	 * being deleted from Nengo GUI?
	 */
	@Override
	public void notifyAboutDeletion() {
		this.awaitStarted();
		System.out.print(me+"OK, I am being deleted, will close ROS componnets and "+getName());
		//sc.callStopSimulation();
		sc.callDestroySimulation();
		mc.stop(); 
	}

	@Override
	public Node[] getChildren() { return null;	}

	@Override
	public String toScript(HashMap<String, Object> scriptData)
			throws ScriptGenException {
		// TODO Implement toScript for VivaeNeuralModule
		System.err.println("TODO: toScript not implemented so far!");
		return null;
	}
	
	@Override
	public void awaitStarted(){
		slept=0;
		while(sc==null){
			try {
				Thread.sleep(sleeptime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(sleeptime*slept++ >maxSleep){
				System.err.println(me+"my SimulationControls not started within " +
						"max. time of "+maxSleep+"ms, giving up !!!");
				return;
			}
		}
	}

}
