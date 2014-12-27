package mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Stack;
import java.util.logging.Logger;

import mysql.result.Account;
import mysql.result.Category;
import mysql.result.Location;

/**
 * class to modify the database restricted
 * 
 * @author Holger Ebhart
 * @version 1.0
 * 
 */
public class DBgui extends DBConnection implements DBIgui {

    /**
     * configure the connection to the database
     * 
     * @param accessData
     *            the access data to the specified mysql-database as AccessData
     * @param logger
     *            a global logger for the whole program as Logger
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public DBgui(AccessData accessData, Logger logger)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        super(accessData, logger);
    }

    @Override
    public Category[] getCategories() {
        String sqlCommand = "SELECT * FROM category;";

        ResultSet res = null;
        try {
            Statement s = c.createStatement();
            res = s.executeQuery(sqlCommand);
        } catch (SQLException e) {
            logger.warning("Couldn't execute sql query: \n" + e.getMessage());
            return null;
        }

        Stack<Category> st = new Stack<Category>();
        try {
            while (res.next()) {
                // TODO parent relationship
                st.push(new Category(res.getInt("Id"), res.getString("Name"),
                        null));
            }
        } catch (SQLException e) {
            logger.warning("Couldn't read sql result: \n" + e.getMessage());
            return null;
        }

        // TODO parent relationship

        Category[] ret = new Category[st.size()];
        for (int i = 0; i < st.size(); i++) {
            ret[i] = st.pop();
        }
        return ret;
    }

    @Override
    public Location[] getLocations() {
        String sqlCommand = "SELECT * FROM location;";

        ResultSet res = null;
        try {
            Statement s = c.createStatement();
            res = s.executeQuery(sqlCommand);
        } catch (SQLException e) {
            logger.warning("Couldn't execute sql query: \n" + e.getMessage());
            return null;
        }

        Stack<Location> st = new Stack<Location>();
        try {
            while (res.next()) {
                // TODO parent relationship
                st.push(new Location(res.getInt("Id"), res.getString("Name"),
                        res.getString("Code"), null));
            }
        } catch (SQLException e) {
            logger.warning("Couldn't read sql result: \n" + e.getMessage());
            return null;
        }

        // TODO parent relationship

        Location[] ret = new Location[st.size()];
        for (int i = 0; i < st.size(); i++) {
            ret[i] = st.pop();
        }
        return ret;
    }

    @Override
    public int getAccountId(String accountName) {

        PreparedStatement stmt = null;
        ResultSet res = null;
        try {
            // TODO Id or TwitterAccountId
            stmt = c.prepareStatement("SELECT Id FROM accounts WHERE AccountName = ? LIMIT 1;");
            stmt.setString(1, accountName);
            res = stmt.executeQuery();
        } catch (SQLException e) {
            logger.warning("SQL-Status: " + e.getSQLState() + "\n Message: "
                    + e.getMessage() + "\n SQL-Query: " + stmt + "\n");
            return -1;
        }

        int ret = -1;
        try {
            ret = res.getInt("Id");
        } catch (SQLException e) {
            ret = -1;
        }

        return ret;
    }

    @Override
    public Account[] getData(int[] categoryIds, int[] countryIds) {

        // TODO
        return null;
        // TODO
        // String sqlCommand =
        // "SELECT (Id, TwitterAccountId, AccountName, URL, Follower, LocationId) FROM accounts WHERE Id = ();";
        //
        // ResultSet res = null;
        // try {
        // Statement s = c.createStatement();
        // res = s.executeQuery(sqlCommand);
        // } catch (SQLException e) {
        // logger.warning("Couldn't execute sql query: \n" + e.getMessage());
        // return null;
        // }
        //
        // Stack<Account> st = new Stack<Account>();
        // try {
        // while (res.next()) {
        // st.push(new Account(res.getInt("Id"), res
        // .getLong("TwitterAccountId"), res
        // .getString("AccountName"), res.getString("URL"), res
        // .getInt("Follower"), res.getInt("LocationId")));
        // }
        // } catch (SQLException e) {
        // logger.warning("Couldn't read sql result: \n" + e.getMessage());
        // return null;
        // }
        // Account[] ret = new Account[st.size()];
        // for (int i = 0; i < st.size(); i++) {
        // ret[i] = st.pop();
        // }
        // return ret;
    }

    // @Override
    // public Account[] getAccounts() {
    // // TODO Auto-generated method stub
    // return null;
    // }

    @Override
    public Account[] getAccounts(String search) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean addAccount() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean setCategory(int accountId, int categoryId) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean setLocation(int accountId, int locationId) {
        // TODO Auto-generated method stub
        return false;
    }

}
