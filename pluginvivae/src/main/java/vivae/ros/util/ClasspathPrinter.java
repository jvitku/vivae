package vivae.ros.util;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

/** @author Ram, Matthews */
public class ClasspathPrinter {

	public final static String pathSep = System.getProperty("path.separator");
	public final static String list = System.getProperty("java.class.path");

	public static void main(final String[] args) throws Throwable {
		printListFiles();
	}

	public static void listClasses(){
		ClassLoader cl = ClassLoader.getSystemClassLoader();

		URL[] urls = ((URLClassLoader)cl).getURLs();

		for(URL url: urls){
			System.out.println(url.getFile());
		}
	}

	public static void printClasspath(){
		String classpath = System.getProperty("java.class.path");
		System.out.println("Classpath is: "+classpath);
	}
	
	public static void printPWD(){
		  System.out.println("Working Directory = " + System.getProperty("user.dir"));
	}
	
	public static String getPWD(){
		  return System.getProperty("user.dir");
	}
	
	/**
	 * List all files found on the classpath.
	 */
	public static void printListFiles(){
		for (final String path : list.split(pathSep)) {
			final File object = new java.io.File(path);
			if( object.isDirectory()) 
				ls(object);
			else 
				System.out.println(object);
		}
	}

	/** list recursively */
	private static void ls(File f) { 
		File list[] = f.listFiles();
		for (File file : list) {

			if (file.isDirectory()) 
				ls(file);
			else 
				System.out.println(file);
		}
	}
}
