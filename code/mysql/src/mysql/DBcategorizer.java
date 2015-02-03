package mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import mysql.result.Account;

/**
 * class to address a database with the categorizer
 * 
 * @author Holger Ebhart
 * 
 */
public class DBcategorizer extends DBConnection implements DBIcategorizer {

    /**
     * configure the connection to the database
     * 
     * @param accessData
     *            the access data to the specified mysql-database as AccessData
     * @param logger
     *            a global logger for the whole program as Logger
     * @throws InstantiationException
     *             thrown if the mysql-driver hasn't been found
     * @throws IllegalAccessException
     *             thrown if the mysql-driver hasn't been found
     * @throws ClassNotFoundException
     *             thrown if the mysql-driver hasn't been found
     */
    public DBcategorizer(AccessData accessData, Logger logger)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        super(accessData, logger);
    }

    @Override
    public List<Account> getNonCategorized() {

        String sqlCommand = "SELECT Id, AccountName, URL FROM accounts WHERE Categorized = 0;";

        // create and execute statement to get uncategorized accounts
        ResultSet result = null;
        Statement stmt = null;
        runningRequest = true;
        try {
            stmt = c.createStatement();
            result = stmt.executeQuery(sqlCommand);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
            return new ArrayList<Account>();
        } finally {
            runningRequest = false;
        }

        // create list of accounts to return
        List<Account> ret = new ArrayList<Account>();
        try {
            // read mysql-result and add accounts into the return list
            while (result.next()) {
                ret.add(new Account(result.getInt("Id"), result
                        .getString("AccountName"), result.getString("URL")));
            }
        } catch (SQLException e) {
            sqlExceptionResultLog(e);
        } finally {
            // close mysql-statement
            closeResultAndStatement(stmt, result);
        }

        return ret;
    }

    @Override
    public boolean addCategoryToAccount(int accountId, int categoryId) {

        // create statement to add a category to an account
        boolean result1 = false;
        PreparedStatement stmt = null;
        try {
            stmt = c.prepareStatement("INSERT IGNORE INTO accountCategory (AccountId, CategoryId) VALUES (?, ?);");
            stmt.setInt(1, accountId);
            stmt.setInt(2, categoryId);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        }
        // execute query and save result
        result1 = executeStatementUpdate(stmt, false);

        // prepare statement to set categorized = true
        String sqlCommand = "UPDATE accounts SET categorized = 1 WHERE Id = "
                + accountId + ";";

        boolean result2 = false;
        // set categorized = true if the account-category pair was successfully
        // set
        if (result1) {
            // create and execute statement to set categorized = true
            Statement stmt2 = null;
            runningRequest = true;
            try {
                stmt2 = c.createStatement();
                result2 = stmt2.executeUpdate(sqlCommand) >= 0 ? true : false;
            } catch (SQLException e) {
                sqlExceptionLog(e, stmt2);
            } finally {
                runningRequest = false;
                // close mysql-statement
                closeStatement(stmt2);
            }
        }

        return result1 && result2;
    }

    @Override
    public List<Integer> getCategoriesForAccount(String url, String name) {
        // create list of category-Ids to return
        List<Integer> ret = new ArrayList<Integer>();

        // create the statement to look for url and name matches
        ResultSet result = null;
        PreparedStatement stmt = null;
        runningRequest = true;
        try {
            // prepare mysql statement
            stmt = c.prepareStatement("SELECT CategoryId FROM page WHERE Page LIKE ? OR Page LIKE ?;");
            stmt.setString(1, url + "%");
            stmt.setString(2, "%" + name + "%");
            result = stmt.executeQuery();
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
            return new ArrayList<Integer>();
        } finally {
            runningRequest = false;
        }

        // read the mysql-result
        try {
            // add each category-id to the return list
            while (result.next()) {
                ret.add(result.getInt(1));
            }
        } catch (SQLException e) {
            sqlExceptionResultLog(e);
            ret.remove(ret.size() - 1);
        } finally {
            // close mysql statement
            closeResultAndStatement(stmt, result);
        }

        return ret;
    }

    @Override
    public boolean setCategorized(int accountId) {

        // create statment to set the categorized bit of the account to 1
        PreparedStatement stmt = null;
        try {
            // prepare mysql statement
            stmt = c.prepareStatement("UPDATE accounts SET Categorized=1 WHERE Id=?;");
            stmt.setInt(1, accountId);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        }

        // execute query
        return executeStatementUpdate(stmt, false);
    }

    @Override
    public int getParentId(int id) {

        // statement to get the parentId of a category
        String sqlCommand = "SELECT ParentId FROM category WHERE Id=" + id
                + ";";

        // create and execute statement
        ResultSet result = null;
        Statement stmt = null;
        try {
            stmt = c.createStatement();
            result = stmt.executeQuery(sqlCommand);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        }

        int ret = -1;

        // read result
        try {
            result.next();
            ret = result.getInt(1);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        } finally {
            closeResultAndStatement(stmt, result);
        }

        return ret;
    }
}
