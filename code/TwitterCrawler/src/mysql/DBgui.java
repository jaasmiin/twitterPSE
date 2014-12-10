package mysql;

import java.util.logging.Logger;

/**
 * class to modify the database restricted
 * 
 * @author Holger Ebhart
 * @version 1.0
 * 
 */
public class DBgui extends DBConnection implements DBIGUI{

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
    public DBgui(AccessData accessData, Logger logger)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        super(accessData, logger);
    }

}
