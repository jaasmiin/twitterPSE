package mysql;

import twitter4j.User;
import mysql.result.Account;
import mysql.result.Category;
import mysql.result.Location;

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
     * @return an array of all categories.
     */
    public Category[] getCategories();

    /**
     * Get all locations from db.
     * 
     * @return an array of locations.
     */
    public Location[] getLocations();

    /**
     * Get id of account with accountName.
     * 
     * @param accountName
     *            of the twitter account.
     * @return id of the account or -1 if not found.
     */
    public int getAccountId(String accountName);

    // TODO: Probably problem with existing Result classes.
    public Account[] getData(int[] categoryIDs, int[] locationIDs,
            boolean separateDate);

    /**
     * Return list of accounts which name contains search
     * 
     * @param search
     * @return an array of accounts (Limit 50, no tweets, retweets and
     *         categories), on fault return null.
     */
    public Account[] getAccounts(String search);

    /**
     * Add a new account to db.
     * 
     * @param user
     *            TwitterUser
     * @param locationId
     *            of the TwitterUser
     * @return true if successful
     */
    public boolean addAccount(User user, int locationID);

    /**
     * Change category of an account.
     * 
     * @param accountId
     *            of an account
     * @param categoryId
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
