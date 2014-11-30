package mysql;

import java.sql.Connection;
import java.util.Date;
import java.util.Stack;
import java.util.logging.Logger;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * class to connect to a MySQL database and to write data into a database
 * 
 * @author Holger Ebhart
 * @version 1.0
 * 
 */
public class DBConnection {

    private final AccessData accessData;
    private Connection c;
    private DateFormat dateFormat;
    private Logger logger;

    /**
     * configurate the connection to the database
     * 
     * @param accessData
     *            the access data to the specified mysql-database as AccessData
     * @param logger
     *            a global logger for the whole program as Logger
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public DBConnection(AccessData accessData, Logger logger)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {

        // load drivers
        Class.forName("org.gjt.mm.mysql.Driver").newInstance();

        this.accessData = accessData;
        this.logger = logger;

        // create date format for the database
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    /**
     * starts the connection to the database
     * 
     * @throws SQLException
     *             thrown if there is no connection to the database possible
     */
    public void connect() throws SQLException {
        // connect to database
        String url = "jdbc:mysql://" + accessData.getHostname() + ":"
                + accessData.getPort() + "/" + accessData.getName();
        c = DriverManager.getConnection(url, accessData.getUser(),
                accessData.getPassword());
        logger.info("Connected to database " + accessData.getName()
                + " with user " + accessData.getUser());
    }

    /**
     * cuts the connection to the database off
     */
    public void disconnect() {
        try {
            c.close();
            logger.info("Disonnected from database " + accessData.getName()
                    + " with user " + accessData.getUser());
        } catch (SQLException e) {
            logger.warning("SQL-Status: " + e.getSQLState() + "\nMessage: "
                    + e.getMessage() + "\n");
            e.printStackTrace();
            // TODO
        }
    }

