package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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
    private static int THREADNUM = 10;

    /**
     * starts a crawler, that collects data from twitter
     * 
     * @param args
     *            1. Argument: The run-time in seconds that the crawler should
     *            run; 2. Argument: The password for the root user of the
     *            database twitter; no more arguments are required
     */
    public static void main(String[] args) {

        // only numbers from 0-9
        if (args.length > 1 && args[0].matches("[0-9]*")
                && args[1].length() > 0) {
            coordinator(Integer.parseInt(args[0]), args[1]);
        } else {
            System.out.println("Error: Wrong argument!");
        }

    }

    /**
     * starts required threads to collect data
     * 
     * @param time
     *            the run-time in seconds that the crawler should run as Integer
     * @param pw
     *            the password for the root user of the database twitter as
     *            String
     */
    private static void coordinator(int time, String pw) {
        ConcurrentLinkedQueue<Status> statusQueue = new ConcurrentLinkedQueue<Status>();

        Logger log = null;
        // get logger
        try {
            log = getLogger();
        } catch (SecurityException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // create thread to pull status from twitter
        StreamListener sl = new StreamListener(statusQueue, log, false);
        Thread crawler = new Thread(sl);
        crawler.start();
        log.info("Crawler started");

        // create threads that extract informations of status and store them in
        // the db
        Thread worker[] = new Thread[THREADNUM];
        StatusProcessor[] workerObject = new StatusProcessor[THREADNUM];
        for (int i = 0; i < THREADNUM; i++) {
            workerObject[i] = new StatusProcessor(statusQueue, log, pw);
            Thread thread = new Thread(workerObject[i]);
            worker[i] = thread;
            worker[i].start();
        }
        log.info("StatusProcessors started");

        // controller to stop other threads after a specified time
        Thread c = new Thread(new Controller(crawler, workerObject, worker, sl,
                statusQueue, log, time));
        c.start();
        log.info("Controller started");

        manuelExit(sl, log, worker, workerObject, statusQueue, crawler);

        // join threads

        try {
            crawler.join();
        } catch (InterruptedException e) {
            log.warning(e.getMessage());
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        for (int i = 0; i < THREADNUM; i++) {
            try {
                worker[i].join();
            } catch (InterruptedException e) {
                log.warning(e.getMessage());
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        try {
            c.join();
        } catch (InterruptedException e) {
            log.warning(e.getMessage());
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * user can exit program via console
     * 
     * @param sl
     * @param logger
     * @param workerThreads
     * @param worker
     */
    private static void manuelExit(StreamListener sl, Logger logger,
            Thread[] workerThreads, StatusProcessor[] worker,
            Queue<Status> queue, Thread stream) {

        boolean run = true;
        while (run) {
            System.out
                    .println("Enter 'exit' to exit or 'status' to view the current status: ");
            String in = "";
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(System.in));
                in = reader.readLine();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (in.equals("exit")) {
                run = false;

            } else if (in.equals("status")) {
                // print status
                System.out.println("Current state of the crawler: ");
                System.out.println("Number of status-objects in queue: "
                        + queue.size());
                System.out
                        .println(stream.isAlive() ? "Streamlistener receives data from twitter"
                                : "Streamlistener has crashed");
                System.out.println("More Informations");
            }
        }

        sl.exit();

        // worker stop if queue is empty
        for (int i = 0; i < worker.length; i++) {
            worker[i].run = false;
        }

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

        System.out.println("Crawler terminated");
        logger.info("Program terminated by user");
        System.exit(0);

    }

    private static Logger getLogger() throws SecurityException, IOException {

        Logger l = Logger.getLogger("logger");
        FileHandler fh;
        fh = new FileHandler("LogFile.log", true);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
        l.addHandler(fh);
        // true: print output on console and into file
        // false: only store output in logFile
        l.setUseParentHandlers(false);

        return l;
    }

}
