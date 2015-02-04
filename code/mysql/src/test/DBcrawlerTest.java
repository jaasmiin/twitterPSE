package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.logging.Logger;

import mysql.AccessData;
import mysql.DBcrawler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import util.LoggerUtil;

/**
 * class to test the database-access of the crawler
 * 
 * @author Holger Ebhart
 * 
 */
public class DBcrawlerTest {

    private DBcrawler dbc;
    private Logger log;
    private AccessData access;
    private Date date = new Date();
    private DBtest cleaner;
    private DateFormat dateFormat;

    private Method mAddLocation;
    private Object[] parametersAddLocation;

    private Method mGetCountryCodes;
    private Object[] parametersGetCountryCodes;

    /**
     * initialize this test-class
     */
    public DBcrawlerTest() {
        access = new AccessData("localhost", "3306", "twittertest", "root",
                "root");
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    /**
     * prepare database and database-connection for next test-case
     */
    @SuppressWarnings("rawtypes")
    @Before
    public void setUp() {
        try {
            log = LoggerUtil.getLogger("TestLog");
            cleaner = new DBtest(access, log);
            dbc = new DBcrawler(access, log);
            dbc.connect();
        } catch (InstantiationException | IllegalAccessException
                | ClassNotFoundException | SQLException | SecurityException
                | IOException e) {
            e.printStackTrace();
        }

        Class[] pTypesAddLocation = new Class[2];
        pTypesAddLocation[0] = java.lang.String.class;
        pTypesAddLocation[1] = java.lang.String.class;
        try {
            mAddLocation = dbc.getClass().getDeclaredMethod("addLocation",
                    pTypesAddLocation);
            mAddLocation.setAccessible(true);
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        parametersAddLocation = new Object[2];

        Class[] pTypesGetCountryCodes = new Class[0];
        try {
            mGetCountryCodes = dbc.getClass().getDeclaredMethod(
                    "getCountryCodes", pTypesGetCountryCodes);
            mGetCountryCodes.setAccessible(true);
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        parametersGetCountryCodes = new Object[0];

    }

    /**
     * test getting accounts as hashset
     */
    @Test
    public void testGetAccounts() {
        HashSet<Long> h = dbc.getAccounts();
        assertEquals(6, h.size());
    }

    /**
     * test to get the countryCodes
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testGetCountryCodes() {
        HashSet<String> h = new HashSet<String>();
        try {
            h = (HashSet<String>) mGetCountryCodes.invoke(dbc,
                    parametersGetCountryCodes);
        } catch (IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            e.printStackTrace();
        }
        assertEquals(8, h.size());
    }

    /**
     * test for number of NonVerifiedAccounts
     */
    @Test
    public void testGetNonVerifiedAccounts() {
        long[] l = dbc.getNonVerifiedAccounts();
        assertEquals(1, l.length);
        // check id
        assertEquals(5, l[0]);
    }

    /**
     * test to add a day
     */
    @Test
    public void testAddDay() {
        boolean res = dbc.addDay(date);
        cleaner.sql("DELETE FROM day WHERE day = \"" + dateFormat.format(date)
                + "\";");
        assertTrue(res);
    }

    /**
     * add an account
     */
    @Test
    public void test1AddAccount() {
        boolean[] res = dbc.addAccount(null, null, date, true);
        assertTrue(!res[0]);
        assertTrue(!res[1]);
        assertTrue(!res[2]);
    }

    /**
     * add an account
     */
    @Test
    public void test2AddAccount() {
        dbc.addDay(date);
        boolean[] res = dbc.addAccount(new MyUser("name", 111, "timeZone",
                "location", "http://url", 11, true), null, date, false);
        HashSet<Long> h = dbc.getAccounts();
        long[] l = dbc.getNonVerifiedAccounts();
        cleaner.sql("DELETE FROM tweets WHERE 1;");
        cleaner.sql("DELETE FROM accounts WHERE TwitterAccountId = 111;");
        cleaner.sql("DELETE FROM day WHERE day = \"" + dateFormat.format(date)
                + "\";");
        assertTrue(res[0]);
        assertTrue(res[1]);
        assertTrue(res[2]);

        assertEquals(7, h.size());
        assertEquals(1, l.length);
    }

    /**
     * add two different Accounts
     */
    @Test
    public void test3AddAccount() {
        dbc.addDay(date);
        boolean[] res1 = dbc.addAccount(new MyUser("name", 111, "timeZone",
                "location", "http://www.url", 11, true), null, date, false);
        boolean[] res2 = dbc.addAccount(new MyUser("name2", 999, "timeZone",
                "location", null, 11, false), null, date, true);
        HashSet<Long> h = dbc.getAccounts();
        long[] l = dbc.getNonVerifiedAccounts();
        cleaner.sql("DELETE FROM tweets WHERE 1;");
        cleaner.sql("DELETE FROM accounts WHERE TwitterAccountId = 111;");
        cleaner.sql("DELETE FROM accounts WHERE TwitterAccountId = 999;");
        cleaner.sql("DELETE FROM day WHERE day = \"" + dateFormat.format(date)
                + "\";");
        assertTrue(res1[0]);
        assertTrue(res1[1]);
        assertTrue(res1[2]);

        assertTrue(res2[0]);
        assertTrue(res2[1]);
        assertTrue(res2[2]);

        assertEquals(8, h.size());
        assertEquals(2, l.length);
    }

    /**
     * add the same account twice
     */
    @Test
    public void test4AddAccount() {
        dbc.addDay(date);
        boolean[] res1 = dbc.addAccount(new MyUser("name", 111, "timeZone",
                "location", "url", 11, true), null, date, false);
        boolean[] res2 = dbc.addAccount(new MyUser("name", 111, "timeZone",
                "location", "url", 11, true), null, date, false);
        HashSet<Long> h = dbc.getAccounts();
        long[] l = dbc.getNonVerifiedAccounts();
        cleaner.sql("DELETE FROM tweets WHERE 1;");
        cleaner.sql("DELETE FROM accounts WHERE TwitterAccountId = 111;");
        cleaner.sql("DELETE FROM accounts WHERE TwitterAccountId = 999;");
        cleaner.sql("DELETE FROM day WHERE day = \"" + dateFormat.format(date)
                + "\";");
        assertTrue(res1[0]);
        assertTrue(res1[1]);
        assertTrue(res1[2]);

        assertTrue(res2[0]);
        assertTrue(res2[1]);
        assertTrue(res2[2]);

        assertEquals(7, h.size());
        assertEquals(1, l.length);
    }

    /**
     * test to add a location
     */
    @SuppressWarnings("unchecked")
    @Test
    public void test1AddLocation() {

        parametersAddLocation[0] = "TTT";
        parametersAddLocation[1] = null;
        boolean res = false;
        try {
            res = (boolean) mAddLocation.invoke(dbc, parametersAddLocation);
        } catch (IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e1) {
            e1.printStackTrace();
        }
        HashSet<String> h = new HashSet<String>();
        try {
            h = (HashSet<String>) mGetCountryCodes.invoke(dbc,
                    parametersGetCountryCodes);
        } catch (IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            e.printStackTrace();
        }
        cleaner.sql("DELETE FROM location WHERE code = \"TTT\";");
        assertTrue(res);
        assertTrue(h.contains("TTT"));
        assertEquals(9, h.size());
    }

    /**
     * test to add a location that's code is to long
     */
    @SuppressWarnings("unchecked")
    @Test
    public void test2AddLocation() {

        parametersAddLocation[0] = "000TTT";
        parametersAddLocation[1] = null;
        boolean res = false;
        try {
            res = (boolean) mAddLocation.invoke(dbc, parametersAddLocation);
        } catch (IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e1) {
            e1.printStackTrace();
        }
        HashSet<String> h = new HashSet<String>();
        try {
            h = (HashSet<String>) mGetCountryCodes.invoke(dbc,
                    parametersGetCountryCodes);
        } catch (IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            e.printStackTrace();
        }

        cleaner.sql("DELETE FROM location WHERE code = \"000\";");
        assertTrue(res);
        assertTrue(h.contains("000"));
        assertEquals(9, h.size());
    }

    /**
     * test to add a location with parent
     */
    @SuppressWarnings("unchecked")
    @Test
    public void test3AddLocation() {

        parametersAddLocation[0] = "000TTT";
        parametersAddLocation[1] = "T0";
        boolean res = false;
        try {
            res = (boolean) mAddLocation.invoke(dbc, parametersAddLocation);
        } catch (IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e1) {
            e1.printStackTrace();
        }
        HashSet<String> h = new HashSet<String>();
        try {
            h = (HashSet<String>) mGetCountryCodes.invoke(dbc,
                    parametersGetCountryCodes);
        } catch (IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            e.printStackTrace();
        }

        cleaner.sql("DELETE FROM location WHERE code = \"000\";");
        assertTrue(res);
        assertTrue(h.contains("000"));
        assertEquals(9, h.size());
    }

    /**
     * test to add a location with parent for a few times
     */
    @SuppressWarnings("unchecked")
    @Test
    public void test4AddLocation() {

        parametersAddLocation[0] = "000TTT";
        parametersAddLocation[1] = "ZZ";
        boolean res1 = false;
        boolean res2 = false;
        try {
            res1 = (boolean) mAddLocation.invoke(dbc, parametersAddLocation);
            res2 = (boolean) mAddLocation.invoke(dbc, parametersAddLocation);
        } catch (IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e1) {
            e1.printStackTrace();
        }
        HashSet<String> h = new HashSet<String>();
        try {
            h = (HashSet<String>) mGetCountryCodes.invoke(dbc,
                    parametersGetCountryCodes);
        } catch (IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            e.printStackTrace();
        }

        cleaner.sql("DELETE FROM location WHERE code = \"000\";");
        cleaner.sql("DELETE FROM location WHERE code = \"ZZ\";");
        assertTrue(res1);
        assertTrue(res2);
        assertTrue(h.contains("000"));
        assertTrue(h.contains("ZZ"));
        assertEquals(10, h.size());
    }

    /**
     * test to add two locations with parent
     */
    @SuppressWarnings("unchecked")
    @Test
    public void test5AddLocation() {

        boolean res1 = false;
        boolean res2 = false;
        try {
            parametersAddLocation[0] = "PPP";
            parametersAddLocation[1] = "ZZ";
            res1 = (boolean) mAddLocation.invoke(dbc, parametersAddLocation);
            parametersAddLocation[0] = "XXXC";
            parametersAddLocation[1] = "ZZZB";
            res2 = (boolean) mAddLocation.invoke(dbc, parametersAddLocation);
        } catch (IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e1) {
            e1.printStackTrace();
        }
        HashSet<String> h = new HashSet<String>();
        try {
            h = (HashSet<String>) mGetCountryCodes.invoke(dbc,
                    parametersGetCountryCodes);
        } catch (IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            e.printStackTrace();
        }

        cleaner.sql("DELETE FROM location WHERE code = \"PPP\";");
        cleaner.sql("DELETE FROM location WHERE code = \"ZZ\";");
        cleaner.sql("DELETE FROM location WHERE code = \"XXX\";");
        cleaner.sql("DELETE FROM location WHERE code = \"ZZZ\";");
        assertTrue(res1);
        assertTrue(res2);
        assertTrue(h.contains("PPP"));
        assertTrue(h.contains("ZZ"));
        assertTrue(h.contains("XXX"));
        assertTrue(h.contains("ZZZ"));
        assertEquals(12, h.size());
    }

    /**
     * test to add a retweet
     */
    @Test
    public void test1AddRetweet() {
        boolean[] res = dbc.addRetweet(9999, null, date);
        cleaner.sql("DELETE FROM retweets WHERE 1;");
        assertTrue(res[0]);
        assertTrue(!res[1]);
    }

    /**
     * test to add a retweet
     */
    @Test
    public void test2AddRetweet() {
        boolean res1 = dbc.addDay(date);
        boolean[] res2 = dbc.addRetweet(9999, null, date);
        cleaner.sql("DELETE FROM retweets WHERE 1;");
        cleaner.sql("DELETE FROM day WHERE day = \"" + dateFormat.format(date)
                + "\";");
        assertTrue(res1);
        assertTrue(res2[0]);
        assertTrue(!res2[1]);
    }

    /**
     * test to add a retweet
     */
    @Test
    public void test3AddRetweet() {
        boolean res1 = dbc.addDay(date);
        boolean[] res2 = dbc.addRetweet(1, null, date);
        cleaner.sql("DELETE FROM retweets WHERE 1;");
        cleaner.sql("DELETE FROM day WHERE day = \"" + dateFormat.format(date)
                + "\";");
        assertTrue(res1);
        assertTrue(res2[0]);
        assertTrue(res2[1]);
    }

    /**
     * test to add a retweet
     */
    @Test
    public void test4AddRetweet() {
        boolean res1 = dbc.addDay(date);
        boolean[] res2 = dbc.addRetweet(1, "T3", date);
        cleaner.sql("DELETE FROM retweets WHERE 1;");
        cleaner.sql("DELETE FROM day WHERE day = \"" + dateFormat.format(date)
                + "\";");
        assertTrue(res1);
        assertTrue(res2[0]);
        assertTrue(res2[1]);
    }

    /**
     * test to add a retweet
     */
    @Test
    public void test5AddRetweet() {
        boolean[] res2 = dbc.addRetweet(1, "T3", null);
        cleaner.sql("DELETE FROM retweets WHERE 1;");
        assertTrue(!res2[0]);
        assertTrue(!res2[1]);
    }

    /**
     * disconnect from database
     */
    @After
    public void tearDown() {
        dbc.disconnect();
    }

}