    /**
     * insert an account into the database
     * 
     * @param name
     *            the name of the account as String
     * @param id
     *            the official twitter id of the account as long
     * @param isVer
     *            true if the account is verified, else false
     * @param follower
     *            the number of followers as int
     * @param location
     *            the location of the account as String
     * @param category
     * @param date
     *            the date of the tweet as Date
     * @param tweet
     *            true if a tweet status object has been read, false if a
     *            retweets status object has been read
     * @return integer-array with two results of the database requests. First is
     *         result for adding the account, second is for adding the tweet.
     *         Database-request result is 0 if request was successfully, 'other'
     *         if line 'other' has been modified (no success)
     */
    private int[] addAccount(String name, long id, boolean isVer, int follower,
            String location, String locationParent, String category, Date date,
            boolean tweet) {

        // insert location
        // writeLocation(location, locationParent);

        // insert account
        String sqlCommand = "INSERT INTO Accounts (AccountId,AccountName,Verified,Follower,Location,Category,UnlocalizedRetweets) VALUES ("
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
                // + category is NULL
                + ", NULL"
                + ","
                + 0
                + ") ON DUPLICATE KEY UPDATE Follower = "
                + follower + ";";

        // System.out.println(sqlCommand);
        Statement s;
        int result1 = -1;
        try {
            s = c.createStatement();
            result1 = s.executeUpdate(sqlCommand);
        } catch (SQLException e) {
            logger.warning("SQL-Status: " + e.getSQLState() + "\nMessage: "
                    + e.getMessage() + "\n");
            result1 = -1;
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // set Tweet count
        sqlCommand = "INSERT INTO Tweets (Account,Counter,Day) VALUES ((SELECT Id FROM Accounts WHERE AccountId = "
                + id
                + "),"
                + (tweet ? "1" : "0")
                + ", (SELECT Id FROM Day WHERE Day = \""
                + dateFormat.format(date)
                + "\" )) ON DUPLICATE KEY UPDATE Counter = Counter + "
                + (tweet ? "1" : "0") + ";";

        int result2 = -1;
        try {
            s = c.createStatement();
            result2 = s.executeUpdate(sqlCommand);
        } catch (SQLException e) {
            logger.warning("SQL-Status: " + e.getSQLState() + "\nMessage: "
                    + e.getMessage() + "\n");
            result2 = -1;
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return new int[] {result1, result2 };
    }

    /**
     * inserts an account into the database
     * 
     * @param name
     *            the name of the account as String
     * @param id
     *            the official twitter id of the account as long
     * @param isVer
     *            true if the account is verified, else false
     * @param follower
     *            the number of followers as int
     * @param location
     *            the location of the account as String
     * @param date
     *            the date of the tweet as Date
     * @param tweet
     *            true if a tweet status object has been read, false if a
     *            retweets status object has been read
     * @return integer-array with two results of the database requests. First is
     *         result for adding the account, second is for adding the tweet.
     *         Database-request result is 0 if request was successfully, 'other'
     *         if line 'other' has been modified (no success)
     */
    public int[] writeAccount(String name, long id, boolean isVer,
            int follower, String location, String locationParent, Date date,
            boolean tweet) {

        return addAccount(name, id, isVer, follower, location, locationParent,
                "category", date, tweet);
    }

    /**
     * inserts a retweet into the database, if it's still in the database the
     * counter will be increased
     * 
     * @param id
     *            the id of the account who's tweet was retweeted as long
     * @param location
     *            TO COMPLETE
     * @param day
     *            the day when the retweet has been written as Date
     * @return database-request result as Integer (0 if request successfully,
     *         'other' if line 'other' has been modified (no success))
     * @throws SQLException
     */
    public int writeRetweet(long id, int location, Date date)
            throws SQLException {
        int count = 0; // getting day counter

        // TODO How to add Day

        String sqlCommand = "INSERT INTO Retweets (Account,Location, Counter, Day) VALUES ("
                + "(SELECT Id FROM Accounts WHERE AccountId = "
                + id
                + "),"
                + 1
                + ","
                + count
                + ","
                + "(SELECT Id FROM Day WHERE Day = \""
                + dateFormat.format(date)
                + "\")"
                + ") ON DUPLICATE KEY UPDATE Counter = Counter + 1";

        Statement s = c.createStatement();

        return s.executeUpdate(sqlCommand);

    }

    /**
     * inserts a new location into the database
     * 
     * @param name
     *            the name of the location as String
     * @param parent
     *            the parent location of the current location as String
     * @return database-request result as Integer (0 if request successfully,
     *         'other' if line 'other' has been modified (no success))
     */
    public int writeLocation(String name, String parent) {

        // TODO what if parent location isn't in database

        String sqlCommand = "INSERT INTO Location (Name, Parent) VALUES (\""
                + name + "\", (SELECT Id FROM Location WHERE Name = \""
                + parent + "\" LIMIT 1)) ON DUPLICATE KEY UPDATE Parent = \""
                + parent + "\";";

        Statement s;
        int ret = -1;
        try {
            s = c.createStatement();
            ret = s.executeUpdate(sqlCommand);
        } catch (SQLException e) {
            logger.warning("SQL-Status: " + e.getSQLState() + "\nMessage: "
                    + e.getMessage() + "\n");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return ret;

    }

    /**
     * inserts a new date into the database
     * 
     * @param date
     *            the date to write into the database as Date
     * @return database-request result as Integer (0 if request successfully,
     *         'other' if line 'other' has been modified (no success))
     */
    public int writeDay(Date date) {

        String sqlCommand = "INSERT INTO Day (Day) VALUES (\""
                + dateFormat.format(date)
                + "\") ON DUPLICATE KEY UPDATE Day = Day";
        Statement s;
        int ret = -1;
        try {
            s = c.createStatement();
            ret = s.executeUpdate(sqlCommand);
        } catch (SQLException e) {
            logger.warning("SQL-Status: " + e.getSQLState() + "\nMessage: "
                    + e.getMessage() + "\n");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * returns all AccountId's that aren't verified
     * 
     * @return all AccountId's from the database that aren't verified as
     *         Integer-Array
     * @throws SQLException
     */
    public long[] getNonVerified() throws SQLException {
        String sqlCommand = "SELECT AccountId FROM Accounts WHERE Verified = 0";
        Statement s = c.createStatement();
        ResultSet res = s.executeQuery(sqlCommand);

        // TODO check if it works right

        Stack<Integer> st = new Stack<Integer>();
        while (res.first()) {
            st.push(res.getRow());
            res.deleteRow();
        }
        long[] ret = new long[st.size()];
        for (int i = 0; i < st.size(); i++) {
            ret[i] = (long) st.pop();
        }
        return ret;
    }

}
