package test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
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
        List<Location> l = dbg.getLocations();
        // check parent
        assertEquals(8, l.size());
    }

    @Test
    public void testGetCategories() {
        Category c = dbg.getCategories();
        // check parent
        assertEquals("ROOT", c.toString());
        assertEquals(1, c.getChilds().size());
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
        List<Account> a = dbg.getAccounts("Tester3");
        assertEquals(1, a.size());
        assertEquals(a.get(0).getName(), "Tester3");
    }

    @Test
    public void test2GetAccounts() {
        List<Account> a = dbg.getAccounts("Tester");
        assertEquals(6, a.size());
    }

    @Test
    public void test3GetAccounts() {
        List<Account> a = dbg.getAccounts("er0");
        assertEquals(1, a.size());
        assertEquals(a.get(0).getName(), "Tester0");
    }

    @Test
    public void test4GetAccounts() {
        List<Account> a = dbg.getAccounts("blabla");
        assertEquals(0, a.size());
    }

    // @Test(expected = IllegalArgumentException.class)
    // public void test1GetSumOfData() {
    // try {
    // dbg.getSumOfData(new Integer[0], new Integer[0], new Integer[0],
    // false);
    // } catch (SQLException e) {
    // e.printStackTrace();
    // }
    // }
    //
    // @Test
    // public void test2GetSumOfData() {
    // TweetsAndRetweets test = new TweetsAndRetweets();
    // try {
    // test = dbg.getSumOfData(new Integer[] {1 }, new Integer[] {1 },
    // new Integer[0], false);
    // } catch (IllegalArgumentException | SQLException e) {
    // e.printStackTrace();
    // }
    // assertEquals(1, test.tweets.size());
    // assertEquals(6, test.tweets.get(0).getCounter());
    // assertNull(test.tweets.get(0).getDate());
    // assertEquals(3, test.retweets.size());
    // }
    //
    // @Test
    // public void test2GetSumOfDataWithDates() {
    // TweetsAndRetweets test = new TweetsAndRetweets();
    // try {
    // test = dbg.getSumOfData(new Integer[] {1 }, new Integer[] {1 },
    // new Integer[0], true);
    // } catch (IllegalArgumentException | SQLException e) {
    // e.printStackTrace();
    // }
    // assertEquals(3, test.tweets.size());
    // assertEquals(3, test.tweets.get(0).getCounter());
    // assertEquals(2, test.tweets.get(1).getCounter());
    // assertEquals(1, test.tweets.get(2).getCounter());
    // assertEquals(4, test.retweets.size());
    // assertEquals(10, test.retweets.get(3).getCounter());
    // }
    //
    // @Test(expected = IllegalArgumentException.class)
    // public void test1GetSumOfDataWithDates() {
    // try {
    // dbg.getSumOfData(new Integer[0], new Integer[0], new Integer[0],
    // true);
    // } catch (SQLException e) {
    // e.printStackTrace();
    // }
    // }
    //
    // @Test(expected = IllegalArgumentException.class)
    // public void test1GetAllDataWithDates() {
    // try {
    // dbg.getAllData(new Integer[0], new Integer[0], new Integer[0], true);
    // } catch (SQLException e) {
    // e.printStackTrace();
    // }
    // }
    //
    // @Test
    // public void test2GetAllDataWithDates() {
    // List<Account> res = null;
    // try {
    // res = dbg.getAllData(new Integer[] {1 }, new Integer[] {1 },
    // new Integer[0], true);
    // } catch (SQLException e) {
    // e.printStackTrace();
    // }
    // assertEquals(2, res.size());
    // assertEquals(2, res.get(0).getTweets().size());
    // assertEquals(1, res.get(1).getTweets().size());
    // assertEquals(3, res.get(0).getRetweets().size());
    // assertEquals(2, res.get(1).getRetweets().size());
    // }
    //
    // @Test(expected = IllegalArgumentException.class)
    // public void test1GetAllData() {
    // try {
    // dbg.getAllData(new Integer[0], new Integer[0], new Integer[0],
    // false);
    // } catch (SQLException e) {
    // e.printStackTrace();
    // }
    // }
    //
    // @Test
    // public void test2GetAllData() {
    // List<Account> res = null;
    // try {
    // res = dbg.getAllData(new Integer[] {1 }, new Integer[] {1 },
    // new Integer[0], false);
    // } catch (SQLException e) {
    // e.printStackTrace();
    // }
    // assertEquals(2, res.size());
    // assertEquals(1, res.get(0).getTweets().size());
    // assertEquals(1, res.get(1).getTweets().size());
    // System.out.println(res.get(0).getRetweets().get(0).getLocation());
    // assertEquals(2, res.get(0).getRetweets().size());
    // assertEquals(2, res.get(1).getRetweets().size());
    // }

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
