package mysql;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.logging.Logger;

/**
 * 
 * class to write data into a database
 * 
 * @author Holger Ebhart
 * @version 1.0
 * 
 */
public class DBWrite extends DBConnection implements Write {

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
    public DBWrite(AccessData accessData, Logger logger)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        super(accessData, logger);
    }

    @Override
    public int[] addAccount(String name, long id, boolean isVer, int follower,
            String location, String locationParent, String url, Date date,
            boolean tweet) {

        // TODO avoid sql injection

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

        // insert account
        String sqlCommand = "INSERT INTO accounts (AccountId,AccountName,Verified,Follower,Location,URL) VALUES ("
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
                + ") ON DUPLICATE KEY UPDATE Follower = " + follower + ";";

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
        sqlCommand = "INSERT INTO tweets (Account,Counter,Day) VALUES ((SELECT Id FROM accounts WHERE AccountId = "
                + id
                + "),"
                + (tweet ? "1" : "0")
                + ", (SELECT Id FROM day WHERE Day = \""
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

    @Override
    public int writeRetweet(long id, int location, Date date)
            throws SQLException {

        // TODO How to add Day

        String sqlCommand = "INSERT INTO retweets (Account,Location, Counter,CounterNonLocalized, Day) VALUES ("
                + "(SELECT Id FROM accounts WHERE AccountId = "
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

        return s.executeUpdate(sqlCommand);

    }

    @Override
    public int writeLocation(String name, String parent) {

        // TODO avoid sql injection

        // TODO what if parent location isn't in database

        String sqlCommand = "INSERT INTO location (Name, Parent) VALUES (\""
                + name + "\", (SELECT Id FROM location WHERE Name = \""
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

    @Override
    public int writeDay(Date date) {

        String sqlCommand = "INSERT INTO day (Day) VALUES (\""
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

}
