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

import locate.Locator;
import twitter4j.GeoLocation;
import twitter4j.Place;
import twitter4j.User;

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
    private Locator locator;

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
    public DBcrawler(AccessData accessData, Locator locator, Logger logger)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException, SQLException {
        super(accessData, logger);
        this.locator = locator;
        connect();
        locationHash = getCountryCodes();
        accountHash = getAccounts();
        disconnect();
    }

    @Override
    public boolean[] addAccount(User user, Place place, GeoLocation geotag,
            Date date, boolean tweet) {

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

            // locate account
            String location = locator.locate(place, geotag, user.getLocation(),
                    user.getTimeZone());

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
            result2 = insertAccount(id, name, user.isVerified(),
                    user.getFollowersCount(), location, url);

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

        return executeStatementUpdate(stmt);
    }

    private boolean updateAccount(long id, int follower) {

        // prevent SQL-injection
        PreparedStatement stmt = null;
        try {
            stmt = c.prepareStatement("UPDATE accounts SET Follower = ? WHERE TwitterAccountId = ? ;");
            stmt.setInt(1, follower);
            stmt.setLong(2, id);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        }

        return executeStatementUpdate(stmt);
    }

    private boolean insertTweet(long id, boolean tweet, Date date) {

        // prevent SQL-injection
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

        return executeStatementUpdate(stmt);
    }

    @Override
    public boolean[] addRetweet(long id, String location, Date date) {

        if (date == null) {
            return new boolean[] {false, false };
        }

        location = checkString(location, 3, DEFAULT_LOCATION);

        boolean result1 = true;
        if (!addLocation(location, null)) {
            location = DEFAULT_LOCATION;
            result1 = false;
        }

        // prevent SQL-injection
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

        boolean result2 = executeStatementUpdate(stmt);

        return new boolean[] {result1, result2 };
    }

    @Override
    public boolean addLocation(String code, String parent) {

        code = checkString(code, 3, "0");
        parent = checkString(parent, 3, null);

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

        // prevent SQL-injection
        PreparedStatement stmt = null;
        try {
            if (parent == null) {
                stmt = c.prepareStatement("INSERT IGNORE INTO location (Name, Code, ParentId) VALUES (\"null\", ?, ?);");
                stmt.setString(1, code);
                stmt.setNull(2, Types.INTEGER);
            } else {
                stmt = c.prepareStatement("INSERT IGNORE INTO location (Name, Code, ParentId) SELECT \"null\", ?, Id FROM location WHERE Code = ?;");
                stmt.setString(1, code);
                stmt.setString(2, parent);
            }
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        }

        boolean ret = executeStatementUpdate(stmt);
        if (ret) {
            locationHash.add(code);
        }

        return ret;
    }

    @Override
    public boolean addDay(Date date) {

        // prevent SQL-injection
        PreparedStatement stmt = null;
        try {
            stmt = c.prepareStatement("INSERT IGNORE INTO day (Day) VALUES (?);");
            stmt.setString(1, dateFormat.format(date));
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        }

        return executeStatementUpdate(stmt);
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

    private String checkString(String word, int maxLength, String byDefault) {
        if (word == null) {
            return byDefault;

        } else {
            String ret = word.replace("\\", "/");
            ret = ret.replace("\"", "\"\"");
            if (ret.length() > maxLength) {
                ret = ret.substring(0, maxLength);
            }
            return ret;
        }
    }

    @Override
    public HashSet<String> getCountryCodes() {

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

        Stack<String> st = new Stack<String>();
        try {
            while (res.next()) {
                st.push(res.getString(1));
            }
        } catch (SQLException e) {
            sqlExceptionResultLog(e);
            return new HashSet<String>();
        } finally {
            closeResultAndStatement(stmt, res);
        }

        HashSet<String> ret = new HashSet<String>(st.size());
        for (String code : st) {
            ret.add(code);
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

        Stack<Long> st = new Stack<Long>();
        try {
            while (res.next()) {
                st.push(res.getLong("TwitterAccountId"));
            }
        } catch (SQLException e) {
            sqlExceptionResultLog(e);
            return new HashSet<Long>();
        } finally {
            closeResultAndStatement(stmt, res);
        }

        HashSet<Long> ret = new HashSet<Long>(st.size());
        for (long l : st) {
            ret.add(l);
        }
        return ret;
    }

}
