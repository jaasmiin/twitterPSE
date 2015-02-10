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

    private DBtest dbt;

    /**
     * prepares a database-connection for the next test-case
     */
    @Before
    public void setUp() {
        try {
            log = LoggerUtil.getLogger("TestLog");
            access = new AccessData("localhost", "3306", "twittertest", "root",
                    "root");

            // start database-connection for the component to test
            dbc = new DBcategorizer(access, log);
            dbc.connect();

            // start database-connection to execute customized queries
            dbt = new DBtest(access, log);
            dbt.connect();

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
     * test if the right accounts where returned
     */
    @Test
    public void testGetNonCategorizedAndSetCategorized() {

        // set 2 accounts as categorized
        assertTrue(dbc.setCategorized(1));
        assertTrue(dbc.setCategorized(2));

        List<Account> list = new ArrayList<Account>();
        list = dbc.getNonCategorized();
        assertEquals(3, list.size());
        for (int i = 0; i < list.size(); i++) {
            assertEquals(0, list.get(i).getCategoryIds().size());
        }

        // clean up database
        dbt.executeQuery("UPDATE accounts SET Categorized=0 WHERE Id=1 OR Id=2;");
    }

    /**
     * test to add null
     */
    @Test
    public void test1AddCategoryToAccount() {
        assertFalse(dbc.addCategoryToAccount(0, 0));
    }

    /**
     * test to add null
     */
    @Test
    public void test2AddCategoryToAccount() {
        assertFalse(dbc.addCategoryToAccount(1, 0));
    }

    /**
     * test if categories where set right
     */
    @Test
    public void test3AddCategoryToAccount() {

        assertTrue(dbc.addCategoryToAccount(1, 1));
        List<Account> list = dbc.getNonCategorized();
        assertEquals(4, list.size());

        // clean up database
        dbt.executeQuery("UPDATE accounts SET Categorized = 0 WHERE Id = 1;");
        dbt.executeQuery("DELETE FROM accountCategory WHERE AccountId=1;");
    }

    /**
     * test if categories where set right even if they were set twice
     */
    @Test
    public void test4AddCategoryToAccount() {
        // try to execute 3-times the same query
        assertTrue(dbc.addCategoryToAccount(1, 3));
        assertTrue(dbc.addCategoryToAccount(1, 3));
        assertTrue(dbc.addCategoryToAccount(1, 3));
        List<Account> list = dbc.getNonCategorized();
        assertEquals(4, list.size());

        // clean up database
        dbt.executeQuery("UPDATE accounts SET Categorized = 0 WHERE Id = 1;");
        dbt.executeQuery(" DELETE FROM accountCategory WHERE AccountId=1;");
    }

    /**
     * test if categories with parents where set right
     */
    @Test
    public void test5AddCategoryToAccount() {

        assertTrue(dbc.addCategoryToAccount(1, 8));
        List<Account> list = dbc.getNonCategorized();
        assertEquals(4, list.size());

        // clean up database
        dbt.executeQuery("UPDATE accounts SET Categorized = 0 WHERE Id = 1;");
        dbt.executeQuery(" DELETE FROM accountCategory WHERE AccountId=1;");
    }

    /**
     * test if categories with parents where set right even if they where added
     * multiple
     */
    @Test
    public void test6AddCategoryToAccount() {
        assertTrue(dbc.addCategoryToAccount(1, 8));
        assertTrue(dbc.addCategoryToAccount(1, 8));
        assertTrue(dbc.addCategoryToAccount(1, 8));
        List<Account> list = dbc.getNonCategorized();
        assertEquals(4, list.size());

        // clean up database
        dbt.executeQuery("UPDATE accounts SET Categorized = 0 WHERE Id = 1;");
        dbt.executeQuery("DELETE FROM accountCategory WHERE AccountId=1;");
    }

    /**
     * test get categories for account by url with invalid parameters
     */
    @Test
    public void test1GetCategoriesForAccount() {
        List<Integer> list = dbc.getCategoriesForAccount(null, null);
        assertEquals(0, list.size());
    }

    /**
     * test get categories for account by url with invalid parameters
     */
    @Test
    public void test2GetCategoriesForAccount() {
        List<Integer> list = dbc.getCategoriesForAccount("", "");
        assertEquals(0, list.size());
    }

    /**
     * test get categories for account by url
     */
    @Test
    public void test3GetCategoriesForAccount() {
        List<Integer> list = dbc.getCategoriesForAccount("url", "Tester0");
        assertEquals(3, list.size());
        assertEquals(1, (int) list.get(0));
        assertEquals(5, (int) list.get(1));
        assertEquals(9, (int) list.get(2));
    }

    /**
     * test get categories for account by url
     */
    @Test
    public void test4GetCategoriesForAccount() {
        List<Integer> list = dbc.getCategoriesForAccount("null", "Tester4");
        assertEquals(1, list.size());
        assertEquals(9, (int) list.get(0));
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
        assertEquals(2, dbc.getParentId(8));
    }

    /**
     * disconnect from the database
     */
    @After
    public void tearDown() {
        dbc.disconnect();
        dbt.disconnect();
    }

}
