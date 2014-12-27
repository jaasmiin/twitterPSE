package mysql.result;

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
    private int[] categoryIds;
    private Tweets[] tweets;
    private Retweets[] retweets;

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
     * @param categoryIds
     *            the category-id's of the account as int[]
     * @param tweets
     *            the tweets of the account as ResultTweet[]
     * @param retweets
     *            the retweets of the account as ResultRetweet[]
     */
    public Account(int id, long twitterId, String name, String url,
            int follower, int locationId, int[] categoryIds, Tweets[] tweets,
            Retweets[] retweets) {
        super(id);
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
        this(id, twitterId, name, url, follower, locationId, categories,
                new Tweets[0], new Retweets[0]);
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
     */
    public Account(int id, long twitterId, String name, String url,
            int follower, int locationId) {
        this(id, twitterId, name, url, follower, locationId, new int[0],
                new Tweets[0], new Retweets[0]);
    }

    /**
     * create a new object to store a account-data
     * 
     * @param id
     *            the id of the account in the database as int
     * @param url
     *            the official url of the account as String
     */
    public Account(int id, String url) {
        this(id, 0, null, url, 0, -1);
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
     * @return the tweets of the account as ResultTweet[]
     */
    public Tweets[] getTweets() {
        return tweets;
    }

    /**
     * returns the retweets of the account
     * 
     * @return the retweets of the account as ResultRetweet[]
     */
    public Retweets[] getRetweets() {
        return retweets;
    }

}
