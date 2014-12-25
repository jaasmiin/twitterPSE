package mysql.result;

import java.util.Date;

/**
 * store the retweets of a day
 * 
 * @author Holger Ebhart
 * @version 1.0
 */
public class Retweets extends Tweets {

    private Location location;

    /**
     * store the retweets of a day
     * 
     * @param date
     *            the date of the retweets as Date
     * @param counter
     *            the number of localized retweets as int
     * @param location
     *            the location of this retweets as ResultLocation
     */
    public Retweets(Date date, int counter, Location location) {
        super(date, counter);
        this.location = location;
    }

    /**
     * returns the location of this retweets
     * 
     * @return the location of this retweets as ResultLocation
     */
    public Location getLocation() {
        return location;
    }

}
