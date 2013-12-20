package vivae.ros.simulatorServer.services;

import org.ros.exception.ServiceException;
import org.ros.node.service.ServiceResponseBuilder;

import vivae.SimControllerRequest;
import vivae.SimControllerResponse;
import vivae.ros.simulator.SimulatorController;
import vivae.ros.simulatorServer.SimulatorServer;

public class SimControlServiceResponseBuilder implements 
ServiceResponseBuilder<vivae.SimControllerRequest, vivae.SimControllerResponse>{

	public static final String me = "[SimControlService] ";
	private final SimulatorController sc;

	public SimControlServiceResponseBuilder(SimulatorController sc){
		this.sc = sc;
	}

	@Override
	public void build(SimControllerRequest req, SimControllerResponse resp)
			throws ServiceException {
		//System.out.println(me+"requested this: "+req.getWhat());

		if(req.getWhat().equalsIgnoreCase(SimCommands.INIT)){
			boolean result = sc.init();
			//System.out.println(me+"called request for init, responding, result: "+result);
			resp.setOk(result);

		}else if(req.getWhat().equalsIgnoreCase(SimCommands.START)){
			sc.start();
			//System.out.println(me+"called request for start, responding ok");
			resp.setOk(true);

		}else if(req.getWhat().equalsIgnoreCase(SimCommands.STOP)){
			sc.stop();
			//System.out.println(me+"called request for stop, responding ok");
			resp.setOk(true);

		}else if(req.getWhat().equalsIgnoreCase(SimCommands.DESTROY)){
			sc.destroy();
			//System.out.println(me+"called request for destroy, responding ok");
			resp.setOk(true);

		}else if(req.getWhat().equalsIgnoreCase(SimCommands.RESET)){
			sc.reset();
			resp.setOk(true);

		}else if(req.getWhat().equalsIgnoreCase(SimCommands.SETVISIBLE)){
			sc.setVisible(true);
			resp.setOk(true);	
		}else if(req.getWhat().equalsIgnoreCase(SimCommands.SETINVISIBLE)){
			sc.setVisible(false);
			resp.setOk(true);
		}else{
			System.err.println(me+"This request: \""+req.getWhat()+"\" on SimControlService "
					+ "not recognized, only the following commands are supported: \n"
					+"-----------\n"+SimCommands.getAll()+"\n----------");
		}
	}
}