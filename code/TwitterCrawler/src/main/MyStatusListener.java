package main;

import java.util.concurrent.ConcurrentLinkedQueue;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;

/**
 * listener for the twitter streaming api
 * 
 * @author Holger Ebhart
 * @version 1.0
 * 
 */
public class MyStatusListener implements StatusListener {

    // private TwitterStream stream;
    private ConcurrentLinkedQueue<Status> queue;

    /**
     * initialize a new listener to listen to the twitter streaming api
     * 
     * @param stream
     *            the stream to follow as TwitterStream
     * @param queue
     *            the queue for the status-objects to store as
     *            ConcurrentLinkedQueue<Status>
     */
    public MyStatusListener(TwitterStream stream,
            ConcurrentLinkedQueue<Status> queue) {
        // this.stream = stream;
        this.queue = queue;
    }

    @Override
    public void onException(Exception arg0) {
        // TODO Auto-generated method stub

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
        System.out.println(arg0);

    }

    @Override
    public void onStatus(Status status) {
        queue.add(status);
    }

    @Override
    public void onTrackLimitationNotice(int arg0) {
        // System.out.println("ERROR: Tracking limit reached!!!");

    }

}
