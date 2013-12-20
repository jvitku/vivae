package vivae.ros.util;

import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

/**
 * OK, Loading svg files from JAR is TODO, @see probably:
 * http://stackoverflow.com/questions/13249613/cannot-read-an-image-in-jar-file
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
