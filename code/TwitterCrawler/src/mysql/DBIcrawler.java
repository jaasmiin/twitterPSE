package mysql;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import twitter4j.User;

/**
 * interface for writing data into a database
 * 
 * @author Holger Ebhart
 * @version 1.0
 */
public interface DBIcrawler {

    // * @param name
    // * the name of the account as String
    // * @param id
    // * the official twitter id of the account as long
    // * @param isVer
    // * true if the account is verified, else false
    // * @param follower
    // * the number of followers as int
    // * @param location
    // * the location of the account as String
    // * @param timeZone
    // * the time-zone of the user as String
    // * @param url
    // * the url of the verified user as String

    // * @param place
    // * if set the place of the user, else null as Place
    // * @param geotag
    // * the geolocation of the tweet from this account as GeoLocation

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
     * @throws SQLException
     */
    public boolean[] addRetweet(long id, String location, Date date)
            throws SQLException;

    // maybe private
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
    public boolean addLocation(String code, String parent);

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

    // maybe private
    /**
     * returns a hashSet of all the country-codes from the database
     * 
     * @return a hashSet of all the country-codes from the database as
     *         HashSet<String>, empty if an error occurred
     */
    public HashSet<String> getCountryCodes();

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
     *         location-codes as ConcurrentHashMap<String, String>
     */
    public ConcurrentHashMap<String, String> getLocationStrings();

    /**
     * inserts a location-code - word combination to the database
     * 
     * @param code
     *            the location-code as String (max. length 3)
     * @param word
     *            the word to connect with the location-code as String (max.
     *            length 250)
     * @return true if data has been insert into the database, else false
     */
    public boolean addLocationString(String code, String word);

    /**
     * returns if an account with this id is even in the database
     * 
     * @param id
     *            the id to check as long
     * @return true if an account with this id is in the database, else false
     */
    public boolean containsAccount(long id);

}
