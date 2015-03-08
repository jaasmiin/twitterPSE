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

/**
 * class to test some basic functionality of the GUIController
 * 
 * @author Maximilian Awiszus
 * 
 */
public class GUIControllerTest {

    private static GUIController guiController;

    /**
     * Create connection with db.
     * 
     * @throws InterruptedException
     *             can be thrown
     */
    @BeforeClass
    public static void setUpBeforeClass() throws InterruptedException {
        // Initialize Java FX
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

    /**
     * Close application after all tests.
     * 
     * @throws Exception
     *             can be thrown
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        guiController.close();
    }

    /**
     * Clear selection before each test.
     */
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

    /**
     * Test to load categories containing 'Politics'..
     */
    @Test
    public void testGetCategories() {
        Category c = guiController.getCategoryRoot("Politics");
        while (!c.getChilds().isEmpty()) {
            c = c.getChilds().get(0);
        }
        assertTrue(c.toString().contains("Politics"));
    }

    /**
     * Test loading accounts containing 'Barack'.
     */
    @Test
    public void testGetAccounts() {
        List<Account> list = guiController.getAccounts("Barack");
        assertTrue(list.get(0).toString().equals("Barack Obama"));
    }

    /**
     * Test loading data grouped by location containing accounts with 'Katy'.
     * 
     * @throws InterruptedException
     *             if problem with waiting
     */
    @Test
    public void testGetDataByLocation() throws InterruptedException {
        GUIElement e = new GUIElement() {
            private String locationCode = "";

            @Override
            public void update(UpdateType type) {
                if (type == UpdateType.TWEET_BY_LOCATION_BY_DATE) {
                    TweetsAndRetweets tr = guiController
                            .getDataByLocationAndDate();
                    int no = 0;
                    String loc = "";
                    for (mysql.result.Retweets r : tr.getRetweets()) {
                        if (!r.getLocationCode().equals("0")
                                && no < r.getCounter()) {
                            loc = r.getLocationCode();
                            no = r.getCounter();
                        }
                    }
                    locationCode = loc;
                }
            }

            @Override
            public String toString() {
                return locationCode;
            }
        };
        guiController.subscribe(e);
        List<Account> accounts = guiController.getAccounts("Katy");
        guiController.setSelectedAccount(accounts.get(0).getId(), true);
        while (e.toString().equals("")) {
            Thread.sleep(500);
        }
        System.err.println("Location code: " + e.toString());
        assertTrue(e.toString().equals("US"));
    }

    /**
     * Test getting all locations.
     */
    @Test
    public void testGetLocations() {
        List<Location> list = guiController.getLocations();
        assertTrue(list.size() > 0);
    }

    /**
     * Test selection a category.
     */
    @Test
    public void testSelectCategory() {
        Category c = guiController.getCategoryRoot("Music").getChilds().get(0);
        guiController.setSelectedCategory(c.getId(), true);
        assertTrue(guiController.getSelectedCategories().contains(c));
        guiController.setSelectedCategory(c.getId(), false);
    }

    /**
     * Test deselecting a category.
     */
    @Test
    public void testDeselectCategory() {
        Category c = guiController.getCategoryRoot("Music").getChilds().get(0);
        guiController.setSelectedCategory(c.getId(), true);
        guiController.setSelectedCategory(c.getId(), false);
        assertTrue(!guiController.getSelectedCategories().contains(c));
    }

    /**
     * Test selecting a location.
     */
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

    /**
     * Test deselecting a location.
     */
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

    /**
     * Test selecting an account.
     */
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

    /**
     * Test deselecting an account.
     */
    @Test
    public void testDeselectAccount() {
        Account a = guiController.getAccounts("Obama").get(0);
        guiController.setSelectedAccount(a.getId(), true);
        guiController.setSelectedAccount(a.getId(), false);
        assertFalse(guiController.getSelectedAccounts().contains(a));
    }

    /**
     * Test subscribing at the GUIController and getting an update within 1s.
     * 
     * @throws InterruptedException
     *             can be thrown
     */
    @Test
    public void testSubscribe() throws InterruptedException {
        TestGUIElement e = new TestGUIElement();
        guiController.subscribe(e);
        guiController.setDontLoadFromDB(true);
        Thread.sleep(1000);
        assertTrue(e.isUpdated());
        guiController.setDontLoadFromDB(false);
    }

    /**
     * Class for testing the subscription.
     * 
     * @author Maximilian Awiszus
     */
    class TestGUIElement extends GUIElement {
        private boolean updated = false;

        @Override
        public void update(UpdateType type) {
            if (type == UpdateType.DONT_LOAD) {
                updated = true;
            }
        }

        /**
         * True if element has been updated with type CATEGORY_SELECTION
         * 
         * @return true if element has been updated with type CATEGORY_SELECTION
         */
        public boolean isUpdated() {
            return updated;
        }

    }
}
