package test.gui;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import gui.GUIController;
import mysql.AccessData;
import mysql.DBgui;
import mysql.result.Category;
import mysql.result.Location;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class GUIControllerTest {

	private static GUIController guiController;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
//		guiController = GUIController.getInstance();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

	}

	@Test
	public void testGetCategories() {
//		List<Category> list;
//		list = guiController.getCategories();
//		assertTrue(list.size() > 1);
		DBgui db;
		try {
			db = new DBgui(new AccessData("172.22.214.133", "3306", "twitter", "root", "182cc4"), getLogger() );
			db.connect();
			db.getCategories();
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException | SecurityException | IOException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private Logger getLogger() throws SecurityException, IOException {
        Logger l = Logger.getLogger("logger");
        new File("LogFile.log").createNewFile();
        FileHandler fh = new FileHandler("LogFile.log", true);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
        l.addHandler(fh);
        // true: print output on console and into file
        // false: only store output in logFile
        l.setUseParentHandlers(false);
        return l;
    }
	
	@Test
	public void testGetCategoriesString() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetLocations() {
//		List<Location> list;
//		list = guiController.getLocations();
//		assertTrue(list.size() > 1);
	}

	@Test
	public void testGetLocationsString() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetCategory() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetLocation() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetSelectedAccounts() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetSelectedCategories() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetSelectedLocations() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetAccount() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetDataByAccount() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetDataByLocation() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetDateRange() {
		fail("Not yet implemented");
	}

	@Test
	public void testSubscribe() {
		fail("Not yet implemented");
	}

}
