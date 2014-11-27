package main;

import java.util.concurrent.ConcurrentLinkedQueue;

import twitter4j.Status;

/**
 * main class to collect data from twitter over the twitter stream api with
 * multiple threads
 * 
 * @author Holger Ebhart
 * @version 1.0
 * 
 */
public class CrawlerMain {

    /**
     * number of worker-threads
     */
    private static int THREADNUM = 8;

    /**
     * starts a crawler, that collects data from twitter
     * 
     * @param args
     *            no arguments required
     */
    public static void main(String[] args) {
        coordinator();

    }

    /**
     * starts required threads to collect data
     */
    private static void coordinator() {
        ConcurrentLinkedQueue<Status> q = new ConcurrentLinkedQueue<Status>();

        // create thread to pull status from twitter
        StreamListener sl = new StreamListener(q);
        Thread crawler = new Thread(sl);
        crawler.start();

        // create threads that extract informations of status and store them in
        // the db
        Thread worker[] = new Thread[THREADNUM];
        for (int i = 0; i < THREADNUM; i++) {
            Thread thread = new Thread(new StatusProcessor(q));
            worker[i] = thread;
            worker[i].start();
        }

        // controller to stop other threads after a specified time
        Thread c = new Thread(new Controller(crawler, worker, sl, q));
        c.start();

        // join threads

        try {
            crawler.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for (int i = 0; i < THREADNUM; i++) {
            try {
                worker[i].join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        try {
            c.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
