package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import mysql.AccessData;
import mysql.DBcategorizer;
import mysql.result.Account;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import util.LoggerUtil;

/**
 * class to test the database-access of the categorizer
 * 
 * @author Holger Ebhart
 * 
 */
public class DBcategorizerTest {

    private DBcategorizer dbc;
    private Logger log;
    private AccessData access;

    /**
     * prepares a database-connection for the next test-case
     */
    @Before
    public void setUp() {
        try {
            log = LoggerUtil.getLogger("TestLog");
            access = new AccessData("localhost", "3306", "twittertest", "root",
                    "root");
            dbc = new DBcategorizer(access, log);
            dbc.connect();
        } catch (InstantiationException | IllegalAccessException
                | ClassNotFoundException | SQLException | SecurityException
                | IOException e) {
            e.printStackTrace();
            log.warning(e.getMessage());
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
            t.sql("DELETE FROM accountCategory WHERE AccountId=1;");
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

    /**
     * test getParentId with category top
     */
    @Test
    public void test1GetParentId() {
        assertEquals(0, dbc.getParentId(1));
    }

    /**
     * test getParentId with invalid category
     */
    @Test
    public void test2GetParentId() {
        assertEquals(-1, dbc.getParentId(-5));
    }

    /**
     * test getParentId
     */
    @Test
    public void test3GetParentId() {
        assertEquals(1, dbc.getParentId(5));
    }

    /**
     * test getParentId
     */
    @Test
    public void test4GetParentId() {
        assertEquals(1, dbc.getParentId(8));
    }

    /**
     * disconnect from the database
     */
    @After
    public void tearDown() {
        dbc.disconnect();
    }

}
