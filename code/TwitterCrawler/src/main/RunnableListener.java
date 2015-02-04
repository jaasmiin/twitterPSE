package main;

/**
 * interface for a listener that ran in a Thread
 * 
 * @author Holger Ebhart
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
