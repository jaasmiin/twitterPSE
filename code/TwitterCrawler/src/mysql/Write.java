package mysql;

import java.sql.SQLException;
import java.util.Date;

/**
 * interface for writing data into a database
 * 
 * @author Holger Ebhart
 * @version 1.0
 */
public interface Write {

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
     * @param url
     *            the url of the verified user as String
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
    public int[] addAccount(String name, long id, boolean isVer, int follower,
            String location, String locationParent, String url, Date date,
            boolean tweet);

    /**
     * inserts a retweet into the database, if it's still in the database the
     * counter will be increased
     * 
     * @param id
     *            the id of the account who's tweet was retweeted as long
     * @param location
     *            TO COMPLETE 0 if non localizable
     * @param date
     *            the day when the retweet has been written as Date
     * @return database-request result as Integer (0 if request successfully,
     *         'other' if line 'other' has been modified (no success))
     * @throws SQLException
     */
    public int writeRetweet(long id, int location, Date date)
            throws SQLException;

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
    public int writeLocation(String name, String parent);

    /**
     * inserts a new date into the database
     * 
     * @param date
     *            the date to write into the database as Date
     * @return database-request result as Integer (0 if request successfully,
     *         'other' if line 'other' has been modified (no success))
     */
    public int writeDay(Date date);

}
