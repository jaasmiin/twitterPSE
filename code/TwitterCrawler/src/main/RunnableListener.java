package main;

/**
 * interface for listener that run in Threads
 * 
 * @author Holger Ebhart
 * @version 1.0
 * 
 */
public interface RunnableListener extends Runnable {

    /**
     * start collecting data from the twitter stream api
     */
    public void run();

    /**
     * shuts the twitter stream down
     */
    public void exit();

}
