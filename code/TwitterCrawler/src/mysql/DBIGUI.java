package mysql;

import mysql.result.Account;
import mysql.result.Category;
import mysql.result.Location;

/**
 * interface for modifying some data from a database (with restrictions)
 * 
 * @author Holger Ebhart
 * @version 1.0
 * 
 */
public interface DBIgui {

    public Category[] getCategories();

    //public Category[] getCategories(String search);

    public Location[] getLocations();

    //public Location[] getLocations(String search);

    // return -1 on fault
    public int getAccountId(String accountName);

    public Account[] getData(int[] categoryIds, int[] countryIds);

    // public Account[] getAccounts();

    public Account[] getAccounts(String search);

    public boolean addAccount();

    public boolean setCategory(int accountId, int categoryId);

    public boolean setLocation(int accountId, int locationId);

}
