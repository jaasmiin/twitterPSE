package mysql.result;

import java.util.ArrayList;
import java.util.List;

/**
 * class provides the possibility to store an amount of tweets and retweets into
 * one Object
 * 
 * @author Holger Ebhart
 * 
 */
public class TweetsAndRetweets {

    /**
     * a list of tweets
     */
    private List<Tweets> tweets;
    /**
     * a list of retweets
     */
    private List<Retweets> retweets;

    /**
     * initializes a new TweetsAndRetweets Object
     */
    public TweetsAndRetweets() {
        tweets = new ArrayList<Tweets>();
        retweets = new ArrayList<Retweets>();
    }

    /**
     * returns the stored list of tweets
     * 
     * @return the stored list of tweets as List<Tweets>
     */
    public List<Tweets> getTweets() {
        return tweets;
    }

    /**
     * returns the stored list of retweets
     * 
     * @return the stored list of retweets as List<Retweets>
     */
    public List<Retweets> getRetweets() {
        return retweets;
    }

    /**
     * set a new tweets list to store
     * 
     * @param tweets
     *            the new tweets list to store as List<Tweets>
     */
    public void setTweets(List<Tweets> tweets) {
        this.tweets = tweets;
    }

    /**
     * set a new retweets list to store
     * 
     * @param retweets
     *            the new reretweets list to store as List<Retweets>
     */
    public void setRetweets(List<Retweets> retweets) {
        this.retweets = retweets;
    }

}
