package mysql;

import java.sql.SQLException;

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
     *         Integer-Array
     * @throws SQLException
     */
    public long[] getNonVerifiedAccounts() throws SQLException;

}
