package main;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import mysql.AccessData;
import mysql.DBRead;

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
    private DBRead reader;

    /**
     * initializing accountupdate with right database and Hashtable
     * 
     * @param logger
     *            a global logger for the whole program as Logger
     * @param accounts
     *            the hashtable where to write the non verified accounts as
     *            ConcurrentHashMap<Long, Object>
     * @param accessData
     *            the access data for the root user of the database as String
     */
    public AccountUpdate(Logger logger,
            ConcurrentHashMap<Long, Object> accounts, AccessData accessData) {
        this.accounts = accounts;
        this.logger = logger;
        myAccounts = new HashSet<Long>();
        try {
            reader = new DBRead(accessData, logger);
        } catch (InstantiationException | IllegalAccessException
                | ClassNotFoundException e) {
            this.logger.warning(e.getMessage() + "\n");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        try {
            reader.connect();
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        while (run) {
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

            try {
                Thread.sleep(1000); // sleep 1s
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                // e.printStackTrace();
            }
        }
        reader.disconnect();
    }

    @Override
    public void exit() {
        run = false;
    }

}
