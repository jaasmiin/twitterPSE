package mysql;

import java.sql.SQLException;
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
 * @version 1.0
 * 
 */
public interface DBIgui {

    /**
     * Get all categories from db.
     * 
     * @return a list of all categories.
     */
    public List<Category> getCategories();

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
     * returns all accounts that match a category- and a location-ID, with the
     * associated total-number of Tweets and Retweets
     * 
     * @param categoryIDs
     *            the ids of the selected categorys as int[]
     * @param locationIDs
     *            the ids of the selected locations as int[]
     * @param accountIDs
     *            the ids of the additional accounts as int[]
     * @return all accounts that match a category- and a location-ID, with the
     *         associated total-number of Tweets and Retweets as List<Account>
     */
    public List<Account> getAllData(int[] categoryIDs, int[] locationIDs,
            int[] accountIDs) throws IllegalArgumentException, SQLException;

    /**
     * returns the total number of tweets and retweets from all the accounts
     * that match a category- and a location-ID per location
     * 
     * @param categoryIDs
     *            the ids of the selected categorys as int[]
     * @param locationIDs
     *            the ids of the selected locations as int[]
     * @param accountIDs
     *            the ids of the additional accounts as int[]
     * @return the total number of tweets and retweets from all the accounts
     *         that match a category- and a location-ID per location as
     *         TweetsAndRetweets
     */
    public TweetsAndRetweets getSumOfData(int[] categoryIDs, int[] locationIDs,
            int[] accountIDs) throws IllegalArgumentException, SQLException;

    /**
     * returns all accounts that match a category- and a location-ID, with the
     * associated number of Tweets and Retweets per Day
     * 
     * @param categoryIDs
     *            the ids of the selected categorys as int[]
     * @param locationIDs
     *            the ids of the selected locations as int[]
     * @param accountIDs
     *            the ids of the additional accounts as int[]
     * @return all accounts that match a category- and a location-ID, with the
     *         associated number of Tweets and Retweets per Day as List<Account>
     */
    public List<Account> getAllDataWithDates(int[] categoryIDs,
            int[] locationIDs, int[] accountIDs)
            throws IllegalArgumentException, SQLException;

    /**
     * returns the total number of tweets and retweets from all the accounts
     * that match a category- and a location-ID per location per day
     * 
     * @param categoryIDs
     *            the ids of the selected categorys as int[]
     * @param locationIDs
     *            the ids of the selected locations as int[]
     * @param accountIDs
     *            the ids of the additional accounts as int[]
     * @return the total number of tweets and retweets from all the accounts
     *         that match a category- and a location-ID per location per Day as
     *         TweetsAndRetweets
     * @throws IllegalArgumentException
     */
    public TweetsAndRetweets getSumOfDataWithDates(int[] categoryIDs,
            int[] locationIDs, int[] accountIDs)
            throws IllegalArgumentException, SQLException;

    /**
     * Return list of accounts which name contains search
     * 
     * @param search
     * @return a list of accounts (Limit 50, no tweets, retweets and
     *         categories), on fault return null.
     */
    public List<Account> getAccounts(String search);

    /**
     * Add a new account to db.
     * 
     * @param user
     *            TwitterUser
     * @param locationID
     *            of the TwitterUser
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
     * @param active
     *            if true add category to account, otherwise remove category
     *            from account
     * @return true on success
     */
    public boolean setLocation(int accountId, int locationId, boolean active);

}
