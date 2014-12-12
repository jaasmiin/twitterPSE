package mysql;

import mysql.result.ResultAccount;

/**
 * interface to the database for the categorizer
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
