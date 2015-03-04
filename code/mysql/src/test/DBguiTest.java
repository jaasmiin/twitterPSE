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
import mysql.result.Retweets;
import mysql.result.Tweets;
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
    private List<Integer> list23;
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

        list23 = new ArrayList<Integer>();
        list23.add(2);
        list23.add(3);
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
        Location a = l.get(0);
        // test equals method
        assertEquals(new Location(1, null, null), a);
        assertEquals(1, a.getId());
        assertEquals("0", a.getLocationCode());
        assertEquals("Defaultlocation", a.toString());
        a = l.get(7);
        assertEquals(8, a.getId());
        assertEquals("TP", a.getLocationCode());
        assertEquals("Testparent", a.toString());
    }

    /**
     * test of getting the categories
     */
    @Test
    public void testGetCategories() {
        Category c = dbg.getCategories();

        assertEquals("ROOT", c.getText());
        assertEquals("ROOT (3)", c.toString());

        List<Category> l = c.getChilds();
        assertEquals(6, l.size());

        Category a = l.get(0);
        assertEquals(0, a.getChilds().size());
        assertTrue(a.equals(new Category(3, null, 0, false, 0)));
        assertEquals(0, a.getMatchedAccounts());
        assertEquals("testC1 (0)", a.toString());

        a = l.get(5);
        assertEquals(2, a.getChilds().size());
        assertEquals(2, a.getMatchedAccounts());
        assertEquals("testC0 (2)", a.toString());

        // test to initialize a new Category
        assertEquals(
                "moreThan50CharsmoreThan50CharsmoreThan50CharsmoreT",
                new Category(
                        2,
                        "moreThan50CharsmoreThan50CharsmoreThan50CharsmoreThan50Chars",
                        0, false, 0).getText());
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
        List<Account> l = dbg.getAccounts("er0");
        assertEquals(1, l.size());
        Account a = l.get(0);
        assertEquals("Tester0", a.getName());
        assertEquals("Tester0", a.toString());
        assertEquals(new Account(1, null, 0, null), a);
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
    public void test3GetSumOfData() {
        TweetsAndRetweets test = dbg.getSumOfData(list0, list0, list1, false);

        assertEquals(1, test.getTweets().size());
        assertEquals(0, test.getRetweets().size());
    }

    /**
     * test for getting a sum of data
     */
    @Test
    public void test4GetSumOfData() {
        TweetsAndRetweets test = dbg.getSumOfData(list0, list0, list1, true);

        assertEquals(0, test.getTweets().size());
        assertEquals(0, test.getRetweets().size());
    }

    /**
     * test for getting a sum of data
     */
    @Test
    public void test5GetSumOfData() {
        TweetsAndRetweets test = dbg
                .getSumOfData(list23, list23, list23, false);

        assertEquals(1, test.getTweets().size());
        assertEquals(6, test.getTweets().get(0).getCounter());
        assertNull(test.getTweets().get(0).getDate());

        List<Retweets> l = test.getRetweets();
        assertEquals(3, l.size());

        Retweets r = l.get(0);
        assertEquals(7, r.getCounter());
        assertEquals("0", r.getLocationCode());
        assertNull(r.getDate());

        r = l.get(1);
        assertEquals(8, r.getCounter());
        assertEquals("T0", r.getLocationCode());
        assertNull(r.getDate());

        r = l.get(2);
        assertEquals(10, r.getCounter());
        assertEquals("T1", r.getLocationCode());
        assertNull(r.getDate());
    }

    /**
     * test for getting a sum of data
     */
    @Test
    public void test6GetSumOfData() {
        TweetsAndRetweets test = dbg.getSumOfData(list23, list1, list0, false);

        assertEquals(1, test.getTweets().size());
        assertEquals(5, test.getTweets().get(0).getCounter());
        assertNull(test.getTweets().get(0).getDate());

        List<Retweets> l = test.getRetweets();
        assertEquals(2, l.size());

        Retweets r = l.get(0);
        assertEquals(7, r.getCounter());
        assertEquals("0", r.getLocationCode());
        assertNull(r.getDate());

        r = l.get(1);
        assertEquals(6, r.getCounter());
        assertEquals("T1", r.getLocationCode());
        assertNull(r.getDate());
    }

    /**
     * test for getting a sum of data
     */
    @Test
    public void test2GetSumOfDataWithDates() {
        TweetsAndRetweets test = dbg.getSumOfData(list1, list1, list0, true);

        List<Tweets> tweets = test.getTweets();
        assertEquals(3, tweets.size());
        assertEquals(3, tweets.get(0).getCounter());
        assertEquals(2, tweets.get(1).getCounter());
        assertEquals(1, tweets.get(2).getCounter());
        List<Retweets> retweets = test.getRetweets();
        assertEquals(4, retweets.size());
        assertEquals(10, retweets.get(3).getCounter());
        assertEquals("T1", retweets.get(3).getLocationCode());
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
     * test for getting all data
     */
    @Test
    public void test3GetAllData() {
        List<Account> res = dbg.getAllData(list0, list1, list0, false);

        assertEquals(2, res.size());
        assertEquals(1, res.get(0).getTweets().size());
        assertEquals(1, res.get(1).getTweets().size());
        assertEquals(1, res.get(0).getTweets().get(0).getCounter());
        assertEquals(5, res.get(1).getTweets().get(0).getCounter());
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

    /**
     * test addAccount with invalid parameters
     */
    @Test
    public void test1AddAccount() {
        boolean res = dbg.addAccount(null, 1);
        assertTrue(!res);
    }

    /**
     * test addAccount with invalid parameters
     */
    @Test
    public void test2AddAccount() {
        boolean res = dbg.addAccount(new MyUser(null, 0, null, null, null, 0,
                false), 1);
        assertTrue(res);
    }

    /**
     * test addAccount
     */
    @Test
    public void test3AddAccount() {
        boolean res = dbg.addAccount(new MyUser("Added", 1234, null, null,
                null, 11, false), 6);
        List<Account> l = dbg.getAccounts("Added");
        dbt.executeQuery("DELETE FROM accounts WHERE AccountName=\"Added\";");

        assertTrue(res);
        assertEquals(1, l.size());
        Account a = l.get(0);
        assertEquals("Added", a.getName());
        assertEquals(1234, a.getTwitterId());
        assertTrue(!a.isVerified());
        assertEquals(11, a.getFollower());
        assertEquals("T4", a.getLocationCode());
        assertNull(a.getUrl());
    }

    /**
     * test addAccount add two times the same account
     */
    @Test
    public void test4AddAccount() {
        boolean res1 = dbg.addAccount(new MyUser("Added", 1234, null, null,
                null, 11, false), 1);
        boolean res2 = dbg.addAccount(new MyUser("Added", 1234, null, null,
                null, 11, false), 1);
        List<Account> l = dbg.getAccounts("Added");
        dbt.executeQuery("DELETE FROM accounts WHERE AccountName=\"Added\";");

        assertTrue(res1);
        assertTrue(res2);
        assertEquals(1, l.size());
        Account a = l.get(0);
        assertEquals("Added", a.getName());
        assertEquals(1234, a.getTwitterId());
        assertTrue(!a.isVerified());
        assertEquals(11, a.getFollower());
        assertNull(a.getUrl());
    }

    /**
     * test addAccount
     */
    @Test
    public void test5AddAccount() {
        boolean res1 = dbg.addAccount(new MyUser("Added_1", 1234, null, null,
                "url1", 1, false), 1);
        boolean res2 = dbg.addAccount(new MyUser("Added_2", 123456, "timezone",
                "location", "url", 2, true), 4);
        List<Account> l = dbg.getAccounts("Added");
        dbt.executeQuery("DELETE FROM accounts WHERE AccountName=\"Added_1\";");
        dbt.executeQuery("DELETE FROM accounts WHERE AccountName=\"Added_2\";");

        assertTrue(res1);
        assertTrue(res2);

        assertEquals(2, l.size());
        Account a = l.get(0);
        assertEquals("Added_2", a.getName());
        assertEquals(123456, a.getTwitterId());
        assertTrue(a.isVerified());
        assertEquals(2, a.getFollower());
        assertEquals("T2", a.getLocationCode());
        assertEquals("url", a.getUrl());
        a = l.get(1);
        assertEquals("Added_1", a.getName());
        assertEquals(1234, a.getTwitterId());
        assertTrue(!a.isVerified());
        assertEquals(1, a.getFollower());
        assertEquals("0", a.getLocationCode());
        assertEquals("url1", a.getUrl());
    }

    /**
     * test to set invalid location-code
     */
    @Test
    public void test1SetLocationCode() {
        Retweets r = new Retweets(null, 0, null);
        assertNull(r.getLocationCode());
    }

    /**
     * test to set invalid location-code
     */
    @Test
    public void test2SetLocationCode() {
        Retweets r = new Retweets(null, 0, "ABCDEFG");
        assertEquals("ABC", r.getLocationCode());
    }

    /**
     * test to set location-code
     */
    @Test
    public void test3SetLocationCode() {
        Retweets r = new Retweets(null, 0, "AAA");
        assertEquals("AAA", r.getLocationCode());
    }

    /**
     * test to create an Account
     */
    @Test
    public void testCreateAccount() {
        Account a = new Account(999, "name", "url");
        List<Retweets> list = new ArrayList<Retweets>();
        a.setRetweets(list);

        assertEquals(999, a.getId());
        assertEquals("name", a.getName());
        assertEquals("name", a.toString());
        assertEquals("url", a.getUrl());
        assertEquals(0, a.getFollower());
        assertEquals(0, a.getTwitterId());
        assertEquals(0, a.getTweets().size());
        assertTrue(list == a.getRetweets());
        assertEquals(0, a.getCategoryIds().size());
        assertEquals("0", a.getLocationCode());

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
