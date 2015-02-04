package main;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import locate.Locator;
import locate.LocateStatus;
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
 * 
 */
public class StatusProcessor implements RunnableListener {

    private static final String DEFAULT_LOCATION = "0";
    private boolean run = true;
    private DBcrawler dbc;
    private ConcurrentLinkedQueue<Status> queue;
    private ConcurrentLinkedQueue<LocateStatus> locateQueue;
    private Logger logger;
    // use ConcurrentHashMap<Long,Object> as HashSet<Long>
    private ConcurrentHashMap<Long, Object> nonVerAccounts;
    private Locator locator;
    private int count;

    /**
     * initialize class to handle status object from the twitter stream api
     * 
     * @param queue
     *            the queue for communication between producer thread and
     *            consumer thread
     * @param locateQueue
     *            the queue where status-objects who have to be located via the
     *            webservice where buffered as
     *            ConcurrentLinkedQueue<LocateStatus>
     * @param accountsToTrack
     *            the twitter-account-id's of the non verified accounts that
     *            should be tracked as ConcurrentMap<Long, Object>
     * @param locationHash
     *            a hashmap that maps words to country-codes as HashMap<String,
     *            String>
     * @param logger
     *            a global logger for the whole program as Logger
     * @param accessData
     *            the access data for the root user of the database as String
     * @throws InstantiationException
     *             thrown if it is not possible to connect to the database
     */
    public StatusProcessor(ConcurrentLinkedQueue<Status> queue,
            ConcurrentLinkedQueue<LocateStatus> locateQueue,
            ConcurrentHashMap<Long, Object> accountsToTrack,
            HashMap<String, String> locationHash, Logger logger,
            AccessData accessData) throws InstantiationException {

        this.queue = queue;
        this.locateQueue = locateQueue;
        this.logger = logger;
        this.nonVerAccounts = accountsToTrack;
        locator = new Locator(locationHash, this.logger);

        // load drivers to cennect to database
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

        // check weather a connection to the database has been established or
        // not
        if (dbc == null) {
            logger.severe("A StatusProcessor couldn't been started: No database connection!");
            return;
        }

        // open new connection to database
        try {
            dbc.connect();
        } catch (SQLException e) {
            logger.severe(e.getMessage() + "\n");
            return;
        }

        Status status;
        // work till the program will be shut down
        while (run) {

            try {
                Thread.sleep(50); // sleep for 0.05s
            } catch (InterruptedException e) {
                // logger.info("StatusProcessor interrupted\n" +
                // e.getMessage());
            }

            // work until the queue is empty
            while (!queue.isEmpty()) {

                // try to enqueue an element to work on it
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

        if (status.isRetweet()) {

            // go through all retweets to top tweet
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

            // locate and add the first retweet on the tweet, if the user of the
            // tweet is verified or should be tracked
            if (checkUser(tweet.getUser())) {

                // add Retweet and Account
                retweetToDB(tweet, retweet.getGeoLocation(), retweet.getUser()
                        .getLocation(), retweet.getPlace(),
                        retweet.getCreatedAt(), retweet.getUser().getTimeZone());
                added = true;
            }

        } else if (checkUser(status.getUser())) {
            // locate and insert account
            accountToDB(status, true);
            added = true;
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

        // validate if user is verified or if it should be tracked (hashset
        // lookup)
        if (user.isVerified()) {
            return true;
        } else if (nonVerAccounts.containsKey(user.getId())) {
            return true;
        }
        return false;
    }

    /**
     * insert an account into the database or into the locate queue
     * 
     * @param tweet
     *            the status-object of the tweet, that contains the account to
     *            add as user as Status
     * @param isTweet
     *            true if a tweet status object has been read, false if a
     *            retweet status object has been read
     */
    private void accountToDB(Status tweet, boolean isTweet) {

        // add account only if it's not yet in the database
        if (!dbc.containsAccount(tweet.getUser().getId())) {

            // try to locate account wothout webservice
            String loc = locator.locate(tweet.getPlace(),
                    tweet.getGeoLocation(), tweet.getUser().getLocation(),
                    tweet.getUser().getTimeZone());

            // check if localization was successful
            if (loc.equals(DEFAULT_LOCATION)) {
                // account has to been localized by the webservice
                locateQueue.add(new LocateStatus(-1, null, null, null, tweet,
                        isTweet, false, false));
            } else {
                // insert account into database
                dbc.addAccount(tweet.getUser(), loc, tweet.getCreatedAt(),
                        isTweet);
            }

        }

    }

    /**
     * insert a retweet into the database or in the locate queue
     * 
     * @param tweet
     *            the status-object of the tweet
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
    private void retweetToDB(Status tweet, GeoLocation geotag, String location,
            Place place, Date date, String timeZone) {

        // try to locate the retweet without the webservice
        String loc = locator.locate(place, geotag, location, timeZone);

        if (loc.equals(DEFAULT_LOCATION)) {

            // retweet has to be localized by the webservice

            // check account
            boolean isLocated = false;
            // check ig the account is yet in the database
            if (dbc.containsAccount(tweet.getUser().getId())) {
                isLocated = true;
            } else {

                // try to locate account
                String temp = locator.locate(tweet.getPlace(),
                        tweet.getGeoLocation(), tweet.getUser().getLocation(),
                        tweet.getUser().getTimeZone());

                if (!temp.equals(DEFAULT_LOCATION)) {

                    // account is located, so write him into the database
                    dbc.addAccount(tweet.getUser(), temp, tweet.getCreatedAt(),
                            false);
                    isLocated = true;
                }
            }

            // write the retweet and the account into the queue and tell weather
            // the account is in the database yet or not
            locateQueue.add(new LocateStatus(tweet.getUser().getId(), date,
                    location, timeZone, tweet, false, isLocated, false));

        } else {

            // retweet is located, so we only have to look for the account

            // try to locate account
            if (dbc.containsAccount(tweet.getUser().getId())) {

                // account is in database, so write retweet in database
                dbc.addRetweet(tweet.getUser().getId(), loc, date);

            } else {

                // try to locate the account without the webservice
                String temp = locator.locate(tweet.getPlace(),
                        tweet.getGeoLocation(), tweet.getUser().getLocation(),
                        tweet.getUser().getTimeZone());

                if (!temp.equals(DEFAULT_LOCATION)) {

                    // account has been located, so write account and retweet
                    // into database
                    dbc.addAccount(tweet.getUser(), temp, tweet.getCreatedAt(),
                            false);
                    dbc.addRetweet(tweet.getUser().getId(), loc, date);

                } else {

                    // write retweet and account into queue to locate, but set
                    // flag that the retweet is yet located
                    locateQueue.add(new LocateStatus(tweet.getUser().getId(),
                            date, loc, timeZone, tweet, false, false, true));
                }
            }

        }

    }

    /**
     * returns the numbers for statistic
     * 
     * @return the numbers for statistic as int[]
     */
    public int[] getCounter() {

        // get locate-statistic from locator
        int[] temp = locator.getStatistic();
        // create new array to return
        int[] ret = new int[temp.length + 1];
        // copy the statistic-values from the locator
        for (int i = 0; i < temp.length; i++) {
            ret[i] = temp[i];
        }
        // append own value about interesting status-objects
        ret[temp.length] = count;
        return ret;
    }

    @Override
    public void exit() {
        run = false;
    }

}
