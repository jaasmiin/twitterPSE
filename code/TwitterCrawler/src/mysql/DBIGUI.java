package mysql;

import mysql.result.ResultAccount;
import mysql.result.ResultCategory;
import mysql.result.ResultLocation;

/**
 * interface for modifying some data from a database (with restrictions)
 * 
 * @author Holger Ebhart
 * @version 1.0
 * 
 */
public interface DBIGUI {

    public ResultCategory[] getCategories();

    public ResultCategory[] getCategories(String search);

    public ResultLocation[] getLocations();

    public ResultLocation[] getLocations(String search);

    public int getAccountId(String accountName);

    public ResultAccount[] getData(int[] categoryIds, int countryIds);

    public ResultAccount[] getAccounts();

    public ResultAccount[] getAccounts(String search);

    public boolean addAccount();

    public boolean setCategory(int accountId, int categoryId);

    public boolean setLocation(int accountId, int locationId);

}
