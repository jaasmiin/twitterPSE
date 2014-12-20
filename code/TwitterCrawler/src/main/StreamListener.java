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
    private boolean onlyRetweets;

    /**
     * 
     * initialize an object to collect data from the twitter stream api
     * 
     * @param queue
     *            the queue for communication between producer thread and
     *            consumer thread
     * @param logger
     *            a global logger for the whole program as Logger
     * @param onlyRetweets
     *            true if only retweets should be tracked, else false
     */
    public StreamListener(ConcurrentLinkedQueue<Status> queue, Logger logger,
            boolean onlyRetweets) {
        this.queue = queue;
        this.logger = logger;
        this.onlyRetweets = onlyRetweets;
    }

    @Override
    public void run() {

        // try {

        String tracker[] = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
                "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
                "w", "x", "y", "z", "#", "@" };
        getStream(tracker);

        // } catch (TwitterException e) {
        // logger.warning("ErrorCode: " + e.getExceptionCode() + "\nMessage: "
        // + e.getErrorMessage() + "\n");
        // e.printStackTrace();
        // }

    }

    /**
     * 
     * starting the twitter stream
     * 
     * @param track
     *            keywords to track
     */
    private void getStream(String[] track) // throws IllegalStateException,
                                           // TwitterException
    {

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

        // listening to all retweets
        if (onlyRetweets) {
            twitterStream.retweet();
        }

    }

    /**
     * refresh connection by disconnecting from the twitterstream and then
     * reconnecting to the twitterstream
     */
    public void refresh() {
        exit();
        run();
    }

    @Override
    public void exit() {
        twitterStream.shutdown();
    }
}
