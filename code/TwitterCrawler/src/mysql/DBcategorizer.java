package mysql;

import java.util.logging.Logger;

import result.ResultAccount;

/**
 * class to address a database with read-only Access
 * 
 * @author Holger Ebhart
 * @version 1.0
 * 
 */
public class DBcategorizer extends DBConnection implements DBICategorizer {

    /**
     * configurate the connection to the database
     * 
     * @param accessData
     *            the access data to the specified mysql-database as AccessData
     * @param logger
     *            a global logger for the whole program as Logger
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public DBcategorizer(AccessData accessData, Logger logger)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        super(accessData, logger);
    }



    @Override
    public ResultAccount[] getNonCategorized() {
        // TODO Auto-generated method stub
        return null;
    }

}
