package vivae.ros.simulator.server.services;

import org.ros.exception.ServiceException;
import org.ros.node.service.ServiceResponseBuilder;

import vivae.SimControllerRequest;
import vivae.SimControllerResponse;
import vivae.ros.simulator.engine.SimulatorController;
import vivae.ros.simulator.server.SimCommands;

public class SimControlServiceResponseBuilder implements 
ServiceResponseBuilder<vivae.SimControllerRequest, vivae.SimControllerResponse>{

	public static final String me = "[SimControlService] ";
	private final SimulatorController sc;

	public SimControlServiceResponseBuilder(SimulatorController sc){ this.sc = sc; }

	@Override
	public void build(SimControllerRequest req, SimControllerResponse resp)
			throws ServiceException {
		//System.out.println(me+"requested this: "+req.getWhat());

		if(req.getWhat().equalsIgnoreCase(SimCommands.INIT)){
			boolean result = sc.init();
			//System.out.println(me+"called request for init, responding, result: "+result);
			resp.setOk(result);

		}else if(req.getWhat().equalsIgnoreCase(SimCommands.START)){
			boolean result = sc.start();
			//System.out.println(me+"called request for start, responding with "+result);
			resp.setOk(result);

		}else if(req.getWhat().equalsIgnoreCase(SimCommands.STOP)){
			boolean result = sc.stop();
			//System.out.println(me+"called request for stop, responding with "+result);
			resp.setOk(result);

		}else if(req.getWhat().equalsIgnoreCase(SimCommands.DESTROY)){
			boolean result = sc.destroy();
			//System.out.println(me+"called request for destroy, responding with "+result);
			resp.setOk(result);

		}else if(req.getWhat().equalsIgnoreCase(SimCommands.RESET)){
			boolean result = sc.reset();
			resp.setOk(result);

		}else if(req.getWhat().equalsIgnoreCase(SimCommands.SETVISIBLE)){
			boolean result = sc.setVisible(true);
			resp.setOk(result);	
			
		}else if(req.getWhat().equalsIgnoreCase(SimCommands.SETINVISIBLE)){
			boolean result = sc.setVisible(false);
			resp.setOk(result);
			
		}else{
			System.err.println(me+"This request: \""+req.getWhat()+"\" on SimControlService "
					+ "not recognized, only the following commands are supported: \n"
					+"-----------\n"+SimCommands.getAll()+"\n----------");
		}
	}
}