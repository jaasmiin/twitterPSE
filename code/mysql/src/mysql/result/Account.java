package mysql.result;

import java.util.ArrayList;
import java.util.List;

/**
 * class to transmit and store the results from the database
 * 
 * @author Holger Ebhart
 * @version 1.0
 */
public class Account extends Result {

    private String name;
    private String url;
    private long twitterId;
    private int follower;
    private int locationId;
    private boolean verified;
    private int[] categoryIds;
    private List<Tweets> tweets;
    private List<Retweets> retweets;

    /**
     * create a new object to store a account-data
     * 
     * @param id
     *            the id of the account in the database as int
     * @param twitterId
     *            the id of the twitter account as long
     * @param name
     *            the name of the account as String
     * @param verified
     * @param url
     *            the official url of the account as String
     * @param follower
     *            the number of followers of the account as int
     * @param locationId
     *            the location-id of the account as int
     * @param categoryIds
     *            the category-id's of the account as int[]
     * @param tweets
     *            the tweets of the account as List<Tweets>
     * @param retweets
     *            the retweets of the account as List<Retweets>
     */
    public Account(int id, long twitterId, String name, boolean verified,
            String url, int follower, int locationId, int[] categoryIds,
            List<Tweets> tweets, List<Retweets> retweets) {
        super(id);
        this.verified = verified;
        this.twitterId = twitterId;
        this.name = name;
        this.url = url;
        this.follower = follower;
        this.locationId = locationId;
        this.categoryIds = categoryIds;
        this.tweets = tweets;
        this.retweets = retweets;
    }

    /**
     * create a new object to store a account-data
     * 
     * @param id
     *            the id of the account in the database as int
     * @param twitterId
     *            the id of the twitter account as long
     * @param name
     *            the name of the account as String
     * @param url
     *            the official url of the account as String
     * @param follower
     *            the number of followers of the account as int
     * @param locationId
     *            the location-id of the account as int
     * @param categories
     *            the category-id's of the account as int[]
     */
    public Account(int id, long twitterId, String name, String url,
            int follower, int locationId, int[] categories) {
        this(id, twitterId, name, false, url, follower, locationId, categories,
                new ArrayList<Tweets>(), new ArrayList<Retweets>());
    }

    /**
     * create a new object to store a account-data
     * 
     * @param id
     *            the id of the account in the database as int
     * @param twitterId
     *            the id of the twitter account as long
     * @param name
     *            the name of the account as String
     * @param verified
     * @param url
     *            the official url of the account as String
     * @param follower
     *            the number of followers of the account as int
     * @param locationId
     *            the location-id of the account as int
     */
    public Account(int id, long twitterId, String name, boolean verified,
            String url, int follower, int locationId) {
        this(id, twitterId, name, verified, url, follower, locationId,
                new int[0], new ArrayList<Tweets>(), new ArrayList<Retweets>());
    }

    /**
     * create a new object to store a account-data
     * 
     * @param id
     *            the id of the account in the database as int
     * @param name
     *            the name of the account as String
     * @param tweets
     *            a sum of tweets of the account as Tweets
     */
    public Account(int id, String name, Tweets tweets) {
        this(id, 0, name, false, null, 0, 0, new int[0],
                new ArrayList<Tweets>(), new ArrayList<Retweets>());
        addTweet(tweets);
    }

    /**
     * create a new object to store a account-data
     * 
     * @param id
     *            the id of the account in the database as int
     * @param name
     *            the name of the account as String
     * @param retweets
     *            a sum of retweets of the account as Retweets
     */
    public Account(int id, String name, Retweets retweets) {
        this(id, 0, name, false, null, 0, 0, new int[0],
                new ArrayList<Tweets>(), new ArrayList<Retweets>());
        addRetweet(retweets);
    }

    /**
     * create a new object to store a account-data
     * 
     * @param id
     *            the id of the account in the database as int
     * @param name
     * 			  the name of the account
     * @param url
     *            the official url of the account as String
     */
    public Account(int id, String name, String url) {
        this(id, 0, name, false, url, 0, -1);
    }

    /**
     * returns the name of the account
     * 
     * @return the name of the account as String
     */
    public String getName() {
        return name;
    }

    /**
     * returns the url of the account
     * 
     * @return the url of the account as String
     */
    public String getUrl() {
        return url;
    }

    /**
     * returns the id of the twitter account
     * 
     * @return the name of the twitter account as long
     */
    public long getTwitterId() {
        return twitterId;
    }

    /**
     * returns the number of followers of the account
     * 
     * @return the number of followers of the account as int
     */
    public int getFollower() {
        return follower;
    }

    /**
     * returns the location-id of the account
     * 
     * @return the location-id of the account as int
     */
    public int getLocationId() {
        return locationId;
    }

    /**
     * returns the category-id's of the account
     * 
     * @return the category-id's of the account as int[]
     */
    public int[] getCategoryIds() {
        return categoryIds;
    }

    /**
     * returns the tweets of the account
     * 
     * @return the tweets of the account as List<Tweets>
     */
    public List<Tweets> getTweets() {
        return tweets;
    }

    /**
     * returns the retweets of the account
     * 
     * @return the retweets of the account as List<Retweet>
     */
    public List<Retweets> getRetweets() {
        return retweets;
    }

    /**
     * returns if the account is verified or not
     * 
     * @return true if the account is verified, else false
     */
    public boolean isVerified() {
        return verified;
    }

    /**
     * set the retweets array from this account
     * 
     * @param retweets
     *            the new array with the actual retweets as Retweets[]
     */
    public void setRetweets(List<Retweets> retweets) {
        this.retweets = retweets;
    }

    /**
     * adds a Tweets-Object to the tweets-list of this account
     * 
     * @param tweet
     *            the sum of tweets to add as Tweets
     */
    public void addTweet(Tweets tweet) {
        tweets.add(tweet);
    }

    /**
     * adds a Retweets-Object to the retweets-list of this account
     * 
     * @param retweet
     *            the sum of retweets to add as Retweets
     */
    public void addRetweet(Retweets retweet) {
        retweets.add(retweet);
    }

    @Override
    public String toString() {
    	return getName();
    }

    @Override
    public boolean equals(Object o) {
    	boolean equal = false;
		if (o != null && o.getClass() == this.getClass()) {
			equal = ((Result) o).getId() == getId();
		} 
    	return equal;
    }
}
