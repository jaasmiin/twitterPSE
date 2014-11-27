package main;

import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

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

    private boolean run = true;
    private DBConnection t;
    private ConcurrentLinkedQueue<Status> queue;

    /**
     * initialize class to handle status object from the twitter stream api
     * 
     * @param queue
     *            the queue for communication between producer thread and
     *            consumer thread
     */
    public StatusProcessor(ConcurrentLinkedQueue<Status> queue) {
        this.queue = queue;
        try {
            t = new DBConnection( , , "twitter", , );
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
     * method to write twitter status objects to a mySQL database
     */
    public void run() {
        try {
            t.connect();
        } catch (SQLException e) {
            t.disconnect();
            e.printStackTrace();
            return;
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
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        // Thread.yield();
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
                accountToMySQL(user, status.getCreatedAt());
            }

            if (status.isRetweet()
                    && status.getRetweetedStatus().getUser().isVerified()) {
                accountToMySQL(status.getRetweetedStatus().getUser(), status
                        .getRetweetedStatus().getCreatedAt());

                retweetToMySQL(status.getRetweetedStatus().getUser().getId(),
                        status.getGeoLocation() + " - " + user.getLocation(),
                        status.getCreatedAt());

            }

        }
    }

    /**
     * write a user into the database
     * 
     * @param user
     *            the user to write into the database
     */
    private void accountToMySQL(User user, Date tweetDate) {
        try {
            t.writeAccount(user.getName(), user.getId(), user.isVerified(),
                    user.getFollowersCount(), user.getLocation(), tweetDate);
        } catch (SQLException e) {

            System.out.println(e.getErrorCode());
            e.printStackTrace();

        }

    }

    /**
     * write a retweet into the database
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
