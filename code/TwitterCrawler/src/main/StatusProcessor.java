package main;

import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import mysql.AccessData;
import mysql.DBcrawler;
import twitter4j.Status;
import twitter4j.User;

/**
 * class to handle/process twitter status objects and to put them into a
 * database
 * 
 * @author Holger Ebhart
 * @version 1.0
 * 
 */
public class StatusProcessor implements Runnable {

    protected boolean run = true;
    private DBcrawler t;
    private ConcurrentLinkedQueue<Status> queue;
    private Logger logger;
    // use ConcurrentHashMap<Long,Object> as HashSet<Long>
    private ConcurrentHashMap<Long, Object> accounts;

    /**
     * initialize class to handle status object from the twitter stream api
     * 
     * @param queue
     *            the queue for communication between producer thread and
     *            consumer thread
     * @param accountsToTrack
     *            the twitter-account-id's of the non verified accounts that
     *            should be tracked as ConcurrentMap<Long, Object>
     * @param logger
     *            a global logger for the whole program as Logger
     * @param accessData
     *            the access data for the root user of the database as String
     */
    public StatusProcessor(ConcurrentLinkedQueue<Status> queue,
            ConcurrentHashMap<Long, Object> accountsToTrack, Logger logger,
            AccessData accessData) {
        this.queue = queue;
        this.logger = logger;
        this.accounts = accountsToTrack;
        try {
            t = new DBcrawler(accessData, logger);
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            logger.warning(e.getMessage() + "\n");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            logger.warning(e.getMessage() + "\n");
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            logger.warning(e.getMessage() + "\n");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * method to write twitter status objects to a mySQL database
     */
    public void run() {

        try {
            t.connect();
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        Status status;
        while (run) {

            while (!queue.isEmpty()) {
                status = null;
                try {
                    status = queue.remove();
                } catch (Exception e) {
                    status = null;
                }

                if (status != null) {
                    statusToDB(status);
                }

            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                logger.warning(e.getMessage() + "\n");
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        t.disconnect();

    }

    /**
     * decides which data will be written into the database and then writes it
     * 
     * @param status
     *            the status-object with the twitter data as twitter4j.Status
     */
    private void statusToDB(Status status) {

        if (status != null) {

            if (checkUser(status.getUser())) {
                accountToMySQL(status.getUser(), status.getCreatedAt(), true);
            }

            if (status.isRetweet()) {

                Status retweet = status;
                Status tweet = status.getRetweetedStatus();

                while (tweet.isRetweet()) {
                    if (checkUser(tweet.getUser())) {
                        // addAccount
                        accountToMySQL(tweet.getUser(), tweet.getCreatedAt(),
                                false);
                    }
                    retweet = tweet;
                    tweet = tweet.getRetweetedStatus();
                }

                if (checkUser(tweet.getUser())) {
                    // add Account
                    accountToMySQL(tweet.getUser(), tweet.getCreatedAt(), false);

                    // addRetweet
                    retweetToMySQL(tweet.getUser().getId(),
                            retweet.getGeoLocation() + " - "
                                    + retweet.getUser().getLocation(),
                            retweet.getCreatedAt());
                }

            }
        }
    }

    /**
     * checks weather the user is verified or in the accounts HashSet
     * 
     * @param user
     *            the user to check as twitter4j.User
     * @return true if the user should be added to the database, else false
     */
    private boolean checkUser(User user) {
        if (user.isVerified()) {
            return true;
        } else if (accounts.containsKey(user.getId())) {
            return true;
        }
        return false;
    }

    /**
     * insert an account into the database
     * 
     * @param user
     *            the user to write into the database
     * @param tweetDate
     *            the date when the tweet has been created as Date
     * @param tweet
     *            true if a tweet status object has been read, false if a
     *            retweets status object has been read
     */
    private void accountToMySQL(User user, Date tweetDate, boolean tweet) {
        // !!! parent location !!!
        t.addAccount(user.getName(), user.getId(), user.isVerified(),
                user.getFollowersCount(), user.getLocation(), user.getURL(),
                tweetDate, tweet);
    }

    /**
     * insert a retweet into the database
     * 
     * @param id
     *            the id of the account where the tweet was from
     * @param location
     *            the location of the account,wherefrom the retweet was
     * @param date
     *            the date of the retweet as Date
     */
    private void retweetToMySQL(long id, String location, Date date) {
        try {
            t.writeRetweet(id, 1, date);
        } catch (SQLException e) {
            logger.warning("Error by adding a retweet.\nSQL-Status: "
                    + e.getSQLState() + "\nMessage: " + e.getMessage() + "\n");
        }

    }
}
