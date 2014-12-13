package mysql;

import java.util.List;

import mysql.result.Account;
import mysql.result.Category;

/**
 * interface to the database for the categorizer
 * 
 * @author Holger Ebhart
 * @version 1.0
 */
public interface DBICategorizer {

    /**
     * returns the non categorized accounts from the database (max. 100)
     * IMPORTANT only id and url are set
     * 
     * @return the non categorized accounts from the database as List of Account
     */
    public List<Account> getNonCategorized();

    /**
     * inserts first the category in the database and then an entry to connect
     * the account with the category
     * 
     * @param accountId
     * @param category
     * @return the result of the sql-query as boolean
     */
    public boolean addCategoryToAccount(int accountId, Category category);

}
