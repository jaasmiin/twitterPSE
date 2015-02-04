package main;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import locate.LocateStatus;
import locate.WebServiceLocator;
import mysql.AccessData;
import mysql.DBcrawler;
import util.LoggerUtil;
import twitter4j.Status;

/**
 * controller to control the other threads and start dynamically more
 * worker-threads, restarts also the crawler if it has been shut down
 * 
 * @author Holger Ebhart
 * 
 */
public class Controller extends Thread {

    // max. size of the buffer between streamListener, statusProcessors and
    // locator
    private static final int MAX_SIZE = 100000;
    // interval to wait in seconds
    private static final int INTERVAL = 30;

    private final int workerThreadNum;
    private final int locatorThreadNum;
    private final int runtime;

    // database access
    private AccessData accessData;
    private DBcrawler dbc;
    private Date dateForDB;

    // buffer for data from twitter
    private ConcurrentLinkedQueue<Status> statusQueue;
    // buffer for data to locate
    private ConcurrentLinkedQueue<LocateStatus> locateQueue;

    private HashMap<String, String> locationHash;
    private boolean run = true;

    private StreamListener streamListener;
    private Thread thrdStreamListener;

    private StatusProcessor[] statusProcessor;
    private Thread[] thrdStatusProcessor;

    private WebServiceLocator[] locator;
    private Thread[] thrdLocator;

    private AccountUpdate accountUpdate;
    private Thread thrdAccountUpdate;

    private Logger logger;
    private Logger statisticLogger;

    /**
     * instantiate a new Controller
     * 
     * @param timeout
     *            the timeout in seconds (0 for infinity) as Integer
     * @param accessData
     *            the access data to the database as AccessData
     * @param numberOfThreads
     *            the number of worker threads as int
     * @throws IOException
     *             if an error with the LogFile.log has occurred
     */
    public Controller(int timeout, AccessData accessData, int numberOfThreads)
            throws IOException {

        workerThreadNum = numberOfThreads;
        // set number of locators relative to number of worker
        locatorThreadNum = workerThreadNum * 4;

        // initialize dataset to be ready for start
        statusProcessor = new StatusProcessor[workerThreadNum];
        thrdStatusProcessor = new Thread[workerThreadNum];

        locator = new WebServiceLocator[locatorThreadNum];
        thrdLocator = new Thread[locatorThreadNum];

        // prepare logger to log exceptions and informations
        statisticLogger = LoggerUtil.getLogger("Statistic");
        logger = LoggerUtil.getLogger();

        // load drivers for database access/connection
        this.dbc = null;
        try {
            this.dbc = new DBcrawler(accessData, logger);
        } catch (InstantiationException | IllegalAccessException
                | ClassNotFoundException | SQLException e) {
            logger.warning("No dates will be insert into the database because of: "
                    + e.getMessage());
            this.dbc = null;
        }

        this.accessData = accessData;
        runtime = timeout;

        // prepare calendar to add dates into the database
        dateForDB = new Date();
        // add 800 days
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(dateForDB);
        cal.add(Calendar.DATE, 800);
        dateForDB = cal.getTime();

        statusQueue = new ConcurrentLinkedQueue<Status>();
        locateQueue = new ConcurrentLinkedQueue<LocateStatus>();
        locationHash = new HashMap<String, String>();
    }

