package test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import mysql.AccessData;
import mysql.DBgui;
import mysql.result.Account;
import mysql.result.Category;
import mysql.result.Location;
import mysql.result.TweetsAndRetweets;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DBguiTest {

    private DBgui dbg;
    private DBtest dbt;
    private Logger log;
    private AccessData access;

    public DBguiTest() {
        try {
            log = getLogger();
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

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

    @Test
    public void testGetLocations() {
        Location[] l = dbg.getLocations();
        // check parent
        assertEquals(8, l.length);
    }

    @Test
    public void testGetCategories() {
        Category[] c = dbg.getCategories();
        // check parent
        assertEquals(8, c.length);
    }

    @Test
    public void testGetDates() {
        Date[] d = dbg.getDates();
        assertEquals(4, d.length);
    }

    @Test
    public void test1GetAccountId() {
        int result = dbg.getAccountId("Irgendwas");
        assertEquals(-1, result);
    }

    @Test
    public void test2GetAccountId() {
        int result = dbg.getAccountId("Tester0");
        assertEquals(1, result);
    }

    @Test
    public void test3GetAccountId() {
        int result = dbg.getAccountId("Tester3");
        assertEquals(4, result);
    }

    @Test
    public void test1GetAccounts() {
        Account[] a = dbg.getAccounts("Tester3");
        assertEquals(1, a.length);
        assertEquals(a[0].getName(), "Tester3");
    }

    @Test
    public void test2GetAccounts() {
        Account[] a = dbg.getAccounts("Tester");
        assertEquals(6, a.length);
    }

    @Test
    public void test3GetAccounts() {
        Account[] a = dbg.getAccounts("er0");
        assertEquals(1, a.length);
        assertEquals(a[0].getName(), "Tester0");
    }

    @Test
    public void test4GetAccounts() {
        Account[] a = dbg.getAccounts("blabla");
        assertEquals(0, a.length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test1GetSumOfData() {
        try {
            dbg.getSumOfData(new int[0], new int[0], new int[0]);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Test
    public void test2GetSumOfData() {
        TweetsAndRetweets test = new TweetsAndRetweets();
        try {
            test = dbg.getSumOfData(new int[] {1, 2 }, new int[] {1 },
                    new int[0]);
        } catch (IllegalArgumentException | SQLException e) {
            e.printStackTrace();
        }
        assertEquals(1, test.tweets.size());
        assertEquals(3, test.retweets.size());
    }

    @Test
    public void test2GetSumOfDataWithDates() {
        TweetsAndRetweets test = new TweetsAndRetweets();
        try {
            test = dbg.getSumOfDataWithDates(new int[] {1, 2 }, new int[] {1 },
                    new int[0]);
        } catch (IllegalArgumentException | SQLException e) {
            e.printStackTrace();
        }
        assertEquals(3, test.tweets.size());
        assertEquals(4, test.retweets.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void test1GetSumOfDataWithDates() {
        try {
            dbg.getSumOfDataWithDates(new int[0], new int[0], new int[0]);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        removeTestData();
        dbg.disconnect();
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

    private void addTestData() {
        try {
            dbt.connect();
            dbt.sql("INSERT INTO tweets (AccountId,Counter,DayId) VALUES (3,3,1), (3,2,2), (2,1,3);");
            dbt.sql("INSERT INTO retweets (AccountId,LocationId,Counter,DayId) VALUES (3,1,4,1), (3,1,3,2), (3,3,6,2), (2,3,4,2), (2,2,8,1);");
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
            dbt.disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
