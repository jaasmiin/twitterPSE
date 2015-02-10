package mysql;

import java.util.List;

import mysql.result.Account;

/**
 * interface to the database for the categorizer
 * 
 * @author Holger Ebhart
 */
public interface DBIcategorizer {

    /**
     * returns the non categorized accounts from the database (max. 100)
     * IMPORTANT only id and url are valid
     * 
     * @return the non categorized accounts from the database as List of Account
     *         (only id and url are valid)
     */
    public List<Account> getNonCategorized();

    /**
     * returns the categoryIds where an url match was successful
     * 
     * @param url
     *            the url from twitter of the website of this account as String
     * @param name
     *            the name of the twitter account, with wildcards instead of
     *            spaced
     * @return a list of categoryIds where an url match was successful as
     *         List<Integer>
     */
    public List<Integer> getCategoriesForAccount(String url, String name);

    /**
     * inserts first the category in the database and then an entry to connect
     * the account with the category and sets categorized on true
     * 
     * @param accountId
     *            the id of the account to categorize as int
     * @param categoryId
     *            the category-id to set as int
     * @return the result of the sql-query as boolean
     */
    public boolean addCategoryToAccount(int accountId, int categoryId);

    /**
     * returns the parentId of the category specified by id
     * 
     * @param id
     *            the id of the current Category as int
     * 
     * @return the parentId of the category specified by id as int (-1 in case of error)
     */
    public int getParentId(int id);
}
