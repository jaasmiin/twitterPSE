package main;

import java.sql.SQLException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import mysql.AccessData;
import mysql.DBWrite;
import twitter4j.FilterQuery;
import twitter4j.RateLimitStatusListener;
import twitter4j.Status;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

/**
 * AccountListener collects data about specified accounts from the twitter
 * stream, and computes them
 * 
 * @author Holger Ebhart
 * @version 1.0
 * 
 */
public class AccountListener implements Runnable {

    private static String PW = "";
    private ConcurrentLinkedQueue<Status> queue;
    private Logger logger;
    private TwitterStream twitterStream;

    /**
     * 
     * initialize an object to collect data from the twitter stream api
     * 
     * @param logger
     *            a global logger for the whole program as Logger
     */
    public AccountListener(Logger logger) {
        this.logger = logger;
        queue = new ConcurrentLinkedQueue<Status>();
    }

    /**
     * start collecting data from the twitter stream api
     */
    public void run() {

        // TODO starting refresh routine for new Accounts

        // TODO process status-objects from queue

        try {

            DBWrite db = new DBWrite(new AccessData("localhost",
                    "3306", "twitter", "root", PW), logger);
            db.connect();
            long[] accounts = db.getNonVerified();
            db.disconnect();
            getStream(accounts);

        } catch (TwitterException e) {
            logger.warning("ErrorCode: " + e.getExceptionCode() + "\nMessage: "
                    + e.getErrorMessage() + "\n");
            e.printStackTrace();
        } catch (SQLException e) {
            logger.warning("SQL-Status: " + e.getSQLState() + "\nMessage: "
                    + e.getMessage() + "\n");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * 
     * starting the twitter stream
     * 
     * @param track
     *            keywords to track
     * @throws IllegalStateException
     * @throws TwitterException
     */
    private void getStream(long[] track) throws IllegalStateException,

    TwitterException {

        twitterStream = new TwitterStreamFactory().getInstance();

        // get status objects
        StatusListener listener = new MyStatusListener(queue, logger);

        // filter twitter stream
        FilterQuery filter = new FilterQuery();
        // go immediately to the live stream
        filter.count(0);
        filter.follow(track);

        // watch rate limits
        RateLimitStatusListener rateLimitListener = new MyRateLimitStatusListener(
                logger);

        // ConnectionLifeCycleListener??

        // set streaming details
        twitterStream.addListener(listener);
        twitterStream.filter(filter);
        twitterStream.addRateLimitStatusListener(rateLimitListener);

    }

    /**
     * shuts the twitter stream down
     */
    public void exit() {
        twitterStream.shutdown();
    }
}
