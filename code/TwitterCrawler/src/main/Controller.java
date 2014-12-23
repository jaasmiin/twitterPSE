package main;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import mysql.AccessData;
import twitter4j.Status;

/**
 * controller to control the other threads and start dynamically more
 * worker-threads, restarts also the crawler if it has been shut down
 * 
 * @author Holger Ebhart
 * @version 1.0
 * 
 */
public class Controller extends Thread {
    private final static int THREADNUM = 15;
    private final static int MAX_SIZE = 50000;
    private final static int INTERVAL = 10; // interval to wait in seconds

    private ConcurrentLinkedQueue<Status> statusQueue;
    private boolean run = true;
    private Logger log;
    private AccessData accessData;
    private int runtime;
    private StreamListener streamListener;
    private Thread thrdStreamListener;
    private Thread[] thrdStatusProcessor = new Thread[THREADNUM];
    private AccountUpdate accountUpdate;
    private Thread thrdAccountUpdate;
    private StatusProcessor[] statusProcessor = new StatusProcessor[THREADNUM];

    /**
     * 
     * @param timeout
     *            the timeout in seconds (0 for infinity) as Integer
     * @param accessData
     *            the access data to the database as AccessData
     * @throws IOException
     *             if an error with the LogFile.log has occurred
     */
    public Controller(int timeout, AccessData accessData) throws IOException {
        log = getLogger();
        log.info("Controller started");
        this.accessData = accessData;
        runtime = timeout;

        statusQueue = new ConcurrentLinkedQueue<Status>();
    }

    @Override
    public void run() {
        // create thread to pull status from twitter:
        streamListener = new StreamListener(statusQueue, log, false);
        thrdStreamListener = new Thread(streamListener);
        thrdStreamListener.start();
        log.info("Crawler started");

        ConcurrentHashMap<Long, Object> hashSet = new ConcurrentHashMap<Long, Object>();
        try {
            accountUpdate = new AccountUpdate(log, hashSet, accessData);
        } catch (SQLException e) {
            log.severe("AccountUpdate not started - running without AccountUpdate:\n"
                    + e.getMessage());
        }
        thrdAccountUpdate = new Thread(accountUpdate);
        thrdAccountUpdate.start();

        // create threads that extract informations of status and store them in
        // the db
        for (int i = 0; i < THREADNUM; ++i) {
            statusProcessor[i] = new StatusProcessor(statusQueue, hashSet, log,
                    accessData);
            thrdStatusProcessor[i] = new Thread(statusProcessor[i]);
            thrdStatusProcessor[i].start();
        }
        log.info("StatusProcessors started");

        // runtime always >= 0
        if (runtime > 0) {
            new java.util.Timer().schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    shutdown();
                    System.exit(0);
                }
            }, runtime * 1000);
        }

        limitQueue();

    }

    /**
     * Shut down server
     */
    public void shutdown() {
        boolean success = true;

        // send stop message
        run = false;
        streamListener.exit();
        // exit and interrupt AccountUpdate
        accountUpdate.exit();
        thrdAccountUpdate.interrupt();
        for (int i = 0; i < THREADNUM; i++) {
            statusProcessor[i].run = false;
        }

        // join Controller
        // TODO

        // join crawler
        System.out.print("\n Terminating crawler..");
        try {
            thrdStreamListener.join();
        } catch (InterruptedException e) {
            System.out.print(". Error (" + e.getMessage() + ").");
            log.warning(e.getMessage());
            success = false;
        }
        if (success) {
            System.out.println(". done.");
        }

        // join accountupdater
        success = true;
        System.out.print(" Terminating accountUpdater..");
        try {
            thrdAccountUpdate.join();
        } catch (InterruptedException e) {
            System.out.print(". Error (" + e.getMessage() + ").");
            log.warning(e.getMessage());
            success = false;
        }
        if (success) {
            System.out.println(". done.");
        }

        // waiting for empty queue
        success = true;
        System.out.print(" Terminating status processors..");
        while (getQueueSize() > 0) {
            try {
                sleep(100);
            } catch (InterruptedException e) {
                System.out.print(". Error (" + e.getMessage() + ").");
                log.warning(e.getMessage());
                success = false;
            } catch (IllegalArgumentException e) {
                System.out.print(". Error (" + e.getMessage() + ").");
                log.warning(e.getMessage());
                success = false;
            }
        }

        // join status processors
        if (success) {
            for (int i = 0; i < THREADNUM; ++i) {
                try {
                    thrdStatusProcessor[i].join();
                } catch (InterruptedException e) {
                    System.out.print(". Error (" + e.getMessage() + ").");
                    log.warning(e.getMessage());
                    success = false;
                    break;
                }
            }
            if (success) {
                System.out.println(". done.");
            }
        }
        log.info("Program terminated by user");
    }

    private Logger getLogger() throws SecurityException, IOException {
        Logger l = Logger.getLogger("logger");
        FileHandler fh = new FileHandler("LogFile.log", true);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
        l.addHandler(fh);
        // true: print output on console and into file
        // false: only store output in logFile
        l.setUseParentHandlers(false);
        return l;
    }

    public int getQueueSize() {
        return statusQueue.size();
    }

    @Override
    public String toString() {
        int threadsAlive = 0;
        for (int i = 0; i < thrdStatusProcessor.length; i++) {
            if (thrdStatusProcessor[i].isAlive()) {
                threadsAlive++;
            }
        }
        return " STATE OF THE CRAWLER: " + "\n"
                + " Number of status-objects in queue: " + getQueueSize()
                + "\n" + " Status of the Streamlistener: " + "\n"
                + " Status of the Accountupdater: "
                + (thrdAccountUpdate.isAlive() ? "running" : "crashed") + "\n"
                + " Number of running workers: " + threadsAlive + "/"
                + THREADNUM;
    }

    private void limitQueue() {
        int count = 0;
        while (run) {

            // one reconnect to twitter per day
            if (count >= 86400) {// one day = 86400 seconds
                count = 0;
                streamListener.refresh();
                // TODO add a new day in the database
            }

            if (statusQueue.size() > MAX_SIZE) {

                log.info("StatusQueue has been cleared at "
                        + statusQueue.size() + " Elements");

                for (int i = 0; i < MAX_SIZE / 2; i++) {
                    statusQueue.poll();
                }

            }

            try {
                Thread.sleep(INTERVAL * 1000); // wait for INTERVAL seconds
            } catch (InterruptedException e) {
                log.info("Controller has been interrupted\n" + e.getMessage());
            }
            count += INTERVAL;
        }
    }
}
