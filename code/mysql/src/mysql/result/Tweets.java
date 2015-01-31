package mysql.result;

import java.util.Date;

/**
 * store the tweets of a day
 * 
 * @author Holger Ebhart
 * @version 1.0
 * 
 */
public class Tweets {

    private Date date;
    private int counter;

    /**
     * store the tweets of a day
     * 
     * @param date
     *            the date of the tweets as Date
     * @param counter
     *            the number of localized tweets as int
     */
    public Tweets(Date date, int counter) {
        this.date = date;
        this.counter = counter;
    }

    /**
     * returns the date of the tweets
     * 
     * @return the date of the tweets as Date
     */
    public Date getDate() {
        return date;
    }

    /**
     * returns the number of tweets
     * 
     * @return the number of tweets as int
     */
    public int getCounter() {
        return counter;
    }

}
