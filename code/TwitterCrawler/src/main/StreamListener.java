package main;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import twitter4j.FilterQuery;
import twitter4j.RateLimitStatusListener;
import twitter4j.Status;
import twitter4j.StatusListener;
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
    private void getStream(String[] track)     {

        // TODO
        // twitterStream = TwitterStreamFactory.getSingleton();
        twitterStream = new TwitterStreamFactory().getInstance();

        // get status objects
        StatusListener listener = new MyStatusListener(queue, logger);

        // filter twitter stream
        FilterQuery filter = new FilterQuery();
        // go immediately to the live stream
        filter.count(0);
        filter.track(track);

        // watch rate limits
        RateLimitStatusListener rateLimitListener = new MyRateLimitStatusListener(
                logger);

        // ConnectionLifeCycleListener??

        // set streaming details
        twitterStream.addRateLimitStatusListener(rateLimitListener);
        twitterStream.addListener(listener);
        twitterStream.filter(filter);

    }

    // /**
    // * refresh connection by disconnecting from the twitterstream and then
    // * reconnecting to the twitterstream
    // */
    // public void refresh() {
    // exit();
    // twitterStream.cleanUp();
    // twitterStream.clearListeners();
    // run();
    // }

    @Override
    public void exit() {
        twitterStream.shutdown();
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
        }
    }
}
