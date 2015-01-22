package locate;

import twitter4j.Status;

/**
 * class stores the data to write an account into a database
 * 
 * @author Holger Ebhart
 * @version 1.0
 * 
 */
public class StatusAccount {

    private Status status;
    private boolean tweet;

    /**
     * creates a new storage-object to add an account to the database
     * 
     * @param status
     *            the status that contains the user/account to add
     * @param tweet
     *            false if this account was extracted from a
     *            retweet-status-object, else true
     */
    public StatusAccount(Status status, boolean tweet) {
        super();
        this.status = status;
        this.tweet = tweet;
    }

    /**
     * returns the stored status-object that contains the user to add
     * 
     * @return the stored status-object that contains the user to add as Status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * returns true if this status-object was extracted from a tweet, false if
     * it was extracted from a retweet
     * 
     * @return true if this status-object was extracted from a tweet, false if
     *         it was extracted from a retweet
     */
    public boolean isTweet() {
        return tweet;
    }

}
