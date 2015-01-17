package main;

import twitter4j.ConnectionLifeCycleListener;

/**
 * class to get notice of connection or disconnection to the twitter-stream-api
 * 
 * @author Holger Ebhart
 * @version 1.0
 */
public class MyConnectionLifeCycleListener implements
        ConnectionLifeCycleListener {

    private boolean connected;

    /**
     * create a new connection-lifecycle-listener
     */
    public MyConnectionLifeCycleListener() {
        connected = false;
    }

    @Override
    public void onCleanUp() {
        connected = false;
    }

    @Override
    public void onConnect() {
        connected = true;

    }

    @Override
    public void onDisconnect() {
        connected = false;
    }

    @Override
    public String toString() {
        return connected ? "connected" : "disconnected";
    }

}
