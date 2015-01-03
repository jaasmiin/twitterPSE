package main;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

/**
 * listener for the twitter streaming api
 * 
 * @author Holger Ebhart
 * @version 1.0
 * 
 */
public class MyStatusListener implements StatusListener {

    private ConcurrentLinkedQueue<Status> queue;
    private Logger logger;
    private boolean connected;

    /**
     * initialize a new listener to listen to the twitter streaming api
     * 
     * @param queue
     *            the queue for the status-objects to store as
     *            ConcurrentLinkedQueue<Status>
     * @param logger
     *            a global logger for the whole program as Logger
     */
    public MyStatusListener(ConcurrentLinkedQueue<Status> queue, Logger logger) {
        this.queue = queue;
        this.logger = logger;
        connected = true;

    }

    @Override
    public void onException(Exception arg0) {
        logger.warning(arg0.getMessage() + "\n");
        connected = false;
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice arg0) {
    }

    @Override
    public void onScrubGeo(long arg0, long arg1) {
    }

    @Override
    public void onStallWarning(StallWarning arg0) {
        logger.warning(arg0.getCode() + "\n" + arg0.getMessage() + "\n");

        if (arg0.getPercentFull() >= 50) {
            // TODO reconnect
        }
    }

    @Override
    public void onStatus(Status status) {
        // TODO check if it works better
        if (status.isRetweet() || status.getUser().isVerified()) {
            queue.add(status);
        }
    }

    @Override
    public void onTrackLimitationNotice(int arg0) {
        // logger.info("Track limitation notice: " + arg0);
        // sends number of transmitted statusobjects
    }

    @Override
    public String toString() {
        return connected ? "connected" : "maybe crashed";
    }

}