    @Override
    public void run() {

        // create thread to pull status from twitter:
        streamListener = new StreamListener(statusQueue, logger);
        thrdStreamListener = new Thread(streamListener);
        thrdStreamListener.start();
        logger.info("Crawler started");

        // prepare HashMap to track unverified accounts from the database
        ConcurrentHashMap<Long, Object> hashSet = new ConcurrentHashMap<Long, Object>();
        try {
            accountUpdate = new AccountUpdate(logger, hashSet, accessData);
        } catch (SQLException e) {
            logger.severe("AccountUpdate not started - running without AccountUpdate:\n"
                    + e.getMessage());
        }
        thrdAccountUpdate = new Thread(accountUpdate);
        thrdAccountUpdate.start();

        // get connection to database adn fill the HashMap with unverified
        // accounts
        if (dbc != null) {
            try {
                dbc.connect();
                locationHash = dbc.getLocationStrings();
                dbc.disconnect();
            } catch (SQLException e) {
                logger.warning("Database-connection failure! " + e.getMessage());
            }
        }

        // create and start threads that extract informations of status and
        // store them in
        // the database (worker-threads)
        for (int i = 0; i < workerThreadNum; i++) {
            try {
                statusProcessor[i] = new StatusProcessor(statusQueue,
                        locateQueue, hashSet, locationHash, logger, accessData);
                thrdStatusProcessor[i] = new Thread(statusProcessor[i]);
                thrdStatusProcessor[i].start();
            } catch (InstantiationException e) {
                statusProcessor[i] = null;
                thrdStatusProcessor[i] = null;
                logger.warning("Couldn't start a StatusProcessor: "
                        + e.getMessage());
            }
        }
        logger.info("StatusProcessors started");

        // create and start threads that call the WebService for location
        // this locator-threads get their data from the locatequeue and write it
        // directly into the database
        for (int i = 0; i < locatorThreadNum; i++) {
            try {
                locator[i] = new WebServiceLocator(accessData, logger,
                        locateQueue);
                thrdLocator[i] = new Thread(locator[i]);
                thrdLocator[i].start();
            } catch (InstantiationException e) {
                locator[i] = null;
                thrdLocator[i] = null;
                logger.warning("Couldn't start a WebServiceLocator: "
                        + e.getMessage());
            }
        }
        logger.info("WebServiceLocators started");

        // runtime always >= 0
        if (runtime > 0) {
            new java.util.Timer().schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    shutdown(false);
                    System.exit(0);
                }
            }, runtime * 1000);
        }

        // start control over the whole crawler
        limitQueue();

    }

    /**
     * Shut down server
     * 
     * @param kill
     *            true if the application should terminate immediately, false if
     *            it should shut down
     */
    public void shutdown(boolean kill) {

        boolean success = true;

        // send stop message to all threads and interrupt them if kill-flag is
        // set
        run = false;
        streamListener.exit();
        // exit and interrupt AccountUpdate
        if (accountUpdate != null)
            accountUpdate.exit();
        thrdAccountUpdate.interrupt();

        // exit worker-threads
        for (int i = 0; i < workerThreadNum; i++) {
            if (statusProcessor[i] != null) {
                statusProcessor[i].exit();
            }
            // interrupt them to finish work
            if (thrdStatusProcessor[i] != null) {
                thrdStatusProcessor[i].interrupt();
            }
        }

        // exit locator threads
        for (int i = 0; i < locatorThreadNum; i++) {
            if (locator[i] != null) {
                locator[i].exit();
            }
            // interrupt them if they are sleeping
            if (thrdLocator[i] != null) {
                thrdLocator[i].interrupt();
            }
        }

        // stop Controller/this
        this.interrupt();

        // join crawler thread
        // wait till the crawler-thread has finished
        System.out.print("\n Terminating crawler..");
        try {
            thrdStreamListener.join();
        } catch (InterruptedException e) {
            System.out.print(". Error (" + e.getMessage() + ").");
            logger.warning(e.getMessage());
            success = false;
        }
        if (success) {
            System.out.println(". done.");
        }

        // join accountUpdate thread
        success = true;
        System.out.print(" Terminating accountUpdate..");
        try {
            thrdAccountUpdate.join();
        } catch (InterruptedException e) {
            System.out.print(". Error (" + e.getMessage() + ").");
            logger.warning(e.getMessage());
            success = false;
        }
        if (success) {
            System.out.println(". done.");
        }

        success = true;
        System.out.print(" Terminating status processors..");

        // wait till queues are empty (than all the work is done)
        if (kill) {
            // kill-flag is set, so reset the queues, so that there is no more
            // work to do
            statusQueue.clear();
            locateQueue.clear();
        } else {
            // waiting for empty queues
            while (getQueueSize() > 0) {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    System.out.print(". Error (" + e.getMessage() + ").");
                    logger.warning(e.getMessage());
                    success = false;
                } catch (IllegalArgumentException e) {
                    System.out.print(". Error (" + e.getMessage() + ").");
                    logger.warning(e.getMessage());
                    success = false;
                }
            }
        }

        if (success) {
            // join status processors
            // wait untill each worker thread has finished
            for (int i = 0; i < workerThreadNum; ++i) {
                try {
                    if (thrdStatusProcessor[i] != null) {
                        thrdStatusProcessor[i].join();
                    }
                } catch (InterruptedException e) {
                    System.out.print(". Error (" + e.getMessage() + ").");
                    logger.warning(e.getMessage());
                    success = false;
                    break;
                }
            }
            // join locators
            for (int i = 0; i < locatorThreadNum; ++i) {
                try {
                    if (thrdLocator[i] != null) {
                        thrdLocator[i].join();
                    }
                } catch (InterruptedException e) {
                    System.out.print(". Error (" + e.getMessage() + ").");
                    logger.warning(e.getMessage());
                    success = false;
                    break;
                }
            }
            if (success) {
                System.out.println(". done.");
            }
        }

        logger.info("Program terminated by user");
    }

    /**
     * returns the max size of the statusQueue and the locateQueue
     * 
     * @return the max size of the statusQueue and the locateQueue as int
     */
    public int getQueueSize() {
        // return the max size of the two buffer-queues
        return Math.max(locateQueue.size(), statusQueue.size());
    }

    @Override
    public String toString() {

        // return the current state of the crawler as console-output

        // count worker-threads, that are alive
        int threadsAlive = 0;
        for (int i = 0; i < thrdStatusProcessor.length; i++) {
            if (thrdStatusProcessor[i] != null
                    && thrdStatusProcessor[i].isAlive()) {
                threadsAlive++;
            }
        }

        // count locator-threads that are alive
        int threadsLocatorAlive = 0;
        for (int i = 0; i < thrdLocator.length; i++) {
            if (thrdLocator[i] != null && thrdLocator[i].isAlive()) {
                threadsLocatorAlive++;
            }
        }

        return " STATE OF THE CRAWLER: " + "\n"
                + " Number of status-objects in queue: " + statusQueue.size()
                + "\n" + " Number of status-objects to locate: "
                + locateQueue.size() + "\n" + " Status of the Streamlistener: "
                + streamListener.toString() + "\n"
                + " Status of the Accountupdater: "
                + (thrdAccountUpdate.isAlive() ? "running" : "crashed") + "\n"
                + " Number of running workers: " + threadsAlive + "/"
                + workerThreadNum + "\n" + " Number of running locators: "
                + threadsLocatorAlive + "/" + locatorThreadNum;
    }

    private void limitQueue() {
        int m = 0;
        int count = 0;

        // limit queues and control the crawler till the program is shut down
        while (run) {

            if (m >= 3600) { // write statistic once an hour
                m = 0;
                writeStatistic();
            }
            m += INTERVAL;

            // // update locationHashMap twice a day
            // if (count / 40000 == 1 || count / 40000 == 2) {
            // try {
            // updateLocationHash();
            // } catch (SQLException e) {
            // logger.warning("Database-connection failed! "
            // + e.getMessage());
            // }
            // }

            if (count >= 86400) { // one day = 86400 seconds
                count = 0;

                // add a new date to the database (once a day)
                addDate();
            }

            // limit the buffer queues
            limitQueue(statusQueue);
            limitQueue(locateQueue);

            // wait
            try {
                Thread.sleep(INTERVAL * 1000); // wait for INTERVAL seconds
            } catch (InterruptedException e) {
                logger.info("Controller has been interrupted: \n"
                        + e.getMessage());
            }
            count += INTERVAL;
        }
    }

    // private void updateLocationHash() throws SQLException {
    // dbc.connect();
    // HashMap<String, String> temp = dbc.getLocationStrings();
    // dbc.disconnect();
    // Set<String> keys = temp.keySet();
    // for (String k : keys) {
    // if (!locationHash.containsKey(k)) {
    // locationHash.put(k, temp.get(k));
    // }
    // }
    //
    // }

    private void addDate() {

        try {
            // connect to database
            dbc.connect();

            // get date from calendar
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(dateForDB);
            // insert new date into database
            if (dbc.addDay(dateForDB)) {
                // add a day on to the calendar
                cal.add(Calendar.DATE, 1);
                dateForDB = cal.getTime();
            }
            // disconnect from database
            dbc.disconnect();

        } catch (SQLException e) {
            logger.info("A date couldn't be insert into the database: "
                    + e.getMessage());
        }
    }

    @SuppressWarnings("rawtypes")
    private void limitQueue(ConcurrentLinkedQueue queue) {

        // check if queues has more elements than allowed
        if (queue.size() > MAX_SIZE) {

            // remove elements till the buffer is half full
            for (int i = 0; i < MAX_SIZE / 2; i++) {
                queue.poll();
            }
        }
    }

    private void writeStatistic() {

        // write statistics about the received data and about the data that has
        // been insert into the database

        String msg = "";
        msg += "Summe aller empfangener Status-Objekte: "
                + streamListener.getCounter() + "\n";

        // get informations about the data from the StatusProcessors and from
        // the locators
        int[] sum = new int[9];
        for (int j = 0; j < sum.length; j++) {
            sum[j] = 0;
        }
        // sum the data
        for (StatusProcessor sp : statusProcessor) {
            int[] temp = sp.getCounter();
            for (int i = 0; i < sum.length; i++) {
                sum[i] += temp[i];
            }
        }

        // get and sum the informations about the data from the
        // WebServiceLocators
        int[] locate = new int[] {0, 0 };
        for (WebServiceLocator l : locator) {
            locate[0] += l.getStatistic()[0];
            locate[1] += l.getStatistic()[1];
        }
        msg += "Summe aller interessanten Status-Objekte: " + sum[8] + "\n";
        msg += "LOKALISIERUNG\n";
        msg += "Summe Anfragen WebService: " + locate[0] + "\n";
        msg += "Erfolgreiche Locationen WebService: " + locate[1] + "\n";
        msg += "Summe Anfragen Locator: " + sum[0] + "\n";
        msg += "Erfolgreiche Locationen Locator: " + sum[1] + "\n";
        msg += "davon über place lokalisiert: " + sum[2] + "\n";
        msg += "davon über geotag lokalisiert: " + sum[3] + "\n";
        msg += "davon über HashMap lokalisiert: " + sum[4] + "\n";
        msg += "Summe vorhandener Places: " + sum[5] + "\n";
        msg += "Summe vorhandener Geotags: " + sum[6] + "\n";
        msg += "Summe vorhandener location-Information: " + sum[7] + "\n";

        statisticLogger.info(msg);
    }
}
