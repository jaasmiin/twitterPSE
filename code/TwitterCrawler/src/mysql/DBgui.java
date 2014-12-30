package mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Logger;

import twitter4j.User;
import mysql.result.Account;
import mysql.result.Category;
import mysql.result.Location;
import mysql.result.Retweets;

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

        List<Category> list = new ArrayList<Category>();
        try {
            while (res.next()) {
                list.add(new Category(res.getInt("Id"), res.getString("Name"),
                        null));
            }
        } catch (SQLException e) {
            logger.warning("Couldn't read sql result: \n" + e.getMessage());
            return null;
        }

        Category[] ret = new Category[list.size()];
        int i = 0;
        for (Category c : list) {
            // TODO parent relationship
            ret[i++] = c;
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

        List<Location> list = new ArrayList<Location>();
        try {
            while (res.next()) {
                list.add(new Location(res.getInt("Id"), res.getString("Name"),
                        res.getString("Code"), null));
            }
        } catch (SQLException e) {
            logger.warning("Couldn't read sql result: \n" + e.getMessage());
            return null;
        }

        Location[] ret = new Location[list.size()];
        int i = 0;
        for (Location l : list) {
            // TODO parent relationship
            ret[i++] = l;
        }
        return ret;
    }

    @Override
    public int getAccountId(String accountName) {

        PreparedStatement stmt = null;
        ResultSet res = null;
        try {
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
    public Account[] getData(int[] categoryIds, int[] countryIds, boolean separateDate) {

        // get sum of retweets

        // TODO
        return null;
        // TODO
        // String sqlCommand =
        // "SELECT Id, TwitterAccountId, AccountName, URL, Follower, LocationId FROM accounts WHERE Id = ();";
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

    @Override
    public Account[] getAccounts(String search) {

        // prevent SQL-injection
        PreparedStatement stmt = null;
        ResultSet res = null;
        try {
            stmt = c.prepareStatement("SELECT Id, TwitterAccountId, AccountName,Verified, Follower, URL, LocationId FROM accounts WHERE AccountName LIKE ? LIMIT 50;");
            stmt.setString(1, "%" + search + "%");
            res = stmt.executeQuery();
        } catch (SQLException e) {
            logger.warning("SQL-Status: " + e.getSQLState() + "\n Message: "
                    + e.getMessage() + "\n SQL-Query: " + stmt + "\n");
        }

        if (res == null)
            return null;

        Stack<Account> st = new Stack<Account>();
        try {
            while (res.next()) {
                st.push(new Account(res.getInt("Id"), res
                        .getLong("TwitterAccountId"), res
                        .getString("AccountName"), res.getBoolean("Verified"),
                        res.getString("URL"), res.getInt("Follower"), res
                                .getInt("LocationId")));
            }
        } catch (SQLException e) {
            logger.warning("Couldn't read sql result: \n" + e.getMessage());
            return null;
        }
        Account[] ret = new Account[st.size()];
        for (int i = 0; i < st.size(); i++) {
            ret[i] = st.pop();
        }
        return ret;
    }

    @Override
    public boolean setCategory(int accountId, int categoryId) {

        // prevent SQL-injection
        PreparedStatement stmt = null;
        boolean ret = false;
        try {
            stmt = c.prepareStatement("INSERT IGNORE INTO accountCategory (AccountId, CategoryId) VALUES (?, ?);");
            stmt.setInt(1, accountId);
            stmt.setInt(2, categoryId);
            ret = stmt.executeUpdate() != 0 ? true : false;
        } catch (SQLException e) {
            logger.warning("SQL-Status: " + e.getSQLState() + "\n Message: "
                    + e.getMessage() + "\n SQL-Query: " + stmt + "\n");
        }

        return ret;
    }

    @Override
    public boolean setLocation(int accountId, int locationId, boolean active) {

        // prevent SQL-injection
        PreparedStatement stmt = null;
        boolean ret = false;
        try {
            stmt = c.prepareStatement("UPDATE accounts SET LocationId = ? WHERE Id = ?;");
            stmt.setInt(1, accountId);
            stmt.setInt(2, locationId);
            ret = stmt.executeUpdate() != 0 ? true : false;
        } catch (SQLException e) {
            logger.warning("SQL-Status: " + e.getSQLState() + "\n Message: "
                    + e.getMessage() + "\n SQL-Query: " + stmt + "\n");
        }

        return ret;
    }

    @Override
    public boolean addAccount(User user, int locationId) {
        // prevent SQL-injection
        PreparedStatement stmt = null;
        boolean ret = false;
        try {
            stmt = c.prepareStatement("INSERT IGNORE INTO accounts (TwitterAccountId, AccountName, Verified, Follower, LocationId, URL, Categorized) VALUES (?, ?, "
                    + (user.isVerified() ? "1" : "0") + ", ?, ?, ?, 0);");
            stmt.setLong(1, user.getId());
            stmt.setString(2, user.getScreenName());
            stmt.setInt(3, user.getFollowersCount());
            stmt.setInt(4, locationId);
            stmt.setString(5,
                    user.getURL().replace("\\", "/").replace("\"", "\"\""));
            ret = stmt.executeUpdate() != 0 ? true : false;
        } catch (SQLException e) {
            logger.warning("SQL-Status: " + e.getSQLState() + "\n Message: "
                    + e.getMessage() + "\n SQL-Query: " + stmt + "\n");
        }

        return ret;
    }

}
