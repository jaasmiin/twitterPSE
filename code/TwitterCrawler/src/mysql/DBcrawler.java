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

    private static final String DEFAULT_LOCATION = "0";

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

    // SQL PREVENTION
    //
    // PreparedStatement stmt =
    // connection.prepareStatement("SELECT * FROM users WHERE userid=? AND password=?");
    // stmt.setString(1, userid);
    // stmt.setString(2, password);
    // ResultSet rs = stmt.executeQuery();

    @Override
    public boolean[] addAccount(String name, long id, boolean isVer,
            int follower, String location, String url, Date date, boolean tweet) {

        location = checkString(location, 3);
        name = checkString(name, 30);

        // insert location
        boolean result1 = true;
        if (!addLocation(location, null)) {
            location = DEFAULT_LOCATION;
            result1 = false;
        }

        if (url != null) {
            if (url.startsWith("http://www.")) {
                url = url.substring(11, url.length());
            } else if (url.startsWith("http://")) {
                url = url.substring(7, url.length());
            }
            url = checkString(url, 100);
        }

        // TODO prevent sql injection

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
                + "(SELECT Id FROM location WHERE Code = \""
                + location
                + "\" LIMIT 1)"
                + ","
                + (url == null ? "NULL" : "\"" + url + "\"")
                + ", 0) ON DUPLICATE KEY UPDATE Follower = " + follower + ";";

        Statement s;
        boolean result2 = false;
        try {
            s = c.createStatement();
            result2 = s.executeUpdate(sqlCommand) == 0 ? true : false;
        } catch (SQLException e) {
            logger.warning("SQL-Status: " + e.getSQLState() + "\nMessage: "
                    + e.getMessage() + "\n SQL-Query: " + sqlCommand + "\n");
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

        boolean result3 = false;
        try {
            s = c.createStatement();
            result3 = s.executeUpdate(sqlCommand) == 0 ? true : false;
        } catch (SQLException e) {
            logger.warning("SQL-Status: " + e.getSQLState() + "\nMessage: "
                    + e.getMessage() + "\nDatum: " + dateFormat.format(date)
                    + "\n SQL-Query: " + sqlCommand + "\n");
        }

        return new boolean[] {result1, result2, result3 };
    }

    @Override
    public boolean[] addRetweet(long id, String location, Date date) {

        // TODO prevent sql injection

        location = checkString(location, 3);
        boolean result1 = true;
        if (!addLocation(location, null)) {
            location = DEFAULT_LOCATION;
            result1 = false;
        }

        String sqlCommand = "INSERT INTO retweets (AccountId, LocationId, Counter, DayId) VALUES ("
                + "(SELECT Id FROM accounts WHERE TwitterAccountId = "
                + id
                + "),"
                + "(SELECT Id FROM location WHERE Code = \""
                + (location == null ? DEFAULT_LOCATION : location)
                + "\" LIMIT 1)"
                + ","
                + 1
                + ","
                + "(SELECT Id FROM day WHERE Day = \""
                + dateFormat.format(date)
                + "\")"
                + ") ON DUPLICATE KEY UPDATE Counter = Counter + 1;";

        Statement s;
        boolean result2 = false;
        try {
            s = c.createStatement();
            result2 = s.executeUpdate(sqlCommand) == 0 ? true : false;
        } catch (SQLException e) {
            logger.warning("SQL-Status: " + e.getSQLState() + "\nMessage: "
                    + e.getMessage() + "\n SQL-Query: " + sqlCommand + "\n");
        }
        return new boolean[] {result1, result2 };

    }

    @Override
    public boolean addLocation(String code, String parent) {

        code = checkString(code, 3);
        if (code == null) {
            return true;
        }

        if (parent != null) {
            parent = parent.replace("\\", "/");
            if (parent.length() > 3 || parent.contains("\"")) {
                parent = null;
            }
        }

        // TODO prevent sql injection

        // add parent to database
        if (parent != null) {
            addLocation(parent, null);
        }

        String parentId = parent != null ? ("(SELECT Id FROM location WHERE Code = \""
                + parent + "\" LIMIT 1)")
                : "NULL";
        String sqlCommand = "INSERT INTO location (Name, Code, ParentId) VALUES (\"null\", \""
                + code
                + "\", "
                + parentId
                + ") ON DUPLICATE KEY UPDATE ParentId = " + parentId + ";";

        Statement s;
        boolean ret = false;
        try {
            s = c.createStatement();
            ret = s.executeUpdate(sqlCommand) == 0 ? true : false;
        } catch (SQLException e) {
            logger.warning("SQL-Status: " + e.getSQLState() + "\nMessage: "
                    + e.getMessage() + "\n SQL-Query: " + sqlCommand + "\n");
        }

        return ret;

    }

    @Override
    public boolean addDay(Date date) {

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

    private String checkString(String word, int maxLength) {
        if (word == null || word.contains("\"")) {
            return null;

        } else {
            String ret = word.replace("\\", "/");
            // TODO
            ret = ret.replace("\"", "\"\"");
            if (ret.length() > maxLength) {
                ret = ret.substring(0, maxLength - 1);
            }
            return ret;
        }
    }

}
