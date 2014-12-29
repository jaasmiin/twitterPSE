package mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Date;
import java.util.HashSet;
import java.util.Stack;
import java.util.logging.Logger;

/**
 * 
 * class to write data into a database
 * 
 * @author Holger Ebhart
 * @version 1.1
 * 
 */
public class DBcrawler extends DBConnection implements DBIcrawler {

    private static final String DEFAULT_LOCATION = "0";
    private HashSet<String> locationHash;
    private HashSet<Long> accountHash;

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
     * @throws SQLException
     */
    public DBcrawler(AccessData accessData, Logger logger)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException, SQLException {
        super(accessData, logger);
        connect();
        locationHash = getCountryCodes();
        accountHash = getAccounts();
        disconnect();
    }

    @Override
    public boolean[] addAccount(String name, long id, boolean isVer,
            int follower, String location, String url, Date date, boolean tweet) {

        boolean result1 = false;
        boolean result2 = false;

        if (accountHash.contains(id)) {
            // update follower
            result1 = true;
            result2 = updateAccount(id, follower);

        } else {
            // add account
            location = checkString(location, 3, DEFAULT_LOCATION);
            name = checkString(name, 30, null);

            if (url != null) {
                if (url.startsWith("http://www.")) {
                    url = url.substring(11, url.length());
                } else if (url.startsWith("http://")) {
                    url = url.substring(7, url.length());
                }
                url = checkString(url, 100, null);
            }

            // insert location
            result1 = true;
            if (!addLocation(location, null)) {
                location = DEFAULT_LOCATION;
                result1 = false;
            }

            // insert account
            result2 = insertAccount(id, name, isVer, follower, location, url);

            // add account to hashSet
            if (result2) {
                accountHash.add(id);
            }
        }

        // set Tweet count
        boolean result3 = insertTweet(id, tweet, date);

        return new boolean[] {result1, result2, result3 };
    }

    private boolean insertAccount(long id, String name, boolean isVer,
            int follower, String location, String url) {

        // prevent SQL-injection
        PreparedStatement stmt = null;
        boolean ret = false;
        try {
            stmt = c.prepareStatement("INSERT INTO accounts (TwitterAccountId, AccountName, Verified, Follower, LocationId, URL, Categorized) VALUES ( ? , ?, "
                    + (isVer == true ? "1" : "0")
                    + ", ? , (SELECT Id FROM location WHERE Code = ? LIMIT 1), ? , 0) ON DUPLICATE KEY UPDATE Follower = ? ;");
            stmt.setLong(1, id);
            stmt.setString(2, name);
            stmt.setInt(3, follower);
            stmt.setString(4, location);
            if (url == null) {
                stmt.setNull(5, Types.VARCHAR);
            } else {
                stmt.setString(5, url);
            }
            stmt.setInt(6, follower);
            ret = stmt.executeUpdate() != 0 ? true : false;
        } catch (SQLException e) {
            logger.warning("SQL-Status: " + e.getSQLState() + "\n Message: "
                    + e.getMessage() + "\n SQL-Query: " + stmt + "\n");
        }

        return ret;
    }

    private boolean updateAccount(long id, int follower) {

        // prevent SQL-injection
        PreparedStatement stmt = null;
        boolean ret = false;
        try {
            stmt = c.prepareStatement("UPDATE accounts SET Follower = ? WHERE TwitterAccountId = ? ;");
            stmt.setInt(1, follower);
            stmt.setLong(2, id);
            ret = stmt.executeUpdate() != 0 ? true : false;
        } catch (SQLException e) {
            logger.warning("SQL-Status: " + e.getSQLState() + "\n Message: "
                    + e.getMessage() + "\n SQL-Query: " + stmt + "\n");
        }

        return ret;
    }

    private boolean insertTweet(long id, boolean tweet, Date date) {

        // prevent SQL-injection
        PreparedStatement stmt = null;
        boolean ret = false;
        try {
            stmt = c.prepareStatement("INSERT INTO tweets (AccountId,Counter,DayId) VALUES ((SELECT Id FROM accounts WHERE TwitterAccountId = ? LIMIT 1), "
                    + (tweet ? "1" : "0")
                    + ", (SELECT Id FROM day WHERE Day = ? LIMIT 1)) ON DUPLICATE KEY UPDATE Counter = Counter + "
                    + (tweet ? "1" : "0") + ";");
            stmt.setLong(1, id);
            stmt.setString(2, dateFormat.format(date));
            ret = stmt.executeUpdate() != 0 ? true : false;
        } catch (SQLException e) {
            logger.warning("SQL-Status: " + e.getSQLState() + "\n Message: "
                    + e.getMessage() + "\n SQL-Query: " + stmt + "\n");
        }

        return ret;
    }

    @Override
    public boolean[] addRetweet(long id, String location, Date date) {

        location = checkString(location, 3, DEFAULT_LOCATION);

        boolean result1 = true;
        if (!addLocation(location, null)) {
            location = DEFAULT_LOCATION;
            result1 = false;
        }

        // prevent SQL-injection
        PreparedStatement stmt = null;
        boolean result2 = false;
        try {
            stmt = c.prepareStatement("INSERT INTO retweets (AccountId, LocationId, Counter, DayId) VALUES "
                    + "((SELECT Id FROM accounts WHERE TwitterAccountId = ? LIMIT 1), (SELECT Id FROM location WHERE Code = ? LIMIT 1), 1, (SELECT Id FROM day WHERE Day = ? LIMIT 1)) ON DUPLICATE KEY UPDATE Counter = Counter + 1;");
            stmt.setLong(1, id);
            stmt.setString(2, location);
            stmt.setString(3, dateFormat.format(date));
            result2 = stmt.executeUpdate() != 0 ? true : false;
        } catch (SQLException e) {
            logger.warning("SQL-Status: " + e.getSQLState() + "\n Message: "
                    + e.getMessage() + "\n SQL-Query: " + stmt + "\n");
        }

        return new boolean[] {result1, result2 };
    }

