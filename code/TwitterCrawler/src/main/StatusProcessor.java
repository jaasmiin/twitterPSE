package main;

import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import mysql.AccessData;
import mysql.DBConnection;
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
    private static String PW = "";
    private DBConnection t;
    private ConcurrentLinkedQueue<Status> queue;
    private Logger logger;

    /**
     * initialize class to handle status object from the twitter stream api
     * 
     * @param queue
     *            the queue for communication between producer thread and
     *            consumer thread
     * @param logger
     *            a global logger for the whole program as Logger
     */
    public StatusProcessor(ConcurrentLinkedQueue<Status> queue, Logger logger) {
        this.queue = queue;
        this.logger = logger;
        try {
            t = new DBConnection(new AccessData("localhost", "3306", "twitter",
                    "root", PW), logger);
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
                synchronized (queue) {
                    if (!queue.isEmpty()) {
                        status = queue.remove();
                    }
                }

                statusToDB(status);

            }
            try {
                Thread.sleep(5);
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
            User user = status.getUser();

            if (user.isVerified()) {
                accountToMySQL(user, status.getCreatedAt(), !status.isRetweet());
            }

            if (status.isRetweet()
                    && status.getRetweetedStatus().getUser().isVerified()) {
                accountToMySQL(status.getRetweetedStatus().getUser(), status
                        .getRetweetedStatus().getCreatedAt(), false);

                retweetToMySQL(status.getRetweetedStatus().getUser().getId(),
                        status.getGeoLocation() + " - " + user.getLocation(),
                        status.getCreatedAt());

            }
        }
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
        t.writeAccount(user.getName(), user.getId(), user.isVerified(),
                user.getFollowersCount(), user.getLocation(), "parentLocation",
                tweetDate, tweet);
    }

    /**
     * insert a retweet into the database
     * 
     * @param id
     *            the id of the account where the retweets was from
     * @param location
     *            the location of the account,wherefrom the retweet was
     * @param date
     *            the date of the retweet as Date
     */
    private void retweetToMySQL(long id, String location, Date date) {
        try {
            t.writeRetweet(id, 1, date);
        } catch (SQLException e) {
            logger.warning("SQL-Status: " + e.getSQLState() + "\nMessage: "
                    + e.getMessage() + "\n");
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
