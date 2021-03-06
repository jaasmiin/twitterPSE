package mysql;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;

import twitter4j.User;
import mysql.result.Account;
import mysql.result.Category;
import mysql.result.Location;
import mysql.result.TweetsAndRetweets;

/**
 * interface for modifying some data from a database (with restrictions)
 * 
 * @author Maximilian Awiszus, Holger Ebhart
 * 
 */
public interface DBIgui {

    /**
     * Get all categories from db.
     * 
     * @return all categories, null if an error occurred.
     */
    public Category getCategories();

    /**
     * Get all locations from db.
     * 
     * @return a list of locations.
     */
    public List<Location> getLocations();

    /**
     * returns the sum of all retweets for each location for each day
     * 
     * @return the sum of all retweets for each location per Date (the date as
     *         key in the first HashMap, the location as locationCode in the
     *         second hashmap(object) as key) as HashMap<Date, HashMap<String,
     *         Integer>>
     */
    public HashMap<Date, HashMap<String, Integer>> getAllRetweetsPerLocation();

    /**
     * returns all accounts that match a category- and a location-ID, with the
     * associated total-number of Tweets and Retweets
     * 
     * @param categoryIDs
     *            the ids of the selected categorys as Integer[]
     * @param locationIDs
     *            the ids of the selected locations as Integer[]
     * @param accountIDs
     *            the ids of the additional accounts as Integer[]
     * @param byDates
     *            false if the data should be aggregated over days, if not true
     * @return all accounts that match a category- and a location-ID, with the
     *         associated total-number of Tweets and Retweets as List<Account>
     * @throws IllegalArgumentException
     *             thrown if categoryIDs or/and LocationIDs is empty or null
     * @throws SQLException
     *             thrown if the sql-query couldn't be built
     */
    public List<Account> getAllData(List<Integer> categoryIDs,
            List<Integer> locationIDs, List<Integer> accountIDs, boolean byDates);

    /**
     * returns the total number of tweets and retweets from all the accounts
     * that match a category- and a location-ID per location
     * 
     * @param categoryIDs
     *            the ids of the selected categorys as Integer[]
     * @param locationIDs
     *            the ids of the selected locations as Integer[]
     * @param accountIDs
     *            the ids of the additional accounts as Integer[]
     * @param byDates
     *            false if the data should be aggregated over days, if not true
     * @return the total number of tweets and retweets from all the accounts
     *         that match a category- and a location-ID per location as
     *         TweetsAndRetweets
     * @throws IllegalArgumentException
     *             thrown if categoryIDs or/and LocationIDs is empty or null
     * @throws SQLException
     *             thrown if the sql-query couldn't be built
     */
    public TweetsAndRetweets getSumOfData(List<Integer> categoryIDs,
            List<Integer> locationIDs, List<Integer> accountIDs, boolean byDates);

    /**
     * Return list of accounts which name contains search
     * 
     * @param search
     *            the word to search as String
     * @return a list of accounts (Limit 50, no tweets, retweets and
     *         categories), on fault return null.
     */
    public List<Account> getAccounts(String search);

    /**
     * Add a new account to db.
     * 
     * @param user
     *            the twitter-user to add as User
     * @param locationID
     *            the locationID of the TwitterUser as int (has to reference a
     *            guilty location in the database)
     * @return true if successful
     */
    public boolean addAccount(User user, int locationID);

    /**
     * Change category of an account.
     * 
     * @param accountID
     *            of an account
     * @param categoryID
     *            of a category
     * @return true on success
     */
    public boolean setCategory(int accountID, int categoryID);

    /**
     * Add or remove a location from an account.
     * 
     * @param accountId
     *            of the account
     * @param locationId
     *            of the location
     * @return true on success
     */
    public boolean setLocation(int accountId, int locationId);

    /**
     * returns the account behind the given datbase-id
     * 
     * @param id
     *            the id of the account in the database as int
     * @return the account behind the given datbase-id as Account (null if an
     *         error occurs)
     */
    Account getAccount(int id);

}
