package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
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
    private DBtest dbt;
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
        } catch (InstantiationException | IllegalAccessException
                | ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        addTestData();
    }

    /**
     * test of getting the locations
     */
    @Test
    public void testGetLocations() {
        List<Location> l = dbg.getLocations();
        // check parent
        assertEquals(8, l.size());
    }

    /**
     * test of getting the categories
     */
    @Test
    public void testGetCategories() {
        Category c = dbg.getCategories();
        // check parent
        assertEquals("ROOT", c.toString());
        assertEquals(1, c.getChilds().size());
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
     * disconnect from the database and clear the database
     */
    @After
    public void tearDown() {
        removeTestData();
        dbg.disconnect();
    }

    private void addTestData() {
        try {
            dbt.connect();
            dbt.sql("INSERT INTO tweets (AccountId,Counter,DayId) VALUES (3,3,1), (3,2,2), (2,1,3);");
            dbt.sql("INSERT INTO retweets (AccountId,LocationId,Counter,DayId) VALUES (3,1,4,1), (3,1,3,2), (3,3,6,2), (2,3,4,2), (2,2,8,1);");
            dbt.sql("INSERT INTO accountCategory (AccountId, CategoryId) VALUES (3,1);");
            dbt.disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void removeTestData() {
        try {
            dbt.connect();
            dbt.sql("DELETE FROM tweets WHERE 1;");
            dbt.sql("DELETE FROM retweets WHERE 1;");
            dbt.sql("DELETE FROM accountCategory WHERE AccountId=3 AND CategoryId=1;");
            dbt.disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
