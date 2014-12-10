package mysql;

import result.ResultAccount;

/**
 * interface to read data from a database
 * 
 * @author Holger Ebhart
 * @version 1.0
 */
public interface DBICategorizer {



    /**
     * returns the non categorized accounts from the database (max. 100)
     * 
     * @return the non categorized accounts from the database as Result
     */
    public ResultAccount[] getNonCategorized();

}
