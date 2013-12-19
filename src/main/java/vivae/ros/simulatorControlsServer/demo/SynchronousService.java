package vivae.ros.simulatorControlsServer.demo;

import org.ros.exception.RemoteException;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;

/**
 * This class should generically define the synchronous ROS service. 
 * You call service and method will return response if received in 
 * predefined time or null if request did not make it, or exception triggered. 
 * 
 * @author Jaroslav Vitku
 *
 * @param <R> request
 * @param <E> response
 */
public class SynchronousService<R,E> implements ServiceResponseListener<E>{

	private final int w = 50;
	
	public volatile boolean responseReceived;
	public int waitTime = 2000;
	int waited;
	
	private R request;
	private E response;
	
	ServiceClient<R,E> sc;
	
	public SynchronousService(ServiceClient<R,E> sc){
		responseReceived = false;
		waited = 0;
		this.sc = sc;
	}

	public SynchronousService(ServiceClient<R,E> sc, int waitMax){
		this(sc);
		waitTime = waitMax;
	}
	
	private E waitForResponse(){
		while(true){
			if(responseReceived){
				System.out.println("Response received, returning it");
				return response;
			}
			try {
				Thread.sleep(w);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			waited += w;
			if(waited>waitTime){
				System.err.println("SpawnRequester: agent not spawned in "+waitTime+" miliseconds " +
						", giving up");
				return null;
			}
		}
	}
	
	public E callService(R request){
		this.request = request;
		
		// call service with this listener
		sc.call(this.request, this);
		
		// wait for response
		return waitForResponse();
	}
	
	public R getRequest(){
		return sc.newMessage();
	}

	/**
	 * part of ServiceResponseListener - service failed
	 */
	@Override
	public void onFailure(RemoteException e) {
		responseReceived = true;
	}

	/**
	 * part of ServiceResponseListener - response received
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onSuccess(Object resp) {
		response = (E)resp;
		responseReceived =true;
	}
}
