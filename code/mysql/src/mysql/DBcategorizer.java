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
 * @version 1.1
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

        String sqlCommand = "SELECT Id, URL FROM accounts WHERE Categorized = 0;";

        // create and execute statement to get uncategorized accounts
        ResultSet result = null;
        Statement stmt = null;
        try {
            stmt = c.createStatement();
            result = stmt.executeQuery(sqlCommand);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
            return new ArrayList<Account>();
        }

        // create list of accounts to return
        List<Account> ret = new ArrayList<Account>();
        try {
            // read mysql-result and add accounts into the return list
            while (result.next()) {
                ret.add(new Account(result.getInt("Id"), result
                        .getString("URL")));
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
            try {
                stmt2 = c.createStatement();
                result2 = stmt2.executeUpdate(sqlCommand) >= 0 ? true : false;
            } catch (SQLException e) {
                sqlExceptionLog(e, stmt2);
            } finally {
                // close mysql-statement
                if (stmt2 != null) {
                    try {
                        stmt2.close();
                    } catch (SQLException e) {
                        sqlExceptionLog(e);
                    }
                }
            }
        }

        return result1 && result2;
    }

    @Override
    public List<Integer> getCategoriesForAccount(String url) {

        // create the statement to look for url matches
        ResultSet result = null;
        PreparedStatement stmt = null;
        try {
            stmt = c.prepareStatement("SELECT CategoryId FROM page WHERE Page LIKE ? LIMIT 100;");
            stmt.setString(1, url + "%");
            result = stmt.executeQuery();
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
            return new ArrayList<Integer>();
        }

        // create list of category-Ids to return
        List<Integer> ret = new ArrayList<Integer>();
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
            stmt = c.prepareStatement("UPDATE accounts SET Categorized=1 WHERE Id=?;");
            stmt.setInt(1, accountId);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        }

        // execute query
        return executeStatementUpdate(stmt, false);
    }
}
