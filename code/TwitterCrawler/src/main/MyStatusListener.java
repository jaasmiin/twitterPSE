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

    }

    @Override
    public void onException(Exception arg0) {
        logger.warning(arg0.getMessage() + "\n");
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onScrubGeo(long arg0, long arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStallWarning(StallWarning arg0) {
        logger.warning(arg0.getCode() + "\n" + arg0.getMessage() + "\n");
    }

    @Override
    public void onStatus(Status status) {
        queue.add(status);
    }

    @Override
    public void onTrackLimitationNotice(int arg0) {
        //logger.info("Track limitation notice: " + arg0);
        // TODO sends number of transmitted statusobjects
    }

}
