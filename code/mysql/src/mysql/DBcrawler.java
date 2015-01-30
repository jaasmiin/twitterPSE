package mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;
import java.util.logging.Logger;

import twitter4j.User;
import util.Util;

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
     *             thrown if the mysql-driver hasn't been found
     * @throws IllegalAccessException
     *             thrown if the mysql-driver hasn't been found
     * @throws ClassNotFoundException
     *             thrown if the mysql-driver hasn't been found
     * @throws SQLException
     *             thrown if a database-connection couldn't be established now
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
    public boolean[] addAccount(User user, String location, Date date,
            boolean tweet) {

        if (user == null || date == null) {
            return new boolean[] {false, false, false };
        }

        long id = user.getId();
        String url = user.getURL();
        String name = user.getScreenName();

        boolean result1 = false;
        boolean result2 = false;

        if (accountHash.contains(id)) {
            // update follower
            result1 = true;
            result2 = updateAccount(id, user.getFollowersCount());

        } else {
            // add account

            location = Util.checkString(location, 3, DEFAULT_LOCATION);

            name = Util.checkString(name, 30, null);

            url = Util.checkURL(url);

            // insert location
            result1 = true;
            if (!addLocation(location, null)) {
                location = DEFAULT_LOCATION;
                result1 = false;
            }

            // insert account
            result2 = insertAccount(id, name, user.isVerified(),
                    user.getFollowersCount(), location, url);

            // add account to hashSet
            if (result2) {
                accountHash.add(id);
            }
        }

        // set Tweet count
        boolean result3 = false;
        if (result2) {
            result3 = insertTweet(id, tweet, date);
        }

        return new boolean[] {result1, result2, result3 };
    }

    private boolean insertAccount(long id, String name, boolean isVer,
            int follower, String location, String url) {

        PreparedStatement stmt = null;
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
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        }

        return executeStatementUpdate(stmt, true);
    }

    private boolean updateAccount(long id, int follower) {

        PreparedStatement stmt = null;
        try {
            stmt = c.prepareStatement("UPDATE accounts SET Follower = ? WHERE TwitterAccountId = ? ;");
            stmt.setInt(1, follower);
            stmt.setLong(2, id);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        }

        return executeStatementUpdate(stmt, false);
    }

    private boolean insertTweet(long id, boolean tweet, Date date) {

        if (date == null)
            return false;

        PreparedStatement stmt = null;
        try {
            stmt = c.prepareStatement("INSERT INTO tweets (AccountId,Counter,DayId) VALUES ((SELECT Id FROM accounts WHERE TwitterAccountId = ? LIMIT 1), "
                    + (tweet ? "1" : "0")
                    + ", (SELECT Id FROM day WHERE Day = ? LIMIT 1)) ON DUPLICATE KEY UPDATE Counter = Counter + "
                    + (tweet ? "1" : "0") + ";");
            stmt.setLong(1, id);
            stmt.setString(2, dateFormat.format(date));
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        }

        return executeStatementUpdate(stmt, false);
    }

    @Override
    public boolean[] addRetweet(long id, String location, Date date) {

        if (date == null)
            return new boolean[] {false, false };

        location = Util.checkString(location, 3, DEFAULT_LOCATION);

        boolean result1 = true;
        if (!addLocation(location, null)) {
            location = DEFAULT_LOCATION;
            result1 = false;
        }

        PreparedStatement stmt = null;
        try {
            stmt = c.prepareStatement("INSERT INTO retweets (AccountId, LocationId, Counter, DayId) VALUES "
                    + "((SELECT Id FROM accounts WHERE TwitterAccountId = ? LIMIT 1), (SELECT Id FROM location WHERE Code = ? LIMIT 1), 1, (SELECT Id FROM day WHERE Day = ? LIMIT 1)) ON DUPLICATE KEY UPDATE Counter = Counter + 1;");
            stmt.setLong(1, id);
            stmt.setString(2, location);
            stmt.setString(3, dateFormat.format(date));
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        }

        boolean result2 = executeStatementUpdate(stmt, false);

        return new boolean[] {result1, result2 };
    }

    /**
     * inserts a new location into the database
     * 
     * @param code
     *            the country-code of the location as String (max. 3 characters)
     * @param parent
     *            the parent location-code of the current location as String
     *            (could be null, but max. 3 characters)
     * @return database-request result as Boolean
     */
    private boolean addLocation(String code, String parent) {

        code = Util.checkString(code, 3, "0");
        parent = Util.checkString(parent, 3, null);

        // HashTable lookup
        if (locationHash.contains(code)
                && (parent == null || locationHash.contains(parent))) {
            return true;
        }

        // add parent to database
        if (parent != null && !parent.equals(DEFAULT_LOCATION)) {
            if (addLocation(parent, null)) {
                locationHash.add(parent);
            } else {
                parent = null;
            }
        }

        PreparedStatement stmt = null;
        try {
            if (parent == null) {
                stmt = c.prepareStatement("INSERT IGNORE INTO location (Name, Code, ParentId) VALUES (?, ?, ?);");
                stmt.setString(1, code);
                stmt.setString(2, code);
                stmt.setNull(3, Types.INTEGER);
            } else {
                stmt = c.prepareStatement("INSERT IGNORE INTO location (Name, Code, ParentId) SELECT ?, ?, Id FROM location WHERE Code = ?;");
                stmt.setString(1, code);
                stmt.setString(2, code);
                stmt.setString(3, parent);
            }
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        }

        boolean ret = executeStatementUpdate(stmt, false);
        if (ret) {
            locationHash.add(code);
        }

        return ret;
    }

    @Override
    public boolean addDay(Date date) {

        PreparedStatement stmt = null;
        try {
            stmt = c.prepareStatement("INSERT IGNORE INTO day (Day) VALUES (?);");
            stmt.setString(1, dateFormat.format(date));
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        }

        return executeStatementUpdate(stmt, false);
    }

    @Override
    public long[] getNonVerifiedAccounts() {

        String sqlCommand = "SELECT TwitterAccountId FROM accounts WHERE Verified = 0;";

        Statement stmt = null;
        ResultSet res = null;
        try {
            stmt = c.createStatement();
            res = stmt.executeQuery(sqlCommand);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
            return new long[0];
        }

        Stack<Long> st = new Stack<Long>();
        try {
            while (res.next()) {
                st.push(res.getLong("TwitterAccountId"));
            }
        } catch (SQLException e) {
            sqlExceptionResultLog(e);
            return new long[0];
        } finally {
            closeResultAndStatement(stmt, res);
        }

        long[] ret = new long[st.size()];
        for (int i = 0; i < st.size(); i++) {
            ret[i] = st.pop();
        }
        return ret;
    }

    /**
     * returns a hashSet of all the country-codes from the database
     * 
     * @return a hashSet of all the country-codes from the database as
     *         HashSet<String>, empty if an error occurred
     */
    private HashSet<String> getCountryCodes() {

        String sqlCommand = "SELECT Code FROM location;";
        Statement stmt = null;
        ResultSet res = null;
        try {
            stmt = c.createStatement();
            res = stmt.executeQuery(sqlCommand);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
            return new HashSet<String>();
        }

        HashSet<String> ret = new HashSet<String>(100);
        try {
            while (res.next()) {
                ret.add(res.getString(1));
            }
        } catch (SQLException e) {
            sqlExceptionResultLog(e);
            return new HashSet<String>();
        } finally {
            closeResultAndStatement(stmt, res);
        }

        return ret;
    }

    @Override
    public HashSet<Long> getAccounts() {

        String sqlCommand = "SELECT TwitterAccountId FROM accounts;";
        Statement stmt = null;
        ResultSet res = null;
        try {
            stmt = c.createStatement();
            res = stmt.executeQuery(sqlCommand);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
            return new HashSet<Long>();
        }

        HashSet<Long> ret = new HashSet<Long>(10000);
        try {
            while (res.next()) {
                ret.add(res.getLong("TwitterAccountId"));
            }
        } catch (SQLException e) {
            sqlExceptionResultLog(e);
            return new HashSet<Long>();
        } finally {
            closeResultAndStatement(stmt, res);
        }

        return ret;
    }

    @Override
    public HashMap<String, String> getLocationStrings() {

        String sqlCommand = "SELECT Word, TimeZone, Location FROM wordLocation LIMIT 2000000;";
        Statement stmt = null;
        ResultSet res = null;
        try {
            stmt = c.createStatement();
            res = stmt.executeQuery(sqlCommand);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
            return new HashMap<String, String>();
        }

        HashMap<String, String> ret = new HashMap<String, String>(100000);
        try {
            while (res.next()) {
                ret.put(res.getString("Word") + "#" + res.getString("TimeZone"),
                        res.getString("Location"));
            }
        } catch (SQLException e) {
            sqlExceptionResultLog(e);
            return new HashMap<String, String>();
        } finally {
            closeResultAndStatement(stmt, res);
        }

        return ret;
    }

    @Override
    public boolean addLocationString(String code, String word, String timeZone) {

        timeZone = Util.checkString(timeZone, 200, "");
        code = Util.checkString(code, 3, null);
        word = Util.checkString(word, 250, null);
        if (word == null || code == null)
            return false;

        if (!addLocation(code, null))
            return false;

        PreparedStatement stmt = null;
        try {
            stmt = c.prepareStatement("INSERT IGNORE INTO wordLocation (Word, TimeZone, Location) VALUES (?,?,?);");
            stmt.setString(1, word);
            stmt.setString(2, timeZone);
            stmt.setString(3, code);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        }

        return executeStatementUpdate(stmt, false);
    }

    @Override
    public boolean containsAccount(long id) {
        return accountHash.contains(id);
    }

}
