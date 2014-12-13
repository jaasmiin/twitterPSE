package mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Stack;
import java.util.logging.Logger;

/**
 * 
 * class to write data into a database
 * 
 * @author Holger Ebhart
 * @version 1.0
 * 
 */
public class DBcrawler extends DBConnection implements DBICrawler {

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
    public DBcrawler(AccessData accessData, Logger logger)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        super(accessData, logger);
    }

    @Override
    public boolean[] addAccount(String name, long id, boolean isVer,
            int follower, String location, String url, Date date, boolean tweet) {

        // insert location
        // writeLocation(location, locationParent);

        if (url != null) {
            if (url.startsWith("http://www.")) {
                url = url.substring(11, url.length());
            } else if (url.startsWith("http://")) {
                url = url.substring(7, url.length());
            }
            if (url.length() > 100) {
                url = url.substring(0, 99);
            }
        }

        // prevent sql injection
        // if (){
        // return new boolean[] {false, false};
        // }

        // insert account
        String sqlCommand = "INSERT INTO accounts (TwitterAccountId, AccountName, Verified, Follower, LocationId, URL, Categorized) VALUES ("
                + id
                + ",\""
                + name
                + "\","
                + (isVer == true ? "1" : "0")
                + ","
                + follower
                + ","
                // + location
                // + "(SELECT Id FROM Location WHERE Name = \"" + location +
                // "\" LIMIT 1)"
                + 1
                + ","
                + (url == null ? "NULL" : "\"" + url + "\"")
                + ", 0) ON DUPLICATE KEY UPDATE Follower = " + follower + ";";

        // System.out.println(sqlCommand);
        Statement s;
        boolean result1 = false;
        try {
            s = c.createStatement();
            result1 = s.executeUpdate(sqlCommand) == 0 ? true : false;
        } catch (SQLException e) {
            logger.warning("SQL-Status: " + e.getSQLState() + "\nMessage: "
                    + e.getMessage() + "\n");
        }

        // set Tweet count
        sqlCommand = "INSERT INTO tweets (AccountId,Counter,DayId) VALUES ((SELECT Id FROM accounts WHERE TwitterAccountId = "
                + id
                + " LIMIT 1),"
                + (tweet ? "1" : "0")
                + ", (SELECT Id FROM day WHERE Day = \""
                + dateFormat.format(date)
                + "\" LIMIT 1)) ON DUPLICATE KEY UPDATE Counter = Counter + "
                + (tweet ? "1" : "0") + ";";

        boolean result2 = false;
        try {
            s = c.createStatement();
            result2 = s.executeUpdate(sqlCommand) == 0 ? true : false;
        } catch (SQLException e) {
            logger.warning("SQL-Status: " + e.getSQLState() + "\nMessage: "
                    + e.getMessage() + "\n");
        }

        return new boolean[] {result1, result2 };
    }

    @Override
    public boolean writeRetweet(long id, int location, Date date)
            throws SQLException {

        // TODO How to add Day

        // TODO prevent sql injection

        String sqlCommand = "INSERT INTO retweets (AccountId, LocationId, Counter, CounterNonLocalized, DayId) VALUES ("
                + "(SELECT Id FROM accounts WHERE TwitterAccountId = "
                + id
                + "),"
                + 1
                + ","
                + (location == 0 ? 0 : 1)
                + ","
                + (location == 0 ? 1 : 0)
                + ","
                + "(SELECT Id FROM day WHERE Day = \""
                + dateFormat.format(date)
                + "\")"
                + ") ON DUPLICATE KEY UPDATE Counter = Counter + "
                + (location == 0 ? 0 : 1)
                + ", CounterNonLocalized = CounterNonLocalized + "
                + (location == 0 ? 1 : 0);
        // System.out.println(sqlCommand);
        Statement s = c.createStatement();

        return s.executeUpdate(sqlCommand) == 0 ? true : false;

    }

    @Override
    public boolean writeLocation(String name, int parent) {

        // TODO prevent sql injection

        // TODO what if parent location isn't in database

        String sqlCommand = "INSERT INTO location (Name, ParentId) VALUES (\""
                + name + "\", (SELECT Id FROM location WHERE Name = \""
                + parent + "\" LIMIT 1)) ON DUPLICATE KEY UPDATE ParentId = \""
                + parent + "\";";

        Statement s;
        boolean ret = false;
        try {
            s = c.createStatement();
            ret = s.executeUpdate(sqlCommand) == 0 ? true : false;
        } catch (SQLException e) {
            logger.warning("SQL-Status: " + e.getSQLState() + "\nMessage: "
                    + e.getMessage() + "\n");
        }

        return ret;

    }

    @Override
    public boolean writeDay(Date date) {

        String sqlCommand = "INSERT INTO day (Day) VALUES (\""
                + dateFormat.format(date)
                + "\") ON DUPLICATE KEY UPDATE Day = Day";
        Statement s;
        boolean ret = false;
        try {
            s = c.createStatement();
            ret = s.executeUpdate(sqlCommand) == 0 ? true : false;
        } catch (SQLException e) {
            logger.warning("SQL-Status: " + e.getSQLState() + "\nMessage: "
                    + e.getMessage() + "\n");
        }
        return ret;
    }

    @Override
    public long[] getNonVerifiedAccounts() {
        String sqlCommand = "SELECT TwitterAccountId FROM accounts WHERE Verified = 0";

        ResultSet res = null;
        try {
            Statement s = c.createStatement();
            res = s.executeQuery(sqlCommand);
        } catch (SQLException e) {
            logger.warning("Couldn't execute sql query\n" + e.getMessage());
            return null;
        }

        Stack<Integer> st = new Stack<Integer>();
        try {
            while (res.next()) {
                st.push(res.getInt("TwitterAccountId"));
            }
        } catch (SQLException e) {
            logger.warning("Couldn't read sql result\n" + e.getMessage());
            return null;
        }
        long[] ret = new long[st.size()];
        for (int i = 0; i < st.size(); i++) {
            ret[i] = (long) st.pop();
        }
        return ret;
    }

}
