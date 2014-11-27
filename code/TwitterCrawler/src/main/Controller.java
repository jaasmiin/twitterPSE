package main;

import java.util.concurrent.ConcurrentLinkedQueue;

import twitter4j.Status;

/**
 * controller to control the other threads and start dynamically more
 * worker-threads, restarts also the crawler if it has been shut down
 * 
 * @author Holger Ebhart
 * @version 1.0
 * 
 */
public class Controller implements Runnable {

    private StreamListener listener;
    private ConcurrentLinkedQueue<Status> queue;
    private Thread crawler;

    /**
     * initialize a controller instance that coordinates the crawler-thread with
     * the worker-threads
     * 
     * @param crawler
     *            the crawler-thread as Thread
     * @param worker
     *            the worker-threads as array of Threads
     * @param listener
     *            the listener object that gets executed by the crawler-thread
     *            as StreamListener
     * @param queue
     *            the queue where the crawler enqueues all the status objects
     *            for the worker threads as ConcurrentLinkedQueue<Status>
     */
    public Controller(Thread crawler, Thread[] worker, StreamListener listener,
            ConcurrentLinkedQueue<Status> queue) {
        this.listener = listener;
        this.queue = queue;
        this.crawler = crawler;
    }

    @Override
    public void run() {
        int c = 0;
        while (c < 1800000) {

            // TODO
            // start more worker if queue has too much elements
            // stop them when queues size is ordinary

            if (!crawler.isAlive()) {
                // restart crawler
                Thread crawler = new Thread(listener);
                this.crawler = crawler;
                crawler.start();
            }

            // sleep for 10s
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            c += 10000;
        }

        listener.stop = true;
        while (!queue.isEmpty()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        System.exit(0);
    }

}
