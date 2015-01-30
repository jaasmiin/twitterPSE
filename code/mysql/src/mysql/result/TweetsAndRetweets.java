package mysql.result;

import java.util.ArrayList;
import java.util.List;

/**
 * class provides the possibility to store an amount of tweets and retweets into
 * one Object
 * 
 * @author Holger Ebhart
 * @version 1.0
 * 
 */
public class TweetsAndRetweets {

    /**
     * a list of tweets
     */
    public List<Tweets> tweets;
    /**
     * a list of retweets
     */
    public List<Retweets> retweets;

    /**
     * initializes a new TweetsAndRetweets Object
     */
    public TweetsAndRetweets() {
        tweets = new ArrayList<Tweets>();
        retweets = new ArrayList<Retweets>();
    }

}
