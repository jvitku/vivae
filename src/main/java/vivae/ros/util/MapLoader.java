package vivae.ros.util;

import java.io.FileNotFoundException;

/**
 * Class for loading scenario files.
 * 
 * @author Jaroslav Vitku
 *
 */
public class MapLoader extends DataLoader{

	private static final String me = "[MapLoader] ";
	public static final String DEF_MAP = "data/scenarios/arena1.svg"; 


	public static String locateMap(String name) throws FileNotFoundException{
		try{
			return DataLoader.locateFile(name);

		}catch(FileNotFoundException e){
			System.err.println(me+"File: "+name+" not found!");

			// try to load default map, if not possible, throw exception, this is problem
			try{
				return DataLoader.locateFile(DEF_MAP);
			}catch(FileNotFoundException er){
				System.err.println(me+" I can see only these files: ");
				ClasspathPrinter.printListFiles();
				throw new FileNotFoundException(me+"Even the default map could not be found!"+
						"probably some errorn on the classpath! \n My directory: "
						+ClasspathPrinter.getPWD()+"\n");
			}
		}
	}
	
	
	public static boolean defaultMapFound(){
		try{
			locateMap(DEF_MAP);
			return true;
		}catch(Exception e){
			return false;
		}
	}
}

