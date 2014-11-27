package main;

import java.util.concurrent.ConcurrentLinkedQueue;

import twitter4j.FilterQuery;
import twitter4j.Status;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

/**
 * StreamListener collects data from the twitter stream, but does not compute
 * them
 * 
 * @author Holger Ebhart
 * @version 1.0
 * 
 */
public class StreamListener implements Runnable {

    private ConcurrentLinkedQueue<Status> queue;
    protected boolean stop = false;

    /**
     * 
     * initialize an object to collect data from the twitter stream api
     * 
     * @param queue
     *            the queue for communication between producer thread and
     *            consumer thread
     */
    public StreamListener(ConcurrentLinkedQueue<Status> queue) {
        this.queue = queue;
    }

    /**
     * start collecting data from the twitter stream api
     */
    public void run() {

        try {

            String tracker[] = {"a", "b", "c", "d", "e", "f", "g", "h", "i",
                    "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u",
                    "v", "w", "x", "y", "z", "#", "@" };
            getStream(tracker);

        } catch (TwitterException e) {
            e.printStackTrace();
        }

    }

    /**
     * 
     * starting the twitter stream
     * 
     * @param track
     *            keywords to track
     * @throws IllegalStateException
     * @throws TwitterException
     */
    private void getStream(String[] track) throws IllegalStateException,

    TwitterException {

        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();

        // get status objects
        StatusListener listener = new MyStatusListener(twitterStream, queue);

        // filter twitter stream
        FilterQuery filter = new FilterQuery();
        // go immediately to the live stream
        filter.count(0);
        filter.track(track);

        // watch rate limits
        // RateLimitStatusListener rateLimitListener = new
        // MyRateLimitStatusListener();
        // ConnectionLifeCycleListener??

        // set streaming details
        twitterStream.addListener(listener);
        twitterStream.filter(filter);
        // twitterStream.addRateLimitStatusListener(rateLimitListener);
        if (stop) {
            twitterStream.shutdown();
        }
    }
}
