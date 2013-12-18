package test.dataLoader;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import vivae.ros.util.DataLoader;
import vivae.ros.util.MapLoader;

public class ScenarioLoading {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	
	/**
	 * The resource files may have different location in these cases:
	 * -eclipse test
	 * -gradlew test
	 * -gradlew build
	 * -run application from classes
	 * -run installed application from jar
	 * 
	 * So DataLoader tries to find a requested file in all possible
	 * absolute paths. If not found, will return null;
	 */
	@Test
	public void defaultDataLoader() {
		String s;
		try {
			s = DataLoader.locateFile("data/scenarios/arena1.svg");
			System.out.println("This map was found in this path: "+s);

			// check if the one was returned
			assertTrue(stringEndsWith(s,"arena1.svg"));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	/**
	 * Find a given map
	 */
	@Test
	public void defaultMapLoader() {
		String s;
		try {
			s = MapLoader.locateMap("data/scenarios/arena2.svg");
			System.out.println("This map was found in this path: "+s);

			// check if the one was returned
			assertTrue(stringEndsWith(s,"arena2.svg"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail();
		}
	}

	
	
	/**
	 * Non-existing map, return the default one
	 */
	@Test
	public void defaultMapLoaderNotFound() {
		String s;
		try {
			s = MapLoader.locateMap("data/scenarios/arena2.svgggg");
			System.out.println("This map was found in this path: "+s);
			
			// check if the default one was returned
			assertTrue(stringEndsWith(s,MapLoader.DEF_MAP));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	
	@Test
	public void sub(){
		String lg = "data/scenarios/arena2.svg";
		String srch = "/arena2.svg";
		
		assertTrue(stringEndsWith(lg,srch));
		assertFalse(stringEndsWith(lg,srch+"X"));
	}
	
	private boolean stringEndsWith(String longString, String searchedSubstring){
		
		if(searchedSubstring.length() > longString.length())
			return false;
		
		int l = longString.length();
		int s = searchedSubstring.length();
		
		return (longString.substring(l-s).equalsIgnoreCase(searchedSubstring));
	}
	
	
	/**
	 * There are maps and tiles
	 */
	@Test
	public void loadTile(){
	}


}