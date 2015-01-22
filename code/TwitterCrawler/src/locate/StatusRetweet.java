package locate;

import java.util.Date;

/**
 * class stores the data to write a retweet into a database
 * 
 * @author Holger Ebhart
 * @version 1.0
 * 
 */
public class StatusRetweet {

    private long id;
    private Date date;
    private String location;
    private String timeZone;

    /**
     * @param id
     *            the id of the account where the tweet was from as long
     * @param date
     *            the date of the retweet as Date
     * @param location
     *            the location of the account, wherefrom the retweet was as
     *            String
     * @param timeZone
     *            the timeZone of the user who created the retweet as String
     **/
    public StatusRetweet(long id, Date date, String location, String timeZone) {
        super();
        this.id = id;
        this.date = date;
        this.location = location;
        this.timeZone = timeZone;
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

}
