package locate;

import java.sql.SQLException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import main.RunnableListener;
import mysql.AccessData;
import mysql.DBcrawler;

/**
 * 
 * @author
 * @version 1.0
 * 
 */
public class WebServiceLocator implements RunnableListener {

    private Logger logger;
    private ConcurrentLinkedQueue<StatusAccount> locateAccountQueue;
    private ConcurrentLinkedQueue<StatusRetweet> locateRetweetQueue;
    private DBcrawler dbc;
    private boolean run;
    private boolean locateAccounts;

    public WebServiceLocator(AccessData accessData, Logger logger,
            ConcurrentLinkedQueue<StatusAccount> locateAccountQueue,
            ConcurrentLinkedQueue<StatusRetweet> locateRetweetQueue,
            boolean locateAccounts) throws InstantiationException {
        run = true;
        this.logger = logger;
        this.locateAccountQueue = locateAccountQueue;
        this.locateRetweetQueue = locateRetweetQueue;
        this.locateAccounts = locateAccounts;
        try {
            dbc = new DBcrawler(accessData, logger);
        } catch (IllegalAccessException | ClassNotFoundException | SQLException e) {
            dbc = null;
            logger.severe(e.getMessage() + "\n");
            throw new InstantiationException(
                    "Not able to instantiate Databaseconnection.");
        }
    }

    @Override
    public void run() {

        if (dbc == null) {
            logger.severe("A WebServiceLocator couldn't been started: No database connection!");
            return;
        }

        try {
            dbc.connect();
        } catch (SQLException e) {
            logger.severe(e.getMessage() + "\n");
            return;
        }

        while (run) {

            try {
                Thread.sleep(50); // sleep for 0.05s
            } catch (InterruptedException e) {
                // logger.info("StatusProcessor interrupted\n" +
                // e.getMessage());
            }

            if (locateAccounts) {
                while (!locateAccountQueue.isEmpty()) {

                    StatusAccount account = null;
                    try {
                        account = locateAccountQueue.poll();
                    } catch (Exception e) {
                        account = null;
                    }

                    if (account != null) {
                        locateAccount(account);
                    }
                }
            } else {
                while (!locateRetweetQueue.isEmpty()) {

                    StatusRetweet retweet = null;
                    try {
                        retweet = locateRetweetQueue.poll();
                    } catch (Exception e) {
                        retweet = null;
                    }

                    if (retweet != null) {
                        locateRetweet(retweet);
                    }
                }
            }
        }

        dbc.disconnect();

    }

    private void locateRetweet(StatusRetweet retweet) {

        String location = "0";

        // TODO

        dbc.addRetweet(retweet.getId(), location, retweet.getDate());
    }

    private void locateAccount(StatusAccount account) {

        String location = "0";

        // TODO

        dbc.addAccount(account.getStatus().getUser(), location, account
                .getStatus().getCreatedAt(), account.isTweet());

    }

    @Override
    public void exit() {
        run = false;

    }

}
