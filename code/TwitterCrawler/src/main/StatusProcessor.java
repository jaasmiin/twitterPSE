package main;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import org.geonames.GeoNamesException;

import locate.Locator;
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
public class StatusProcessor implements Runnable {

    protected boolean run = true;
    private DBcrawler dbc;
    private ConcurrentLinkedQueue<Status> queue;
    private Logger logger;
    // use ConcurrentHashMap<Long,Object> as HashSet<Long>
    private ConcurrentHashMap<Long, Object> nonVerAccounts;
    private Locator locate;

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
     * @throws InstantiationException
     */
    public StatusProcessor(ConcurrentLinkedQueue<Status> queue,
            ConcurrentHashMap<Long, Object> accountsToTrack, Logger logger,
            AccessData accessData) throws InstantiationException {
        this.queue = queue;
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
                Thread.sleep(50); // sleep for 0.05s
            } catch (InterruptedException e) {
                logger.info("StatusProcessor interrupted\n" + e.getMessage());
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

        if (status != null) {

            if (checkUser(status.getUser())) {
                accountToDB(status.getUser(), status.getCreatedAt(), true);
            }

            if (status.isRetweet()) {

                Status retweet = status;
                Status tweet = status.getRetweetedStatus();

                while (tweet.isRetweet()) {
                    if (checkUser(tweet.getUser())) {
                        // addAccount
                        accountToDB(tweet.getUser(), tweet.getCreatedAt(),
                                false);
                    }
                    retweet = tweet;
                    tweet = tweet.getRetweetedStatus();
                }

                if (checkUser(tweet.getUser())) {
                    // add Account
                    accountToDB(tweet.getUser(), tweet.getCreatedAt(), false);

                    // addRetweet
                    retweetToDB(tweet.getUser().getId(),
                            retweet.getGeoLocation(), retweet.getUser()
                                    .getLocation(), retweet.getPlace(),
                            retweet.getCreatedAt(), retweet.getUser()
                                    .getTimeZone());
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
        } else if (nonVerAccounts.containsKey(user.getId())) {
            return true;
        }
        return false;
    }

    /**
     * insert an account into the database
     * 
     * @param user
     *            the user to write into the database as User
     * @param tweetDate
     *            the date when the tweet has been created as Date
     * @param tweet
     *            true if a tweet status object has been read, false if a
     *            retweet status object has been read
     */
    private void accountToDB(User user, Date tweetDate, boolean tweet) {

        // locate account
        String loc = locate.getLocation(user.getLocation(), user.getTimeZone());
        assert (loc != null);

        dbc.addAccount(user.getName(), user.getId(), user.isVerified(),
                user.getFollowersCount(), loc, user.getURL(), tweetDate, tweet);

    }

    /**
     * insert a retweet into the database
     * 
     * @param id
     *            the id of the account where the tweet was from
     * @param geotag
     *            the Geolocation of the retweet as GeoLocation (null if not
     *            available)
     * @param location
     *            the location of the account,wherefrom the retweet was as
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

        String loc = "0";
        if (place != null) {
            loc = place.getCountryCode();
        } else {
            if (geotag != null) {
                try {
                    loc = locate.getLocation(geotag);
                } catch (IOException | GeoNamesException e) {
                    loc = "0";
                }
            }
            if (loc == "0" && location != null) {
                loc = locate.getLocation(location, timeZone);
            }
        }
        assert (loc != null);

        dbc.addRetweet(id, loc, date);

    }
}
