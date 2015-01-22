package main;

import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import locate.Locator;
import locate.StatusAccount;
import locate.StatusRetweet;
import mysql.AccessData;
import mysql.DBcrawler;
import twitter4j.GeoLocation;
import twitter4j.Place;
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
public class StatusProcessor implements RunnableListener {

    private boolean run = true;
    private DBcrawler dbc;
    private ConcurrentLinkedQueue<Status> queue;
    private Logger logger;
    // use ConcurrentHashMap<Long,Object> as HashSet<Long>
    private ConcurrentHashMap<Long, Object> nonVerAccounts;
    private Locator locate;
    private int count;

    private ConcurrentLinkedQueue<StatusAccount> locateAccountQueue;
    private ConcurrentLinkedQueue<StatusRetweet> locateRetweetQueue;

    /**
     * initialize class to handle status object from the twitter stream api
     * 
     * @param queue
     *            the queue for communication between producer thread and
     *            consumer thread
     * @param locateAccountQueue
     * @param locateRetweetQueue
     * @param accountsToTrack
     *            the twitter-account-id's of the non verified accounts that
     *            should be tracked as ConcurrentMap<Long, Object>
     * @param logger
     *            a global logger for the whole program as Logger
     * @param accessData
     *            the access data for the root user of the database as String
     * @throws InstantiationException
     *             thrown if it is not possible to connect to the database
     */
    public StatusProcessor(ConcurrentLinkedQueue<Status> queue,
            ConcurrentLinkedQueue<StatusAccount> locateAccountQueue,
            ConcurrentLinkedQueue<StatusRetweet> locateRetweetQueue,
            ConcurrentHashMap<Long, Object> accountsToTrack, Logger logger,
            AccessData accessData) throws InstantiationException {
        this.queue = queue;
        this.locateAccountQueue = locateAccountQueue;
        this.locateRetweetQueue = locateRetweetQueue;
        this.logger = logger;
        this.nonVerAccounts = accountsToTrack;
        locate = new Locator(this.logger);
        try {
            dbc = new DBcrawler(accessData, logger);
        } catch (IllegalAccessException | ClassNotFoundException | SQLException e) {
            dbc = null;
            logger.severe(e.getMessage() + "\n");
            throw new InstantiationException(
                    "Not able to instantiate Databaseconnection.");
        }
        count = 0;
    }

    /**
     * method to write twitter status objects to a mySQL database
     */
    public void run() {

        if (dbc == null) {
            logger.severe("A StatusProcessor couldn't been started: No database connection!");
            return;
        }

        try {
            dbc.connect();
        } catch (SQLException e) {
            logger.severe(e.getMessage() + "\n");
            return;
        }

        Status status;
        while (run) {

            try {
                Thread.sleep(50); // sleep for 0.05s
            } catch (InterruptedException e) {
                // logger.info("StatusProcessor interrupted\n" +
                // e.getMessage());
            }

            while (!queue.isEmpty()) {

                status = null;
                try {
                    status = queue.poll();
                } catch (Exception e) {
                    status = null;
                }

                if (status != null) {
                    statusToDB(status);
                }
            }
        }

        dbc.disconnect();

    }

    /**
     * decides which data will be written into the database and then writes it
     * 
     * @param status
     *            the status-object with the twitter data as twitter4j.Status
     */
    private void statusToDB(Status status) {

        boolean added = false;

        if (checkUser(status.getUser())) {
            accountToDB(status, true);
            added = true;
        }

        if (status.isRetweet()) {

            Status retweet = status;
            Status tweet = status.getRetweetedStatus();

            while (tweet.isRetweet()) {
                if (checkUser(tweet.getUser())) {
                    // addAccount
                    accountToDB(tweet, false);
                    added = true;
                }
                retweet = tweet;
                tweet = tweet.getRetweetedStatus();
            }

            if (checkUser(tweet.getUser())) {
                // add Account
                accountToDB(tweet, false);

                // addRetweet
                retweetToDB(tweet.getUser().getId(), retweet.getGeoLocation(),
                        retweet.getUser().getLocation(), retweet.getPlace(),
                        retweet.getCreatedAt(), retweet.getUser().getTimeZone());
                added = true;
            }
        }
        if (added) {
            count++;
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
        } else if (nonVerAccounts.containsKey(user.getId())) {
            return true;
        }
        return false;
    }

    /**
     * insert an account into the database
     * 
     * @param tweet
     *            the status-object of the tweet, that contains the account to
     *            add as user as Status
     * @param isTweet
     *            true if a tweet status object has been read, false if a
     *            retweet status object has been read
     */
    private void accountToDB(Status tweet, boolean isTweet) {

        if (!dbc.containsAccount(tweet.getUser().getId())) {
            String loc = locate.locate(tweet.getPlace(),
                    tweet.getGeoLocation(), tweet.getUser().getLocation());
            if (loc != "0") {
                dbc.addAccount(tweet.getUser(), loc, tweet.getCreatedAt(),
                        isTweet);
            } else {
                locateAccountQueue.add(new StatusAccount(tweet, isTweet));
            }
        }
        // dbc.addAccount(tweet.getUser(), tweet.getPlace(),
        // tweet.getGeoLocation(), tweet.getCreatedAt(), isTweet);

    }

    /**
     * insert a retweet into the database
     * 
     * @param id
     *            the id of the account where the tweet was from as long
     * @param geotag
     *            the Geolocation of the retweet as GeoLocation (null if not
     *            available)
     * @param location
     *            the location of the account, wherefrom the retweet was as
     *            String
     * @param place
     *            if available the place where the retweet has been created as
     *            Place (else null)
     * @param date
     *            the date of the retweet as Date
     * @param timeZone
     *            the timeZone of the user who created the retweet as String
     */
    private void retweetToDB(long id, GeoLocation geotag, String location,
            Place place, Date date, String timeZone) {

        String loc = locate.locate(place, geotag, location);
        if (loc != "0") {
            dbc.addRetweet(id, loc, date);
        } else {
            locateRetweetQueue.add(new StatusRetweet(id, date, location,
                    timeZone));
        }

        // String loc = locate.locate(place, geotag, location, timeZone);
        //
        // dbc.addRetweet(id, loc, date);

    }

    /**
     * returns the numbers for statistic
     * 
     * @return the numbers for statistic as int[]
     */
    public int[] getCounter() {
        int[] temp = locate.getStatistic();
        int[] ret = new int[11];
        for (int i = 0; i < temp.length; i++) {
            ret[i] = temp[i];
        }
        ret[10] = count;
        return ret;
    }

    @Override
    public void exit() {
        run = false;
    }

}
