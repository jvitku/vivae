package vivae.ros.util;

import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

/**
 * OK, Loading svg files from JAR is TODO, @see probably:
 * 
 * https://www.google.cz/search?client=safari&rls=en&q=read+svg+from+jar+file&ie=UTF-8&oe=UTF-8&gws_rd=cr&ei=3aO1UuY2isO0BuqZgOgK
 * 
 * @author Jaroslav Vitku
 *
 */
public class DataLoaderJar {
	
	public static void main(String[] s){
		//LoadFromJar("data/scenarios/arena1.svg");
		
		DataLoaderJar dlj = new DataLoaderJar();
		dlj.LoadImage("arena1.svg");
	}
	
	/**
	 * This should load SVG from the jar file, m
	 * @param name
	 */
	public static void LoadFromJar(String name){
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream input = classLoader.getResourceAsStream("META-INF/test.properties");
		
		try {
			System.out.println("input is available??" +input.available());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void LoadImage(String imageName){
		try {
			ImageIO.read(getClass().getResource(imageName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

}
