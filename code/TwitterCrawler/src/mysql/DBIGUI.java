package mysql;

import mysql.result.Account;
import mysql.result.Category;
import mysql.result.Location;
import mysql.result.Retweets;

/**
 * interface for modifying some data from a database (with restrictions)
 * 
 * @author Holger Ebhart
 * @version 1.0
 * 
 */
public interface DBIgui {

	/**
	 * Get all categories from db.
	 * @return an array of all categories.
	 */
    public Category[] getCategories();

    /**
     * Get all locations from db.
     * @return an array of locations.
     */
    public Location[] getLocations();

    /**
     * Get id of account with accountName.
     * @param accountName of the twitter account. 
     * @return id of the account or -1 if not found.
     */
    public Integer getAccountId(String accountName);
    
    /**
     * Get
     * @param categoryIds
     * @param countryIds
     * @return
     */
    // TODO: Probably problem with existing Result classes.
    public Retweets[] getData(int[] categoryIds, int[] countryIds);

    /**
     * Return list of accounts which name contains search
     * @param search
     * @return an array of accounts.
     */
    public Account[] getAccounts(String search);

    // TODO: not ready
    public Boolean addAccount();

    /**
     * Change category of an account.
     * @param accountId of an account
     * @param categoryId of a category
     * @return true on success
     */
    public Boolean setCategory(Integer accountId, Integer categoryId);

    /**
     * Add or remove a location from an account.
     * @param accountId of the account
     * @param locationId of the location
     * @param active if true add category to account, otherwise remove category from account
     * @return
     */
    public Boolean setLocation(Integer accountId, Integer locationId, Boolean active);

}
