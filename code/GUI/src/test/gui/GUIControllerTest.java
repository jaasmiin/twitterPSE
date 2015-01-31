package test.gui;

import static org.junit.Assert.fail;

import java.util.List;

import gui.GUIController;
import mysql.result.Account;
import mysql.result.Category;
import mysql.result.Location;
import mysql.result.TweetsAndRetweets;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class GUIControllerTest {

	private static GUIController guiController;
	
	@BeforeClass
	public static void setUpBeforeClass() throws InterruptedException {
	    // Initialise Java FX
	    System.out.printf("About to launch FX App\n");
	    Thread t = new Thread("JavaFX Init Thread") {
	        @Override
			public void run() {
	            GUIController.main(null);
	        }
	    };
	    t.setDaemon(true);
	    t.start();
	    System.out.printf("FX App thread started\n");
	    Thread.sleep(1000);
	    guiController = GUIController.getInstance();
	    while (!guiController.isReady()) {
			Thread.sleep(1000);
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		guiController.close();
	}
	
	@Before
	public void setUpBefore() {
		List<Account> aList = guiController.getSelectedAccounts();
		List<Category> cList = guiController.getSelectedCategories();
		List<Location> lList = guiController.getSelectedLocations();
		for (Account a : aList) {
			guiController.setSelectedAccount(a.getId(), false);
		}
		for (Category c : cList) {
			guiController.setSelectedCategory(c.getId(), false);
		}
		for (Location l : lList) {
			guiController.setSelectedLocation(l.getId(), false);
		}
	}
	
	@Test
	public void testGetCategories() {
		Category c = guiController.getCategoryRoot("Politics");
		while (!c.getChilds().isEmpty()) {
			c = c.getChilds().get(0);
		}
		assert(c.toString().contains("Plotitics"));
	}
	
	@Test
	public void testGetAccounts() {
		List<Account> list = guiController.getAccounts("Barack");
		assert(list.get(0).toString().equals("Barach Obama"));
	}

	@Test
	public void testGetLocations() {
		List<Location> list = guiController.getLocations();
		assert(list.size() > 0);
	}
	
	@Test
	public void testSelectCategory() {
		Category c = guiController.getCategoryRoot("Music").getChilds().get(0);
		guiController.setSelectedCategory(c.getId(), true);
		assert(guiController.getSelectedCategories().contains(c));
	}
	
	@Test
	public void testDeselectCategory() {
		Category c = guiController.getCategoryRoot("Music").getChilds().get(0);
		guiController.setSelectedCategory(c.getId(), true);
		guiController.setSelectedCategory(c.getId(), false);
		assert(!guiController.getSelectedCategories().contains(c));
	}
	
	@Test
	public void testSelectLocation() {
		List<Location> list = guiController.getLocations();
		guiController.setSelectedLocation(list.get(list.size() - 1).getId(), true);
		assert(guiController.getSelectedLocations().contains(list.get(list.size() - 1)));
	}
	
	@Test
	public void testDeselectLocation() {
		List<Location> list = guiController.getLocations();
		System.out.println(list.size());
		guiController.setSelectedLocation(list.get(list.size() - 1).getId(), true);
		assert(guiController.getSelectedLocations().contains(list.get(list.size() - 1)));
		guiController.setSelectedLocation(list.get(list.size() - 1).getId(), false);
		assert(!guiController.getSelectedLocations().contains(list.get(list.size() - 1)));
	}
	@Test
	public void testSelectAccount() {
		List<Account> list = guiController.getAccounts("Barack");
		Account a = list.get(0);
		guiController.setSelectedAccount(a.getId(), true);
		assert(guiController.getSelectedAccounts().contains(a));
	}
	
	@Test
	public void testDeselectAccount() {
		Account a = guiController.getAccounts("Obama").get(0);
		guiController.setSelectedAccount(a.getId(), true);
		guiController.setSelectedAccount(a.getId(), false);
		assert(guiController.getSelectedAccounts().contains(a));
	}
	
	@Test
	public void testGetDataByLocation() {
		Account a = guiController.getAccounts("Obama").get(0);
		guiController.setSelectedAccount(a.getId(), true);
		TweetsAndRetweets tar = guiController.getDataByLocation();
		assert(tar.retweets.size() > 1);
	}

	@Test
	public void testGetErrorMessage() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetDateRange() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddAccountToWatch() {
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
	public void testSubscribe() {
		fail("Not yet implemented");
	}	
}