    @Override
    public boolean addLocation(String code, String parent) {

        // HashTable lookup
        if (locationHash.contains(code)
                && (parent == null || locationHash.contains(parent))) {
            return true;
        }

        // add parent to database
        if (parent != null && parent != DEFAULT_LOCATION) {
            if (addLocation(parent, null)) {
                locationHash.add(parent);
            }
        }

        // prevent SQL-injection
        boolean ret = false;
        PreparedStatement stmt = null;
        try {
            if (parent == null) {
                stmt = c.prepareStatement("INSERT IGNORE INTO location (Name, Code, ParentId) VALUES (\"null\", ?, ?);");
                stmt.setString(1, code);
                stmt.setNull(2, Types.INTEGER);
            } else {
                stmt = c.prepareStatement("INSERT IGNORE INTO location (Name, Code, ParentId) VALUES (\"null\", ?, (SELECT Id FROM location WHERE Code = ? LIMIT 1));");
                stmt.setString(1, code);
                stmt.setString(2, parent);
            }
            ret = stmt.executeUpdate() != 0 ? true : false;
            if (ret) {
                locationHash.add(code);
            }
        } catch (SQLException e) {
            logger.warning("SQL-Status: " + e.getSQLState() + "\n Message: "
                    + e.getMessage() + "\n SQL-Query: " + stmt + "\n");
        }

        return ret;
    }

    @Override
    public boolean addDay(Date date) {

        // prevent SQL-injection
        PreparedStatement stmt = null;
        boolean ret = false;
        try {
            stmt = c.prepareStatement("INSERT IGNORE INTO day (Day) VALUES (?);");
            stmt.setString(1, dateFormat.format(date));
            ret = stmt.executeUpdate() != 0 ? true : false;
        } catch (SQLException e) {
            logger.warning("SQL-Status: " + e.getSQLState() + "\n Message: "
                    + e.getMessage() + "\n SQL-Query: " + stmt + "\n");
        }

        return ret;
    }

    @Override
    public long[] getNonVerifiedAccounts() {

        String sqlCommand = "SELECT TwitterAccountId FROM accounts WHERE Verified = 0;";

        ResultSet res = null;
        try {
            Statement s = c.createStatement();
            res = s.executeQuery(sqlCommand);
        } catch (SQLException e) {
            logger.warning("Couldn't execute sql query: \n" + e.getMessage());
            return null;
        }

        Stack<Long> st = new Stack<Long>();
        try {
            while (res.next()) {
                st.push(res.getLong("TwitterAccountId"));
            }
        } catch (SQLException e) {
            logger.warning("Couldn't read sql result: \n" + e.getMessage());
            return null;
        }
        long[] ret = new long[st.size()];
        for (int i = 0; i < st.size(); i++) {
            ret[i] = st.pop();
        }
        return ret;
    }

    private String checkString(String word, int maxLength, String byDefault) {
        if (word == null) {
            return byDefault;

        } else {
            String ret = word.replace("\\", "/");
            ret = ret.replace("\"", "\"\"");
            if (ret.length() > maxLength) {
                ret = ret.substring(0, maxLength - 1);
            }
            return ret;
        }
    }

    @Override
    public HashSet<String> getCountryCodes() {

        String sqlCommand = "SELECT Code FROM location;";

        ResultSet res = null;
        try {
            Statement s = c.createStatement();
            res = s.executeQuery(sqlCommand);
        } catch (SQLException e) {
            logger.warning("Couldn't execute sql query: \n" + e.getMessage());
            return new HashSet<String>();
        }

        Stack<String> st = new Stack<String>();
        try {
            while (res.next()) {
                st.push(res.getString("Code"));
            }
        } catch (SQLException e) {
            logger.warning("Couldn't read sql result: \n" + e.getMessage());
            return new HashSet<String>();
        }

        HashSet<String> ret = new HashSet<String>(st.size());
        for (int i = 0; i < st.size(); i++) {
            ret.add(st.pop());
        }
        return ret;
    }

    @Override
    public HashSet<Long> getAccounts() {
        String sqlCommand = "SELECT TwitterAccountId FROM accounts;";

        ResultSet res = null;
        try {
            Statement s = c.createStatement();
            res = s.executeQuery(sqlCommand);
        } catch (SQLException e) {
            logger.warning("Couldn't execute sql query: \n" + e.getMessage());
            return new HashSet<Long>();
        }

        Stack<Long> st = new Stack<Long>();
        try {
            while (res.next()) {
                st.push(res.getLong("TwitterAccountId"));
            }
        } catch (SQLException e) {
            logger.warning("Couldn't read sql result: \n" + e.getMessage());
            return new HashSet<Long>();
        }

        HashSet<Long> ret = new HashSet<Long>(st.size());
        for (int i = 0; i < st.size(); i++) {
            ret.add((long) st.pop());
        }
        return ret;
    }

}
