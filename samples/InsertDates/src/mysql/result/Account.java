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
    private String location;
    private Category[] categorys;
    private Tweet[] tweets;
    private Retweet[] retweets;

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
     * @param location
     *            the location of the account as String
     * @param categorys
     *            the categories of the account as ResultCategory[]
     * @param tweets
     *            the tweets of the account as ResultTweet[]
     * @param retweets
     *            the retweets of the account as ResultRetweet[]
     */
    public Account(int id, long twitterId, String name, String url,
            int follower, String location, Category[] categorys,
            Tweet[] tweets, Retweet[] retweets) {
        super(id);
        this.twitterId = twitterId;
        this.name = name;
        this.url = url;
        this.follower = follower;
        this.location = location;
        this.categorys = categorys;
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
     * @param location
     *            the location of the account as String
     * @param categorys
     *            the categories of the account as ResultCategory[]
     */
    public Account(int id, long twitterId, String name, String url,
            int follower, String location, Category[] categorys) {
        this(id, twitterId, name, url, follower, location, categorys,
                new Tweet[0], new Retweet[0]);
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
     * @param location
     *            the location of the account as String
     */
    public Account(int id, long twitterId, String name, String url,
            int follower, String location) {
        this(id, twitterId, name, url, follower, location,
                new Category[0], new Tweet[0], new Retweet[0]);
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
        this(id, 0, null, url, 0, null);
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
     * returns the location of the account
     * 
     * @return the location of the account as String
     */
    public String getLocation() {
        return location;
    }

    /**
     * returns the categories of the account
     * 
     * @return the categories of the account as ResultCategory[]
     */
    public Category[] getCategorys() {
        return categorys;
    }

    /**
     * returns the tweets of the account
     * 
     * @return the tweets of the account as ResultTweet[]
     */
    public Tweet[] getTweets() {
        return tweets;
    }

    /**
     * returns the retweets of the account
     * 
     * @return the retweets of the account as ResultRetweet[]
     */
    public Retweet[] getRetweets() {
        return retweets;
    }

}
