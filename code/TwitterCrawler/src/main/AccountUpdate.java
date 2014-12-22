package main;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import mysql.AccessData;
import mysql.DBcrawler;

/**
 * class to get the non verified accounts that should be tracked
 * 
 * @author Holger Ebhart
 * @version 1.0
 */
public class AccountUpdate implements RunnableListener {

    private boolean run = true;
    private ConcurrentHashMap<Long, Object> accounts;
    private HashSet<Long> myAccounts;
    private Logger logger;
    private DBcrawler reader;

    /**
     * initializing accountUpdate with right database and Hashtable
     * 
     * @param logger
     *            a global logger for the whole program as Logger
     * @param accounts
     *            the hashtable where to write the non verified accounts as
     *            ConcurrentHashMap<Long, Object>
     * @param accessData
     *            the access data for the root user of the database as String
     * @throws SQLException
     *             thrown if a connection to the database isn't possible
     */
    public AccountUpdate(Logger logger,
            ConcurrentHashMap<Long, Object> accounts, AccessData accessData)
            throws SQLException {
        this.accounts = accounts;
        this.logger = logger;
        myAccounts = new HashSet<Long>();
        try {
            reader = new DBcrawler(accessData, logger);
        } catch (InstantiationException | IllegalAccessException
                | ClassNotFoundException e) {
            this.logger.warning(e.getMessage() + "\n");
            throw new SQLException("Could not connect to database.");
        }
    }

    @Override
    public void run() {

        // refresh hashset every hour
        while (run) {

            // connect to database
            try {
                reader.connect();
            } catch (SQLException e) {
                logger.severe("Could not connect to database.\n"
                        + e.getMessage());
                return;
            }

            // refresh hashtable
            long[] list = reader.getNonVerifiedAccounts();
            if (list != null) {
                for (int i = 0; i < list.length; i++) {
                    if (!myAccounts.contains(list[i])) {
                        myAccounts.add(list[i]);
                        accounts.put(list[i], new Object());
                    }
                }
            }

            // disconnect from database
            reader.disconnect();

            try {
                Thread.sleep(3600000); // sleep for 1 hour
            } catch (InterruptedException e) {
                logger.info("AccountUpdate has been interrupted\n"
                        + e.getMessage());
            }
        }
    }

    @Override
    public void exit() {
        run = false;
    }

}
