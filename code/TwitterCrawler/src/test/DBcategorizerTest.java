package test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import mysql.AccessData;
import mysql.DBcategorizer;
import mysql.result.Account;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * class to test the database-access of the categorizer
 * 
 * @author Holger Ebhart
 * @version 1.0
 * 
 */
public class DBcategorizerTest {

    private DBcategorizer dbc;
    private Logger log;
    private AccessData access;

    @Before
    public void setUp() {
        try {
            log = getLogger();
            access = new AccessData("localhost", "3306", "twittertest", "root",
                    "root");
            dbc = new DBcategorizer(access, log);
            dbc.connect();
        } catch (InstantiationException | IllegalAccessException
                | ClassNotFoundException | SQLException | SecurityException
                | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * test if the right accounts where returned
     */
    @Test
    public void testGetNonCategorized() {
        List<Account> list = new ArrayList<Account>();
        list = dbc.getNonCategorized();
        assertEquals(5, list.size());
        for (int i = 0; i < list.size(); i++) {
            assertEquals(0, list.get(i).getCategoryIds().size());
        }
    }

    /**
     * test to add null
     */
    @Test
    public void test1AddCategoryToAccount() {
        assertFalse(dbc.addCategoryToAccount(0, 0));
    }

    /**
     * test if categories where set right
     */
    @Test
    public void test2AddCategoryToAccount() {
        boolean res = dbc.addCategoryToAccount(1, 1);
        List<Account> list = dbc.getNonCategorized();
        try {
            DBtest t = new DBtest(access, log);
            t.sql("UPDATE accounts SET Categorized = 0 WHERE Id = 1;");
            t.sql(" DELETE FROM accountCategory WHERE AccountId=1;");
            t.sql("DELETE FROM category WHERE (Name = \"testCP\" OR Name=\"parent\") AND Id > 8;");
        } catch (InstantiationException | IllegalAccessException
                | ClassNotFoundException e) {
            e.printStackTrace();
        }
        assertTrue(res);
        assertEquals(4, list.size());
    }

    /**
     * test if categories where set right even if they were set twice
     */
    @Test
    public void test3AddCategoryToAccount() {
        // try to execute 3-times the same query
        boolean res1 = dbc.addCategoryToAccount(1, 3);
        boolean res2 = dbc.addCategoryToAccount(1, 3);
        boolean res3 = dbc.addCategoryToAccount(1, 3);
        List<Account> list = dbc.getNonCategorized();
        try {
            DBtest t = new DBtest(access, log);
            t.sql("UPDATE accounts SET Categorized = 0 WHERE Id = 1;");
            t.sql(" DELETE FROM accountCategory WHERE AccountId=1;");
            t.sql("DELETE FROM category WHERE (Name = \"testCP\" OR Name=\"parent\") AND Id > 8;");
        } catch (InstantiationException | IllegalAccessException
                | ClassNotFoundException e) {
            e.printStackTrace();
        }
        assertTrue(res1);
        assertTrue(res2);
        assertTrue(res3);
        assertEquals(4, list.size());
    }

    /**
     * test if categories with parents where set right
     */
    @Test
    public void test4AddCategoryToAccount() {
        boolean res = dbc.addCategoryToAccount(1, 8);
        List<Account> list = dbc.getNonCategorized();
        try {
            DBtest t = new DBtest(access, log);
            t.sql("UPDATE accounts SET Categorized = 0 WHERE Id = 1;");
            t.sql(" DELETE FROM accountCategory WHERE AccountId=1;");
            t.sql("DELETE FROM category WHERE (Name = \"testCP\" OR Name=\"parent\") AND Id > 8;");
            t.sql("DELETE FROM category WHERE Name = \"parent\";");
        } catch (InstantiationException | IllegalAccessException
                | ClassNotFoundException e) {
            e.printStackTrace();
        }
        assertTrue(res);
        assertEquals(4, list.size());
    }

    /**
     * test if categories with parents where set right even if they where added
     * multiple
     */
    @Test
    public void test5AddCategoryToAccount() {
        boolean res1 = dbc.addCategoryToAccount(1, 8);
        boolean res2 = dbc.addCategoryToAccount(1, 8);
        boolean res3 = dbc.addCategoryToAccount(1, 8);
        List<Account> list = dbc.getNonCategorized();
        try {
            DBtest t = new DBtest(access, log);
            t.sql("UPDATE accounts SET Categorized = 0 WHERE Id = 1;");
            t.sql(" DELETE FROM accountCategory WHERE AccountId=1;");
            t.sql("DELETE FROM category WHERE (Name = \"testCP\" OR Name=\"parent\") AND Id > 8;");
            t.sql("DELETE FROM category WHERE Name = \"parent\";");
        } catch (InstantiationException | IllegalAccessException
                | ClassNotFoundException e) {
            e.printStackTrace();
        }
        assertTrue(res1);
        assertTrue(res2);
        assertTrue(res3);
        assertEquals(4, list.size());
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
