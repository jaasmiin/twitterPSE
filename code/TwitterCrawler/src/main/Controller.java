package main;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

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

    // timeout for connection to twitter
    // private static int TIMEOUT = 3600; // 10minutes

    private StreamListener listener;
    // private ConcurrentLinkedQueue<Status> queue;
    // private Thread crawler;
    private int timeout;
    private Logger logger;
    private StatusProcessor[] worker;
    private Thread[] workerThreads;

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
     * @param logger
     *            a global logger for the whole program as Logger
     * @param timeout
     *            the timeout in seconds (0 for infinity) as Integer
     */
    public Controller(Thread crawler, StatusProcessor[] worker,
            Thread[] workerThreads, StreamListener listener,
            ConcurrentLinkedQueue<Status> queue, Logger logger, int timeout) {
        this.listener = listener;
        // this.queue = queue;
        this.workerThreads = workerThreads;
        // this.crawler = crawler;
        this.logger = logger;
        this.worker = worker;
        this.timeout = timeout;
    }

    @Override
    public void run() {
       
        int c = 0;
        if (timeout == 0){
            c = -1;
        }

        while (c < timeout) {

            // if (!crawler.isAlive()) {
            // // restart crawler
            // Thread crawler = new Thread(listener);
            // this.crawler = crawler;
            // crawler.start();
            // }

            // sleep for 1s
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.warning(e.getMessage() + "\n");
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            if (timeout != 0) {
                c += 1;
            }
        }

        listener.exit();
        logger.info("Crawler terminated by the Controller");

        // worker stop if queue is empty
        for (int i = 0; i < worker.length; i++) {
            worker[i].run = false;
        }

        // while (!queue.isEmpty()) {
        // try {
        // Thread.sleep(50);
        // } catch (InterruptedException e) {
        // logger.warning(e.getMessage() + "\n");
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // }

        // wait till all workers ended
        for (int i = 0; i < workerThreads.length; i++) {
            try {
                workerThreads[i].join();
            } catch (InterruptedException e) {
                logger.warning(e.getMessage());
                // TODO
                e.printStackTrace();
            }
        }

        logger.info("Program terminated");
        System.exit(0);
    }
}
