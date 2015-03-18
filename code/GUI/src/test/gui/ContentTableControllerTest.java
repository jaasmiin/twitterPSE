package test.gui;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.net.URL;

import javafx.scene.control.TableView;
import gui.GUIElement.UpdateType;
import gui.table.ContentTableController;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class for ContentTableController
 * 
 * @author Philipp
 *
 */
public class ContentTableControllerTest {

    private static GuiControllerMock guiController;
    private static ContentTableController table;
    private static Field tableView;
    private static TableView<Object> contentTable;
    

    /**
     * Create GuiControllerMock and load ContentTableController
     */
    @SuppressWarnings("unchecked")
	@BeforeClass
    public static void setUpBeforeClass() {
    	guiController = new GuiControllerMock();
    	guiController.setDontLoadFromDB(true);
    	
    	
    	try {
			tableView = ContentTableController.class.getDeclaredField("table");
		} catch (NoSuchFieldException | SecurityException e) {			
			e.printStackTrace();
		}
    	tableView.setAccessible(true);
    	
    	System.out.println("Loading ContentTableController");
    	
    	URL adress = ContentTableController.class.getResource("ContentTableView.fxml");    	
    	ContentTableLoader.setComponent(adress);
    	
    	Thread starterThread = new Thread("Application Starter Thread") {
    		
    		public void run() {
    			ContentTableLoader.main(null);
    		}
    	};
    	
    	starterThread.setDaemon(true);
    	starterThread.start();
    	
    	// wait until starterThread has initialized controller
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	Object maybeTable = ContentTableLoader.getController();
    	
    	if (maybeTable instanceof ContentTableController) {
    		System.out.println("Set subscriber");
    		table = (ContentTableController) maybeTable;
    		guiController.subscribe(table);
    	}  
    	
    	contentTable = null;
    	try {
			contentTable = (TableView<Object>) tableView.get(table);			
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Close application after all tests.
     * @throws Exception can be thrown
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    	guiController.close();
    }
    
    /**
     * clear the tableView and the selected accounts in the GUIController mock
     */
    @Before
    public void setUpBefore() {
    	guiController.clear();
    	contentTable.getItems().clear();  	
    }

    /**
     * Called after execution of a test case
     */
	@After
	public void tearDown() {

	}

	
    /**
     * Tests if location columns are added in correct order
     */
    @Test
    public void testUpdateLocation() {
    	System.out.println("Test1: UpdateLocation");
    	table.update(UpdateType.LOCATION);
    	
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
    	int locNumber = contentTable.getColumns().size() - 3;
    	System.out.println("locNumber = " + locNumber);
    	
    	boolean equalLocs = true;
    	String names[] = guiController.getLocationNames();
    	String columnName = null;
    	for (int i = 0; i < names.length; i++) {
    		// start after "account", "follower" and "total" column
    		columnName = contentTable.getColumns().get(i + 3).getText();
    		equalLocs &= names[i].equals(columnName);
    	}
    	
    	assertTrue(equalLocs);
    	assertTrue(locNumber == 5);   	
    }
	
    /**
     * Test selection of one account.
     */
    @Test
    public void testInsertOneAccount() {
    	System.out.println("Test2: InsertAccount");   	
    	guiController.setSelectedAccount(0, true);
    	
    	// Wait until updates arrive
    	try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
    	
    	int rowNumber = contentTable.getItems().size();
    	int columnNumber = contentTable.getColumns().size();
    	
    	System.out.println("RowNumber = " + rowNumber + ", ColumnNumber = " + columnNumber);
    	
    	String contentName = contentTable.getColumns().get(0).getCellData(0).toString();
    	
    	
    	
    	
    	assertEquals("Number of Rows is equal to 1 : ", 1, rowNumber);
    	assertEquals("Name of Account is TestAccount0",  "TestAccount0", contentName);
    }
	

}
