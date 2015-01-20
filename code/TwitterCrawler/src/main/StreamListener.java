package main;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import twitter4j.ConnectionLifeCycleListener;
import twitter4j.FilterQuery;
import twitter4j.Status;
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
public class StreamListener implements RunnableListener {

    private ConcurrentLinkedQueue<Status> queue;
    private Logger logger;
    private TwitterStream twitterStream;
    private MyStatusListener listener;
    private ConnectionLifeCycleListener clcl;

    /**
     * 
     * initialize an object to collect data from the twitter stream api
     * 
     * @param queue
     *            the queue for communication between producer thread and
     *            consumer thread
     * @param logger
     *            a global logger for the whole program as Logger
     */
    public StreamListener(ConcurrentLinkedQueue<Status> queue, Logger logger) {
        this.queue = queue;
        this.logger = logger;
    }

    @Override
    public void run() {

        String tracker[] = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
                "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
                "w", "x", "y", "z", "#", "@" };
        getStream(tracker);

    }

    /**
     * 
     * starting the twitter stream
     * 
     * @param track
     *            keywords to track
     */
    private void getStream(String[] track) {

        twitterStream = new TwitterStreamFactory().getInstance();

        // get status objects
        listener = new MyStatusListener(queue, logger);

        // filter twitter stream
        FilterQuery filter = new FilterQuery();
        // go immediately to the live stream
        filter.count(0);
        filter.track(track);

        // watch rate limits
        // RateLimitStatusListener rateLimitListener = new
        // MyRateLimitStatusListener(
        // logger);

        clcl = new MyConnectionLifeCycleListener();

        // set streaming details
        // twitterStream.addRateLimitStatusListener(rateLimitListener);
        twitterStream.addConnectionLifeCycleListener(clcl);
        twitterStream.addListener(listener);
        twitterStream.filter(filter);

    }

    @Override
    public String toString() {
        return clcl.toString();
    }

    @Override
    public void exit() {
        twitterStream.cleanUp();
        twitterStream.shutdown();
    }

    /**
     * returns the sum over all status objects received from twitter
     * 
     * @return the sum over all status objects received from twitter as int
     */
    public int getCounter() {
        return listener.getCounter();
    }
}
