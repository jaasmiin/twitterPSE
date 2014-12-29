package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import mysql.AccessData;
import mysql.DBConnection;
import mysql.DBcategorizer;
import mysql.result.Account;
import mysql.result.Category;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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

    @Test
    public void testGetNonCategorized() {
        List<Account> list = new ArrayList<Account>();
        list = dbc.getNonCategorized();
        assertEquals(5, list.size());
        for (int i = 0; i < list.size(); i++) {
            assertEquals(0, list.get(i).getCategoryIds().length);
        }
    }

    @Test
    public void test1AddCategoryToAccount() {
        assertFalse(dbc.addCategoryToAccount(0, null));
    }

    @Test
    public void test2AddCategoryToAccount() {
        assertTrue(dbc
                .addCategoryToAccount(1, new Category(-1, "testCP", null)));
        List<Account> list = new ArrayList<Account>();
        list = dbc.getNonCategorized();
        try {
            DBtest t = new DBtest(access, log);
            t.sql("UPDATE accounts SET Categorized = 0 WHERE Id = 1;");
            t.sql(" DELETE FROM accountCategory WHERE 1;");
        } catch (InstantiationException | IllegalAccessException
                | ClassNotFoundException e) {
            e.printStackTrace();
        }
        assertEquals(4, list.size());
    }

    @After
    public void tearDown() {
        dbc.disconnect();
    }

    private Logger getLogger() throws SecurityException, IOException {
        Logger l = Logger.getLogger("logger");
        FileHandler fh = new FileHandler("TestLog.log", true);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
        l.addHandler(fh);
        // true: print output on console and into file
        // false: only store output in logFile
        l.setUseParentHandlers(false);
        return l;
    }

    class DBtest extends DBConnection {

        public DBtest(AccessData accessData, Logger logger)
                throws InstantiationException, IllegalAccessException,
                ClassNotFoundException {
            super(accessData, logger);
        }

        public void sql(String sql) {

            try {
                connect();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }

            try {
                Statement s = c.createStatement();
                s.executeUpdate(sql);
            } catch (SQLException e) {
                log.warning("Couldn't execute sql query\n" + e.getMessage());
            }
            disconnect();
        }
    }

}
