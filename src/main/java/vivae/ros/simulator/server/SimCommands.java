package vivae.ros.simulator.server;

/**
 * Class for storing the commands that control the simulation.
 * 
 * These are accepted by the SimControlServiceResponseBuilder.
 * 
 * @author Jaroslav Vitku
 *
 */
public class SimCommands {

	public static final String [] COMMANDS = new String[]{
		"init", 
		"start",
		"stop", 
		"reset",
		"destroy",
		"setvisible",
	 	"setinvisible"};

	public static final String INIT = COMMANDS[0];
	public static final String START = COMMANDS[1];
	public static final String STOP = COMMANDS[2];
	public static final String RESET = COMMANDS[3];
	public static final String DESTROY = COMMANDS[4];
	public static final String SETVISIBLE = COMMANDS[5];
	public static final String SETINVISIBLE = COMMANDS[6];

	public static String getAll(){
		String out = COMMANDS[0];
		for(int i=1; i<COMMANDS.length; i++)
			out = out +"\n"+COMMANDS[i];
		return out;
	}
}
