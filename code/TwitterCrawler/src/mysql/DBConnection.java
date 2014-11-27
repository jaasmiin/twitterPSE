package mysql;

import java.sql.Connection;
import java.util.Date;
import java.sql.DriverManager;
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

    private final String hostname;
    private final String port;
    private final String db;
    private final String user;
    private final String pw;
    private Connection c;
    private DateFormat dateFormat;

    /**
     * configurate the connection to the database
     * 
     * @param hostName
     *            the haostname of the mysql database as String
     * @param port
     *            the port of the mysql database as String
     * @param dbName
     *            the database-name as String
     * @param userName
     *            the user name for the mysql database as String
     * @param password
     *            the password for the mysql database as String
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public DBConnection(String hostName, String port, String dbName,
            String userName, String password) throws InstantiationException,
            IllegalAccessException, ClassNotFoundException {

        // load drivers
        Class.forName("org.gjt.mm.mysql.Driver").newInstance();

        // set acces-data
        this.hostname = hostName;
        this.port = port;
        this.db = dbName;
        this.user = userName;
        this.pw = password;

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
        String url = "jdbc:mysql://" + hostname + ":" + port + "/" + db;
        c = DriverManager.getConnection(url, user, pw);
    }

    /**
     * cuts the connection to the database off
     */
    public void disconnect() {
        try {
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO
        }
    }

    private int[] addAccount(String name, long id, boolean isVer, int follower,
            String location, String category, Date date) throws SQLException {

        String sqlCommand = "INSERT INTO Accounts (AccountId,AccountName,Verified,Follower,Location,Category,UnlocalizedRetweets) VALUES ("
                + id
                + ",\""
                + name
                + "\","
                + (isVer == true ? "1" : "0")
                + ","
                + follower + ","
                // + location
                + 1 + ", NULL"
                // + category
                + "," + 0 + ") ON DUPLICATE KEY UPDATE Id = Id;";

        // System.out.println(sqlCommand);
        Statement s = c.createStatement();
        int result1 = s.executeUpdate(sqlCommand);

        // set Tweet count
        sqlCommand = "INSERT INTO Tweets (Account,Counter,Day) VALUES ((SELECT Id FROM Accounts WHERE AccountId = "
                + id
                + "),"
                + 1
                + ", (SELECT Id FROM Day WHERE Day = \""
                + dateFormat.format(date)
                + "\" )) ON DUPLICATE KEY UPDATE Counter = Counter + 1;";
        int result2 = s.executeUpdate(sqlCommand);

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
     * @return integer-array with two results of the database requests. First is
     *         result for adding the account, second is for adding the tweet.
     *         Database-request result is 0 if request was successfully, 'other'
     *         if line 'other' has been modified (no success)
     * @throws SQLException
     */
    public int[] writeAccount(String name, long id, boolean isVer,
            int follower, String location, Date date) throws SQLException {

        return addAccount(name, id, isVer, follower, location, "", date);
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

        // TODO add Day
        String sqlCommand = "INSERT INTO Retweets (Account,Location, Counter, Day) VALUES ("
                + "(SELECT Id FROM Accounts WHERE AccountId = "
                + id
                + "),"
                + 1
                + ","
                + count
                + ","
                + 1
                + ") ON DUPLICATE KEY UPDATE Counter = Counter + 1";
        // System.out.println(sqlCommand);
        Statement s = c.createStatement();
        int result = s.executeUpdate(sqlCommand);
        return result;

    }

    /**
     * inserts a new location into the database
     * 
     * @param name
     *            TO COMPLETE
     * @param parent
     *            TO COMPLETE
     * @return database-request result as Integer (0 if request successfully,
     *         'other' if line 'other' has been modified (no success))
     * @throws SQLException
     */
    public int writeLocation(String name, String parent) throws SQLException {

        String sqlCommand = "INSERT INTO Location (Name, Parent) VALUES (\""
                + name + "\", (SELECT Id FROM Location WHERE Name = \""
                + parent + "\" LIMIT 1))";

        Statement s = c.createStatement();

        return s.executeUpdate(sqlCommand);

    }

    /**
     * write a new date into the database
     * 
     * @param date
     *            the date to write into the database as Date
     * @return database-request result as Integer (0 if request successfully,
     *         'other' if line 'other' has been modified (no success))
     * @throws SQLException
     */
    public int writeDay(Date date) throws SQLException {

        String sqlCommand = "INSERT INTO Day (Day) VALUES (\""
                + dateFormat.format(date) + "\")";
        Statement s = c.createStatement();
        return s.executeUpdate(sqlCommand);
    }

}
