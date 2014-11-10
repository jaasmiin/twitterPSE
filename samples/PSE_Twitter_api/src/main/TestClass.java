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
    private boolean onlyLocation = false;
    private boolean onlyVerified = true;

    public TestClass(int num) {
        accounts = new HashSet<String>();
        this.num = num;
    }

    public void run() {

        // connecting to twitter
        // creating object to work with
        twitter = TwitterFactory.getSingleton();

        try {
            switch (num) {
            case 1:

                getVerifiedAccounts();
                break;
            case 2:

                String track[] = {"a", "b", "c", "d", "e", "f", "g", "h", "i",
                        "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
                        "u", "v", "w", "x", "y", "z" };
                // String track[] = {"news" };
                getStream(track);
                break;
            default:
            }

        } catch (TwitterException e) {
            e.printStackTrace();
        }

    }

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
                printStatus(status);

            }

            public void onDeletionNotice(
                    StatusDeletionNotice statusDeletionNotice) {
            }

            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
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
        };
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(listener);
        FilterQuery filter = new FilterQuery();
        filter.track(track);
        twitterStream.filter(filter);
    }

    private void printStatus(Status status) {

        if (onlyVerified) {
            if (!status.getUser().isVerified()) {
                return;
            }
        }

        System.out.println("Location of the user: "
                + status.getUser().getLocation());
        if (!onlyLocation) {
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
        try {
            String c = getCountry(status);
            if (c != null) {
                System.out.println("Calculated user country: " + c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("--------------------------------------------");
    }

    private void collectVerifiedAccounts(Status status) {
        if (status.getUser().isVerified()) {
            String user = status.getUser().getScreenName();
            synchronized (accounts) {
                if (!accounts.contains(user)) {
                    accounts.add(user);
                    System.out.println(user);
                }
            }
        }
    }

    private String getCountry(Status status) throws Exception {
        return TestGeoNames.getCountryId(status.getUser().getLocation());
    }
}
