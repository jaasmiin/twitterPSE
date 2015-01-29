package mysql;

import java.sql.SQLException;
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
 * @version 1.1
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
     * Get id of account with accountName.
     * 
     * @param accountName
     *            of the twitter account.
     * @return id of the account or -1 if not found.
     */
    public int getAccountId(String accountName);

    /**
     * returns the sum of all retweets for each location
     * 
     * @return the sum of all retweets for each location (the location as
     *         locationCode in the hashmap as key) as HashMap<String,Integer>
     */
    public HashMap<String, Integer> getAllRetweetsPerLocation();

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
    public List<Account> getAllData(Integer[] categoryIDs,
            Integer[] locationIDs, Integer[] accountIDs, boolean byDates)
            throws IllegalArgumentException, SQLException;

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
    public TweetsAndRetweets getSumOfData(Integer[] categoryIDs,
            Integer[] locationIDs, Integer[] accountIDs, boolean byDates)
            throws IllegalArgumentException, SQLException;


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

}
