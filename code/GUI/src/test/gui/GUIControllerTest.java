package test.gui;

import static org.junit.Assert.*;

import java.util.List;

import gui.GUIController;
import gui.GUIElement;
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
            if (c != null) {
                guiController.setSelectedCategory(c.getId(), false);
            }
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
        assertTrue(c.toString().contains("Politics"));
    }

    @Test
    public void testGetAccounts() {
        List<Account> list = guiController.getAccounts("Barack");
        assertTrue(list.get(0).toString().equals("Barack Obama"));
    }
    
    @Test
    public void testGetDataByLocation() {
    	List<Account> accounts = guiController.getAccounts("Katy");
    	guiController.setSelectedAccount(accounts.get(0).getId(), true);
    	TweetsAndRetweets tr = guiController.getDataByLocationAndDate();
    	int no = 0;
    	String loc = "";
    	for (mysql.result.Retweets r : tr.getRetweets()) {
    		if (no < r.getCounter()) {
    			loc = r.getLocationCode();
    			no = r.getCounter();
    		}
    	}
    	System.out.println(loc);
    	assertTrue(loc.equals("US"));
    }
    
    @Test
    public void testGetLocations() {
        List<Location> list = guiController.getLocations();
        assertTrue(list.size() > 0);
    }

    @Test
    public void testSelectCategory() {
        Category c = guiController.getCategoryRoot("Music").getChilds().get(0);
        guiController.setSelectedCategory(c.getId(), true);
        assertTrue(guiController.getSelectedCategories().contains(c));
        guiController.setSelectedCategory(c.getId(), false);
    }

    @Test
    public void testDeselectCategory() {
        Category c = guiController.getCategoryRoot("Music").getChilds().get(0);
        guiController.setSelectedCategory(c.getId(), true);
        guiController.setSelectedCategory(c.getId(), false);
        assertTrue(!guiController.getSelectedCategories().contains(c));
    }

    @Test
    public void testSelectLocation() {
        List<Location> list = guiController.getLocations();
        guiController.setSelectedLocation(list.get(list.size() - 1).getId(),
                true);
        assertTrue(guiController.getSelectedLocations().contains(
                list.get(list.size() - 1)));
        guiController.setSelectedLocation(list.get(list.size() - 1).getId(),
                false);
    }

    @Test
    public void testDeselectLocation() {
        List<Location> list = guiController.getLocations();
        guiController.setSelectedLocation(list.get(list.size() - 1).getId(),
                true);
        assertTrue(guiController.getSelectedLocations().contains(
                list.get(list.size() - 1)));
        guiController.setSelectedLocation(list.get(list.size() - 1).getId(),
                false);
        assertTrue(!guiController.getSelectedLocations().contains(
                list.get(list.size() - 1)));
    }

    @Test
    public void testSelectAccount() {
        List<Account> list = guiController.getAccounts("Barack");
        Account a = list.get(0);
        boolean found = false;
        guiController.setSelectedAccount(a.getId(), true);
        for (Account b : guiController.getSelectedAccounts()) {
            if (b.equals(a)) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testDeselectAccount() {
        Account a = guiController.getAccounts("Obama").get(0);
        guiController.setSelectedAccount(a.getId(), true);
        guiController.setSelectedAccount(a.getId(), false);
        assertFalse(guiController.getSelectedAccounts().contains(a));
    }

    @Test
    public void testSubscribe() throws InterruptedException {
        class TestGUIElement extends GUIElement {
            private boolean updated = false;

            @Override
            public void update(UpdateType type) {
                if (type == UpdateType.CATEGORY_SELECTION) {
                    updated = true;
                }
            }

            public boolean isUpdated() {
                return updated;
            }

        }
        TestGUIElement e = new TestGUIElement();
        guiController.subscribe(e);
        guiController.setSelectedCategory(guiController.getCategoryRoot()
                .getId(), true);
        System.out.println("assertTrue(e.isUpdated());");
        assertTrue(e.isUpdated());
        Thread.sleep(1000);
    }
}
