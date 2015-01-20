package test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import mysql.AccessData;
import mysql.DBcrawler;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DBcrawlerTest {

    private DBcrawler dbc;
    private Logger log;
    private AccessData access;
    private Date date = new Date();
    private DBtest cleaner;
    private DateFormat dateFormat;

    public DBcrawlerTest() {
        access = new AccessData("localhost", "3306", "twittertest", "root",
                "root");
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    @Before
    public void setUp() {
        try {
            log = getLogger();
            cleaner = new DBtest(access, log);
            dbc = new DBcrawler(access, log);
            dbc.connect();
        } catch (InstantiationException | IllegalAccessException
                | ClassNotFoundException | SQLException | SecurityException
                | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetAccounts() {
        HashSet<Long> h = dbc.getAccounts();
        assertEquals(6, h.size());
    }

    @Test
    public void testGetCountryCodes() {
        HashSet<String> h = dbc.getCountryCodes();
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

    @Test
    public void testAddDay() {
        boolean res = dbc.addDay(date);
        cleaner.sql("DELETE FROM day WHERE day = \"" + dateFormat.format(date)
                + "\";");
        assertTrue(res);
    }

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
    @Test
    public void test1AddLocation() {
        boolean res = dbc.addLocation("TTT", null);
        HashSet<String> h = dbc.getCountryCodes();
        cleaner.sql("DELETE FROM location WHERE code = \"TTT\";");
        assertTrue(res);
        assertTrue(h.contains("TTT"));
        assertEquals(9, h.size());
    }

    /**
     * test to add a location that's code is to long
     */
    @Test
    public void test2AddLocation() {
        boolean res = dbc.addLocation("000TTT", null);
        HashSet<String> h = dbc.getCountryCodes();
        cleaner.sql("DELETE FROM location WHERE code = \"000\";");
        assertTrue(res);
        assertTrue(h.contains("000"));
        assertEquals(9, h.size());
    }

    /**
     * test to add a location with parent
     */
    @Test
    public void test3AddLocation() {
        boolean res = dbc.addLocation("000TTT", "T0");
        HashSet<String> h = dbc.getCountryCodes();
        cleaner.sql("DELETE FROM location WHERE code = \"000\";");
        assertTrue(res);
        assertTrue(h.contains("000"));
        assertEquals(9, h.size());
    }

    /**
     * test to add a location with parent for a few times
     */
    @Test
    public void test4AddLocation() {
        boolean res1 = dbc.addLocation("000TTT", "ZZ");
        boolean res2 = dbc.addLocation("000TTT", "ZZ");
        HashSet<String> h = dbc.getCountryCodes();
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
    @Test
    public void test5AddLocation() {
        boolean res1 = dbc.addLocation("PPP", "ZZ");
        boolean res2 = dbc.addLocation("XXXC", "ZZZB");
        HashSet<String> h = dbc.getCountryCodes();
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

    @Test
    public void test1AddRetweet() {
        boolean[] res = dbc.addRetweet(9999, null, date);
        cleaner.sql("DELETE FROM retweets WHERE 1;");
        assertTrue(res[0]);
        assertTrue(!res[1]);
    }

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

    @Test
    public void test5AddRetweet() {
        boolean[] res2 = dbc.addRetweet(1, "T3", null);
        cleaner.sql("DELETE FROM retweets WHERE 1;");
        assertTrue(!res2[0]);
        assertTrue(!res2[1]);
    }

    @After
    public void tearDown() {
        dbc.disconnect();
    }

    private Logger getLogger() throws SecurityException, IOException {
        Logger l = Logger.getLogger("logger");
        new File("LogFile.log").createNewFile();
        FileHandler fh = new FileHandler("TestLog.log", true);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
        l.addHandler(fh);
        // true: print output on console and into file
        // false: only store output in logFile
        l.setUseParentHandlers(false);
        return l;
    }

}
