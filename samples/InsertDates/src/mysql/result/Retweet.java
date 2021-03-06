package mysql.result;

import java.util.Date;

/**
 * store the retweets of a day
 * 
 * @author Holger Ebhart
 * @version 1.0
 */
public class Retweet extends Tweet {

    private int counterNonLocalized;
    private Location location;

    /**
     * store the retweets of a day
     * 
     * @param date
     *            the date of the retweets as Date
     * @param counter
     *            the number of localized retweets as int
     * @param counterNonLocalized
     *            the number of NON localized retweets as int
     * @param location
     *            the location of this retweets as ResultLocation
     */
    public Retweet(Date date, int counter, int counterNonLocalized,
            Location location) {
        super(date, counter);
        this.counterNonLocalized = counterNonLocalized;
        this.location = location;
    }

    /**
     * returns the number of NON localized retweets
     * 
     * @return the number of NON localized retweets as int
     */
    public int getCounterNonLocalized() {
        return counterNonLocalized;
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
