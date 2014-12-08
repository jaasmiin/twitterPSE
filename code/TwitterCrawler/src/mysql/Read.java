package mysql;

/**
 * interface to read data from a database
 * 
 * @author Holger Ebhart
 * @version 1.0
 */
public interface Read {

    /**
     * returns all AccountId's that aren't verified
     * 
     * @return all AccountId's from the database that aren't verified as
     *         Integer-Array, null if an error occured
     */
    public long[] getNonVerifiedAccounts();

    /**
     * returns the non categorized accounts from the database (max. 100)
     * 
     * @return the non categorized accounts from the database as Result
     */
    public Result[] getNonCategorized();

}
