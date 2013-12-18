package vivae.ros.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

import org.jdesktop.swingx.util.OS;

/**
 * We want to be able to preserve the data (scenarios) after the installation, 
 * but also be able to access them from Eclipse.
 * 
 * http://gradle.org/docs/current/userguide/application_plugin.html
 * 
 * @author Jaroslav Vitku
 *
 */
public class DataLoader{

	public static final String me = "[DataLoader] ";
	
	// these relative locations can hold the folder data
	// if ran from class file, the second will be used, if ran from installation the 1st.1
	protected static final String[] unixLocations = {
		"../src/dist/", 
		"bin/", 
		"src/main/resources/",
		"../src/main/resources/",
		"../../src/main/resources/",
		"../../../src/main/resources/",
		"build/resources/test/"};
	
	protected static final String[] winLocations = {"..\\", "..\\src\\dist\\"};
	
	//TODO check support for windows..

	public static String getProjectAbsPath(){
		String path;
		// get my location
		URL location = DataLoader.class.getProtectionDomain().getCodeSource().getLocation();
		// I know that I am in vivae.ros.util.. so
		path = location.getFile();

		path = detachName(path);

		return path;
	}

	/**
	 * if we are in jar, removes the filename
	 * @param path complete path
	 * @return absolute path with removed jar filename if there was
	 */
	private static String detachName(String path){
		String slash;
		char slashChar;
		//unix
		if(OS.isLinux() || OS.isMacOSX()){
			slash="/";
			slashChar='/';
			//win?
		}else{ 
			slash="\\";
			slashChar='\\';
		}
		if(path.charAt(path.length()-1)==slashChar){
			return path;
		}else{
			String[] arr = path.split(slash);
			String out = "";
			for(int i=0;i<arr.length-1; i++){
				if(i==0)
					out=arr[i];
				else
					out=out+slash+arr[i];
			}
			return out+slash;
		}
	}




	/**
	 * Give me string describing path to resource, I will try to find it 
	 * on the list of possible (relative) locations to my location.
	 * If the file is not found in any location, I will throw error?
	 * @param s filename that you like to load, e.g. "data/scenarios/arena1.svg"
	 * @return absolute path to the file if found
	 * @throws Exception
	 */
	public static String locateFile(String s) throws FileNotFoundException{
		String[] locations;

		if(OS.isLinux() || OS.isMacOSX()){
			locations = unixLocations;
		}else{
			locations = winLocations;
		}
		//System.out.println("I am absolutely here: "+getProjectAbsPath());
		String abs;
		// try to find file, if found, return his name with absolute path
		for(int i=0; i<locations.length; i++){
			abs = getProjectAbsPath()+locations[i]+s;
			//System.out.println("trying: "+abs);
			File f = new File(abs);
			if(f.exists()){
				//System.out.println("FOUND here: "+abs);
				return abs;
			}
		}
		
		System.err.println(me+" I can see only these files: ");
		ClasspathPrinter.printListFiles();
		throw new FileNotFoundException(me+" this file not found in any of given "
				+ "locations: "+s+"\nDataLoader: my directory is: "+getProjectAbsPath());
	}
	
	public static boolean fileCanBeLocated(String s){
		String []locations; 
		if(OS.isLinux() || OS.isMacOSX()){
			locations = unixLocations;
		}else{
			locations = winLocations;
		}
		String abs;
		for(int i=0; i<locations.length; i++){
			abs = getProjectAbsPath()+locations[i]+s;
			File f = new File(abs);
			if(f.exists()){
				return true;
			}
		}
		return false;
	}

}
