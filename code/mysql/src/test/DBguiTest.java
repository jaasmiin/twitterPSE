package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import mysql.AccessData;
import mysql.DBgui;
import mysql.result.Account;
import mysql.result.Category;
import mysql.result.Location;
import mysql.result.TweetsAndRetweets;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import util.LoggerUtil;

/**
 * class to test the database-access of the gui
 * 
 * @author Holger Ebhart
 * 
 */
public class DBguiTest {

    private DBgui dbg;
    private Logger log;
    private AccessData access;

    // test data
    private List<Integer> list1;
    private List<Integer> list0;

    /**
     * initialize this test-class and prepare test-data
     */
    public DBguiTest() {
        try {
            log = LoggerUtil.getLogger("TestLog");
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }

        list1 = new ArrayList<Integer>();
        list1.add(1);
        list0 = new ArrayList<Integer>();
    }

    /**
     * prepares a database-connection and the database for the next test-case
     */
    @Before
    public void setUp() {
        try {
            access = new AccessData("localhost", "3306", "twittertest", "root",
                    "root");
            dbg = new DBgui(access, log);
            dbg.connect();
        } catch (InstantiationException | IllegalAccessException
                | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * test of getting the locations
     */
    @Test
    public void testGetLocations() {
        List<Location> l = dbg.getLocations();

        assertEquals(8, l.size());
    }

    /**
     * test of getting the categories
     */
    @Test
    public void testGetCategories() {
        Category c = dbg.getCategories();

        assertEquals("ROOT", c.toString());
        // check parent
        assertEquals(7, c.getChilds().size());
        assertEquals(2, c.getChilds().get(0).getChilds().size());
    }

    /**
     * test of getting accounts by a search String
     */
    @Test
    public void test1GetAccounts() {
        List<Account> a = dbg.getAccounts("Tester3");
        assertEquals(1, a.size());
        assertEquals(a.get(0).getName(), "Tester3");
    }

    /**
     * test of getting accounts by a search String
     */
    @Test
    public void test2GetAccounts() {
        List<Account> a = dbg.getAccounts("Tester");
        assertEquals(6, a.size());
    }

    /**
     * test of getting accounts by a search String
     */
    @Test
    public void test3GetAccounts() {
        List<Account> a = dbg.getAccounts("er0");
        assertEquals(1, a.size());
        assertEquals(a.get(0).getName(), "Tester0");
    }

    /**
     * test of getting accounts by a search String
     */
    @Test
    public void test4GetAccounts() {
        List<Account> a = dbg.getAccounts("blabla");
        assertEquals(0, a.size());
    }

    /**
     * test for getting a sum of data
     */
    @Test
    public void test1GetSumOfData() {
        TweetsAndRetweets test = dbg.getSumOfData(list0, list0, list0, false);
        assertEquals(0, test.getTweets().size());
        assertEquals(0, test.getRetweets().size());
    }

    /**
     * test for getting a sum of data
     */
    @Test
    public void test2GetSumOfData() {
        TweetsAndRetweets test = dbg.getSumOfData(list1, list1, list0, false);

        assertEquals(1, test.getTweets().size());
        assertEquals(6, test.getTweets().get(0).getCounter());
        assertNull(test.getTweets().get(0).getDate());
        assertEquals(3, test.getRetweets().size());
    }

    /**
     * test for getting a sum of data
     */
    @Test
    public void test2GetSumOfDataWithDates() {
        TweetsAndRetweets test = dbg.getSumOfData(list1, list1, list0, true);

        assertEquals(3, test.getTweets().size());
        assertEquals(3, test.getTweets().get(0).getCounter());
        assertEquals(2, test.getTweets().get(1).getCounter());
        assertEquals(1, test.getTweets().get(2).getCounter());
        assertEquals(4, test.getRetweets().size());
        assertEquals(10, test.getRetweets().get(3).getCounter());
    }

    /**
     * test for getting all data
     */
    @Test
    public void test1GetAllData() {
        assertEquals(new ArrayList<Account>(),
                dbg.getAllData(list0, list0, list0, false));
    }

    /**
     * test for getting all data
     */
    @Test
    public void test2GetAllDataWithDates() {
        List<Account> res = dbg.getAllData(list1, list1, list0, true);

        assertEquals(2, res.size());
        assertEquals(1, res.get(0).getTweets().size());
        assertEquals(2, res.get(1).getTweets().size());
        assertEquals(2, res.get(0).getRetweets().size());
        assertEquals(3, res.get(1).getRetweets().size());
    }

    /**
     * test for getting all data
     */
    @Test
    public void test2GetAllData() {
        List<Account> res = dbg.getAllData(list1, list1, list0, false);

        assertEquals(2, res.size());
        assertEquals(1, res.get(0).getTweets().size());
        assertEquals(1, res.get(1).getTweets().size());
        assertEquals(2, res.get(0).getRetweets().size());
        assertEquals(2, res.get(1).getRetweets().size());
    }

    /**
     * test for right sum of retweets per country
     */
    @Test
    public void testGetAllRetweetsPerCountry() {
        HashMap<String, Integer> h = dbg.getAllRetweetsPerLocation();
        // TODO
        assertTrue(h.containsKey(""));
        assertTrue(h.containsKey(""));
        assertEquals(2, h.get("f"));
        assertEquals(2, h.get("d"));
    }

    /**
     * disconnect from the database and clear the database
     */
    @After
    public void tearDown() {
        dbg.disconnect();
    }

}
