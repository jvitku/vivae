package vivae.ros.util;

public class Util {
	
	private static final int defwt = 70;
	
	public static void waitLoop(int waittime){
		waitLoop(waittime, defwt);
	}
	
	public static void waitLoop(int waittime, int wt){
		int waited = 0;
		
		System.out.print("Waiting "+waittime+"ms ");
		while(waited<waittime){
			try {
				Thread.sleep(wt);
			} catch (InterruptedException e) { e.printStackTrace(); }
			waited +=wt;
			System.out.print(".");
		}
		System.out.println(" DONE!\n");
	}
}
