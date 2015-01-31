package main;

import java.util.logging.Logger;

import twitter4j.RateLimitStatusEvent;
import twitter4j.RateLimitStatusListener;

/**
 * class to watch the rate limits of the current twitter stream
 * 
 * @author Holger Ebhart
 * @version 1.0
 * @deprecated not necessary to listen to rate limits
 */
public class MyRateLimitStatusListener implements RateLimitStatusListener {

    private Logger logger;

    /**
     * create a new RateLimitStatusListener
     * 
     * @param logger
     *            a logger to log exceptions, informations, ... as Logger
     */
    public MyRateLimitStatusListener(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void onRateLimitReached(RateLimitStatusEvent arg0) {
        logger.warning("Rate limit status: " + arg0.getRateLimitStatus()
                + "\n Account RateLimit: " + arg0.isAccountRateLimitStatus()
                + "\n IP RateLimit: " + arg0.isIPRateLimitStatus()
                + "\n Message: " + arg0.toString());

    }

    @Override
    public void onRateLimitStatus(RateLimitStatusEvent arg0) {
        // TODO add info to logger

    }

}
