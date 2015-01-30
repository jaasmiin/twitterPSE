package main;

import java.util.HashSet;
import java.util.List;

import twitter4j.FilterQuery;
import twitter4j.Location;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Trends;
import twitter4j.Trend;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

public class TestClass implements Runnable {

    private Twitter twitter;
    private HashSet<String> accounts;
    private int num;
    private static boolean onlyLocation = false;
    private static boolean onlyVerified = false;
    private static boolean onlyRetweets = false;
    private static boolean calcLocation = false;
    private int count = 0;

    /**
     * initialize testclass to try the twitter api
     * 
     * @param num
     *            - 1 for the serch api - 2 for the streaming api
     */
    public TestClass(int num) {
        accounts = new HashSet<String>();
        // connecting to twitter
        // creating object to work with
        twitter = TwitterFactory.getSingleton();
        this.num = num;
        count = 0;
    }

    public void run() {

        try {
            switch (num) {
            case 1:

               
                getVerifiedAccounts();
                break;
            case 2:

                String track[] = {"a", "b", "c", "d", "e", "f", "g", "h", "i",
                        "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
                        "u", "v", "w", "x", "y", "z", "#", "@" };
                getStream(track);
                break;
            default:
            }

        } catch (TwitterException e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("unused")
    private void getTimelines() throws TwitterException {
        // getting own timeline
        List<Status> statuses = twitter.getHomeTimeline();
        System.out
                .println("Home Timeline (KIT-PSE) with the 20 latest tweets:");
        // print own timeline
        for (Status status : statuses) {
            System.out.println(status.getUser().getName() + ":"
                    + status.getText());
        }

        System.out.println("RedBull Timeline:");
        statuses = twitter.getUserTimeline("RedBull");
        for (Status status : statuses) {
            System.out.println(status.getUser().getName() + ":"
                    + status.getText());
        }
    }

    @SuppressWarnings("unused")
    private void getTweetsByHashtag(String hashtag) throws TwitterException {
        // getting 100 tweets with hashtag
        // and the geo position
        System.out.println("100 tweets with #" + hashtag + ":");
        Query query = new Query(hashtag);
        query.count(100);
        QueryResult result = twitter.search(query);
        for (Status status : result.getTweets()) {
            printStatus(status);
        }
    }

    private void getVerifiedAccounts() throws TwitterException {

        System.out.println("Verified users:");
        ResponseList<Location> trendLoc = twitter.getAvailableTrends();
        for (Location l : trendLoc) {
            Trends trend = twitter.getPlaceTrends(l.getWoeid());
            Trend t[] = trend.getTrends();
            for (Trend i : t) {
                crawler(i.getName());
            }
        }

    }

    private void crawler(String checker) throws TwitterException {

        Query query = new Query(checker);
        query.count(1000);
        QueryResult result = twitter.search(query);
        for (Status status : result.getTweets()) {

            collectVerifiedAccounts(status);
        }

    }

    private void getStream(String[] track) throws IllegalStateException,

    TwitterException {
        StatusListener listener = new StatusListener() {
            public void onStatus(Status status) {
                // collectVerifiedAccounts(status);
                try {
                    printStatus(status);
                } catch (TwitterException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (status.isRetweeted()) {
                    printRetweets(status.getContributors());
                }
            }

            public void onException(Exception ex) {
                ex.printStackTrace();
            }

            @Override
            public void onScrubGeo(long arg0, long arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onStallWarning(StallWarning arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTrackLimitationNotice(int arg0) {
                System.out.println("ERROR: Tracking limit reached!!!");

            }
        };
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(listener);
        if (onlyRetweets) {
            twitterStream.retweet();
        }
        FilterQuery filter = new FilterQuery();
        filter.track(track);
        twitterStream.filter(filter);
    }

    private void printStatus(Status status) throws TwitterException {

        if (onlyVerified) {
            if (!status.getUser().isVerified()) {
                return;
            }
        }

        if (!onlyLocation) {
            if (status.isRetweet()) {
                System.out.println("RETWEET");
            }
            System.out.println("Location of the user: "
                    + status.getUser().getLocation());
            System.out.println("User-ID: " + status.getUser().getId());
            System.out.println("User-name: " + status.getUser().getName());
            System.out.println("User display name: "
                    + status.getUser().getScreenName()
                    + ((status.getUser().isVerified() == true) ? " verified"
                            : ""));
            System.out.println("Date: " + status.getCreatedAt());
            System.out.println("Tweet: " + status.getText());
            System.out.println("Geolocation of the tweet: "
                    + status.getGeoLocation());
            System.out.println("Language: " + status.getLang());
        }

        if (calcLocation || onlyLocation) {
            String loc = status.getUser().getLocation();
            if (loc != null && loc.length() > 0) {
                System.out.println("Location of the user: " + loc);
                try {
                    String c = getCountry(status);
                    if (c != null) {
                        System.out.println("Calculated user country: " + c);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out
                        .println("--------------------------------------------");
            } else if (calcLocation) {
                System.out
                        .println("--------------------------------------------");
            }
        } else {
            System.out.println("--------------------------------------------");
        }
        
        // posibility to get retweets over the twitter search api
        // if (!status.isRetweet()) {
        // printRetweets(status.getId());
        // }
    }

    private void printRetweets(long[] ids) {
        System.out.println("ID's of retweeters:");
        for (int i = 0; i < ids.length; i++) {
            System.out.println(ids[i]);
        }
        System.out.println("--------------------------------------------");
    }

    private void collectVerifiedAccounts(Status status) {
        if (status.getUser().isVerified()) {
            String user = status.getUser().getScreenName();
            accounts.add(user);
            System.out.println(user);
        }
    }

    /**
     * prints the retweets of a tweet
     * 
     * @param statusId
     *            - the id of the tweet status
     * @throws TwitterException
     */
    private void printRetweets(long statusId) throws TwitterException {
        List<Status> list = twitter.getRetweets(statusId);
        for (int i = 0; i < list.size(); i++) {
            System.out.println("##### This retweet was found over the twitter search api! #####");
            printStatus(list.get(i));
        }
    }

    private String getCountry(Status status) throws Exception {
        return TestGeoNames.getCountryId(status.getUser().getLocation());
    }
}
