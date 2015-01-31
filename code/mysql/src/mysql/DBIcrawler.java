package mysql;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import twitter4j.User;

/**
 * interface for writing data into a database
 * 
 * @author Holger Ebhart
 * @version 1.0
 */
public interface DBIcrawler {

    /**
     * insert an account into the database
     * 
     * @param user
     *            the user to add as User
     * @param location
     *            the location of the account as String
     * @param date
     *            the date of the tweet as Date
     * @param tweet
     *            true if a tweet status object has been read, false if a
     *            retweets status object has been read
     * @return boolean-array with three results of the database requests. First
     *         is the result of adding the location, second is result for adding
     *         the account and third is for adding the tweet.
     * 
     */
    public boolean[] addAccount(User user, String location, Date date,
            boolean tweet);

    /**
     * inserts a retweet into the database, if it's still in the database the
     * counter will be increased
     * 
     * @param id
     *            the id of the account who's tweet was retweeted as long
     * @param location
     *            the location of the retweet as String (null if could not been
     *            localized)
     * @param date
     *            the day when the retweet has been written as Date
     * @return database-request result as Boolean[]
     */
    public boolean[] addRetweet(long id, String location, Date date);

    /**
     * inserts a new date into the database
     * 
     * @param date
     *            the date to write into the database as Date
     * @return database-request result as Boolean
     */
    public boolean addDay(Date date);

    /**
     * returns all AccountId's that aren't verified
     * 
     * @return all AccountId's from the database that aren't verified as
     *         Integer-Array, null if an error occurred
     */
    public long[] getNonVerifiedAccounts();

    /**
     * returns all AccountId's from the database
     * 
     * @return all AccountId's from the database as HashSet<Long>, empty if an
     *         error occurred
     */
    public HashSet<Long> getAccounts();

    /**
     * returns a threadsafe hashmap that maps words from the database to
     * location-codes
     * 
     * @return a threadsafe hashmap that maps words from the database to
     *         location-codes as HashMap<String, String>
     */
    public HashMap<String, String> getLocationStrings();

    /**
     * inserts a location-code - word combination to the database
     * 
     * @param code
     *            the location-code as String (max. length 3)
     * @param word
     *            the word to connect with the location-code as String (max.
     *            length 250)
     * @param timeZone
     *            the timezone of the location as String (max. length 200)
     * @return true if data has been insert into the database, else false
     */
    public boolean addLocationString(String code, String word, String timeZone);

    /**
     * returns if an account with this id is even in the database
     * 
     * @param id
     *            the id to check as long
     * @return true if an account with this id is in the database, else false
     */
    public boolean containsAccount(long id);

}
