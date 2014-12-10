package result;

import java.util.Date;

/**
 * store the retweets of a day
 * 
 * @author Holger Ebhart
 * @version 1.0
 */
public class ResultRetweet extends ResultTweet {

    private int counterNonLocalized;

    /**
     * store the retweets of a day
     * 
     * @param date
     *            the date of the retweets as Date
     * @param counter
     *            the number of localized retweets as int
     * @param counterNonLocalized
     *            the number of NON localized retweets as int
     */
    public ResultRetweet(Date date, int counter, int counterNonLocalized) {
        super(date, counter);
        this.counterNonLocalized = counterNonLocalized;
    }

    /**
     * returns the number of NON localized retweets
     * 
     * @return the number of NON localized retweets as int
     */
    public int getCounterNonLocalized() {
        return counterNonLocalized;
    }

}
