package vivae.ros.simulator.server.services;

import org.ros.exception.ServiceException;
import org.ros.node.service.ServiceResponseBuilder;

import vivae.LoadMapRequest;
import vivae.LoadMapResponse;
import vivae.ros.simulator.engine.Simulation;

/**
 * This service provides the ability to load map in the ViVae arena over the ROS network.
 *  
 * @author Jaroslav Vitku
 *
 */
public class LoadMapServiceResponseBuilder implements 
ServiceResponseBuilder<vivae.LoadMapRequest, vivae.LoadMapResponse>{

	public static final String me = "[LoadMapService] ";
	
	private final Simulation sim;

	public LoadMapServiceResponseBuilder(Simulation sim){
		this.sim = sim;
	}

	@Override
	public void build(LoadMapRequest req, LoadMapResponse resp) throws ServiceException {

		java.lang.String name = req.getName();	// get name of map
		
		System.out.println(me+"Getting request to load this: "+name+" loading the map");

		if(sim.loadMap(name)){
			resp.setLoadedOK(true);

		}else{
			System.err.println(me+"could not load map named: "+name);
			System.out.println(me+"--------------- Working Directory = " +
					System.getProperty("user.dir"));
			resp.setLoadedOK(false);
		}
	}
}