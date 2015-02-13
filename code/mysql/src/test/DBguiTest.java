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

    private DBtest dbt;
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

            dbt = new DBtest(access, log);
            dbt.connect();

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

        assertEquals("ROOT (3)", c.toString());
        // check parent
        // assertEquals(6, c.getChilds().size());
        // assertEquals(2, c.getChilds().get(0).getChilds().size());
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

        assertTrue(h.containsKey("T0"));
        assertTrue(h.containsKey("T1"));
        assertEquals(8, (int) h.get("T0"));
        assertEquals(10, (int) h.get("T1"));
    }

    /**
     * test to get all data from a specified account (invalid parameters)
     */
    @Test
    public void test1GetAccount() {
        Account a = dbg.getAccount(0);
        assertNull(a);
    }

    /**
     * test to get all data from a specified account (invalid parameters)
     */
    @Test
    public void test2GetAccount() {
        Account a = dbg.getAccount(55);
        assertNull(a);
    }

    /**
     * test to get all data from a specified account
     */
    @Test
    public void test3GetAccount() {
        Account a = dbg.getAccount(4);
        assertTrue(a.isVerified());
        assertEquals("Tester3", a.getName());
        assertEquals(3, a.getFollower());
        assertEquals("url", a.getUrl());
        assertEquals(1, a.getCategoryIds().size());
        assertEquals(1, (int) a.getCategoryIds().get(0));
    }

    /**
     * test to get all data from a specified account
     */
    @Test
    public void test4GetAccount() {
        Account a = dbg.getAccount(5);
        assertTrue(a.isVerified());
        assertEquals("Tester4", a.getName());
        assertEquals(4, a.getFollower());
        assertEquals("url", a.getUrl());
        assertEquals(1, a.getCategoryIds().size());
        assertEquals(2, (int) a.getCategoryIds().get(0));
    }

    /**
     * test to update an invalid account
     */
    @Test
    public void test1SetLocation() {
        boolean res = dbg.setLocation(0, 2);
        assertTrue(!res);
    }

    /**
     * test to set invalid location
     */
    @Test
    public void test2SetLocation() {
        boolean res = dbg.setLocation(1, 0);
        assertTrue(!res);
    }

    /**
     * test to set invalid location
     */
    @Test
    public void test3SetLocation() {
        boolean res = dbg.setLocation(1, 888);
        assertTrue(!res);
    }

    /**
     * test to set location
     */
    @Test
    public void test4SetLocation() {
        boolean res = dbg.setLocation(2, 8);
        Account a = dbg.getAccount(2);
        dbt.executeQuery("UPDATE accounts SET LocationId=1 WHERE Id=2;");
        assertTrue(res);
        assertEquals("TP", a.getLocationCode());
    }

    /**
     * test to set location
     */
    @Test
    public void test5SetLocation() {
        boolean res = dbg.setLocation(6, 7);
        Account a = dbg.getAccount(6);
        dbt.executeQuery("UPDATE accounts SET LocationId=8 WHERE Id=6;");
        assertTrue(res);
        assertEquals("T5", a.getLocationCode());
    }

    /**
     * test to update an invalid account
     */
    @Test
    public void test1SetCategory() {
        boolean res = dbg.setCategory(0, 2);
        assertTrue(!res);
    }

    /**
     * test to set invalid category
     */
    @Test
    public void test2SetCategory() {
        boolean res = dbg.setCategory(1, 888);
        assertTrue(!res);
    }

    /**
     * test to set category
     */
    @Test
    public void test3SetCategory() {
        boolean res = dbg.setCategory(2, 8);
        Account a = dbg.getAccount(2);
        dbt.executeQuery("UPDATE accounts SET Categorized=0 WHERE Id=2;");
        dbt.executeQuery("DELETE FROM accountCategory WHERE AccountId=2 AND CategoryId=8;");
        assertTrue(res);
        assertEquals(2, a.getCategoryIds().size());
        assertEquals(1, (int) a.getCategoryIds().get(0));
        assertEquals(8, (int) a.getCategoryIds().get(1));
    }

    /**
     * test to set category
     */
    @Test
    public void test4SetCategory() {
        boolean res1 = dbg.setCategory(4, 1);
        boolean res2 = dbg.setCategory(4, 6);
        Account a = dbg.getAccount(4);
        dbt.executeQuery("DELETE FROM accountCategory WHERE AccountId=4 AND CategoryId=6;");
        assertTrue(res1);
        assertTrue(res2);
        assertEquals(2, a.getCategoryIds().size());
        assertEquals(1, (int) a.getCategoryIds().get(0));
        assertEquals(6, (int) a.getCategoryIds().get(1));
    }

    @Test
    public void test1AddAccount() {

    }

    @Test
    public void test2AddAccount() {

    }

    @Test
    public void test3AddAccount() {

    }

    @Test
    public void test4AddAccount() {

    }

    @Test
    public void test5AddAccount() {

    }

    /**
     * disconnect from the database and clear the database
     */
    @After
    public void tearDown() {
        dbg.disconnect();
        dbt.disconnect();
    }

}
