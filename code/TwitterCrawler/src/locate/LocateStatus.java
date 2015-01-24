package locate;

import java.util.Date;

import twitter4j.Status;

/**
 * class stores the data to write a retweet/account into a database
 * 
 * @author Holger Ebhart
 * @version 1.0
 * 
 */
public class LocateStatus {

    private long id;
    private Date date;
    private String location;
    private String timeZone;
    private Status status;
    private boolean tweet;
    private boolean accountLocated;

    /**
     * creates a new storage-object to add an account/retweet to the database
     * 
     * @param id
     *            the twitter-id of the account where the tweet was from as long
     * @param date
     *            the date of the retweet as Date
     * @param location
     *            the location of the account, wherefrom the retweet was as
     *            String
     * @param timeZone
     *            the timeZone of the user who created the retweet as String
     * @param status
     *            the status that contains the user/account to add
     * @param tweet
     *            false if this account was extracted from a
     *            retweet-status-object, else true
     * @param accountLocated
     **/
    public LocateStatus(long id, Date date, String location, String timeZone,
            Status status, boolean tweet, boolean accountLocated) {
        super();
        this.id = id;
        this.date = date;
        this.location = location;
        this.timeZone = timeZone;
        this.status = status;
        this.tweet = tweet;
        this.accountLocated = accountLocated;
    }

    /**
     * returns the id of the account where the tweet was from
     * 
     * @return the id of the account where the tweet was from as long
     */
    public long getId() {
        return id;
    }

    /**
     * returns the date of the retweet
     * 
     * @return the date of the retweet as Date
     */
    public Date getDate() {
        return date;
    }

    /**
     * returns the location of the account, wherefrom the retweet was
     * 
     * @return the location of the account, wherefrom the retweet was as String
     */
    public String getLocation() {
        return location;
    }

    /**
     * returns the timeZone of the user who created the retweet
     * 
     * @return the timeZone of the user who created the retweet as String
     */
    public String getTimeZone() {
        return timeZone;
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

    /**
     * returns weather the account is located or not
     * 
     * @return true if the account has been located yet
     */
    public boolean isAccountLocated() {
        return accountLocated;
    }

}
