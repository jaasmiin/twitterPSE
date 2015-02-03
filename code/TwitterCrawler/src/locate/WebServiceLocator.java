package locate;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import twitter4j.User;
import util.Util;
import main.RunnableListener;
import mysql.AccessData;
import mysql.DBcrawler;

/**
 * class to locate accounts and retweets by a webservice
 * 
 * @author Matthias Schimek, Holger Ebhart
 * 
 */
public class WebServiceLocator implements RunnableListener {

    private static final String WEB_SERVICE_URL = "http://172.22.214.196/localhost/TweetLoc.asmx/getCountry?";
    private static final String DEFAULT_LOCATION = "0";
    private Logger logger;
    private ConcurrentLinkedQueue<LocateStatus> locateQueue;
    private DBcrawler dbc;
    private boolean run;
    private long countQuery = 0;
    private long countLocatedQuery = 0;

    /**
     * creates a new WebServiceLocator to locate Strings via a webservice
     * 
     * @param accessData
     *            the access data for the root user of the database as String
     * @param logger
     *            a global logger for the whole program as Logger
     * @param locateQueue
     *            the queue where status-objects who have to be located via the
     *            webservice where buffered as
     *            ConcurrentLinkedQueue<LocateStatus>
     * @throws InstantiationException
     *             thrown if it isn't possible to connect to the database
     */
    public WebServiceLocator(AccessData accessData, Logger logger,
            ConcurrentLinkedQueue<LocateStatus> locateQueue)
            throws InstantiationException {
        run = true;
        this.logger = logger;
        this.locateQueue = locateQueue;

        // get database connection
        try {
            dbc = new DBcrawler(accessData, logger);
        } catch (IllegalAccessException | ClassNotFoundException | SQLException e) {
            dbc = null;
            logger.severe(e.getMessage() + "\n");
            throw new InstantiationException(
                    "Not able to instantiate Database-connection.");
        }
    }

    @Override
    public void run() {

        // check weather a connection to the database has been established or
        // not
        if (dbc == null) {
            logger.severe("A WebServiceLocator couldn't been started: No database connection!");
            return;
        }

        // open new connection to database
        try {
            dbc.connect();
        } catch (SQLException e) {
            logger.severe(e.getMessage() + "\n");
            return;
        }

        // work till the program will be shut down
        while (run) {

            // sleep if no elements are queued
            try {
                Thread.sleep(50); // sleep for 0.05s
            } catch (InterruptedException e) {
                // logger.info("WebServiceLocator interrupted\n" +
                // e.getMessage());
            }

            LocateStatus status;
            // work until the queue is empty
            while (!locateQueue.isEmpty()) {

                // try to enqueue an element to locate it
                status = null;
                try {
                    status = locateQueue.poll();
                } catch (Exception e) {
                    status = null;
                }

                if (status != null) {
                    locate(status);
                }
            }
        }

        dbc.disconnect();

    }

    private void locate(LocateStatus status) {

        if (status.getId() == -1) {
            // locate and add account
            locateAccount(status.getStatus().getUser(), status.getStatus()
                    .getCreatedAt(), status.isTweet());
        } else {
            // locate and add account and retweet
            if (!status.isAccountLocated()) {
                // locate and add account, so that the retweet to this account
                // can be added
                locateAccount(status.getStatus().getUser(), status.getStatus()
                        .getCreatedAt(), status.isTweet());
            }

            if (status.isRetweetLocated()) {
                // add retweet to database
                dbc.addRetweet(status.getId(), status.getLocation(),
                        status.getDate());
            } else {
                // locate retweet and insert into database
                String countryCode = startWebServiceCall(status.getLocation(),
                        status.getTimeZone());
                dbc.addRetweet(status.getId(), countryCode, status.getDate());
            }
        }
    }

    private void locateAccount(User user, Date createdAt, boolean tweet) {

        // locate Account
        String countryCode = startWebServiceCall(user.getTimeZone(),
                user.getLocation());

        dbc.addAccount(user, countryCode, createdAt, tweet);

    }

    private String startWebServiceCall(String loc, String timeZone) {

        // format Strings for webservice request
        String location = Util.formatString(loc, logger);
        String timezone = Util.formatString(timeZone, logger);
        String countryCode = DEFAULT_LOCATION;

        // update counter
        countQuery++;

        // call webservice
        countryCode = callWebservice(location, timezone);
        // check result
        if (!countryCode.equals(DEFAULT_LOCATION)) {
            // add successful location into database to speed up localization
            dbc.addLocationString(countryCode, location, timezone);
            countLocatedQuery++;
        }

        return countryCode;
    }

    /**
     * Method that actually calls WEBSERVICE does not check input strings for
     * forbidden characters e.g. '&','@' etc.
     * 
     * @param location
     *            location attribute
     * @param timezone
     *            timezone attribue
     * @return countrycode in case of success, '0' otherwise
     */
    private String callWebservice(String location, String timezone) {
        String result = DEFAULT_LOCATION;
        try {

            URL u = new URL(WEB_SERVICE_URL + "userlocation=" + location
                    + "&timezone=" + timezone);

            InputStream stream = u.openStream();
            Scanner scanner = new Scanner(stream);
            result = scanner.useDelimiter("//Z").next();
            stream.close();
            scanner.close();

        } catch (MalformedURLException e1) {
            logger.info("URL nicht korrekt: " + e1.getMessage()
                    + "   location= " + location + " timezone=" + timezone);
            return DEFAULT_LOCATION;

        } catch (IOException e2) {
            logger.info("Webservice meldet Fehler: " + e2.getMessage()
                    + "   location= " + location + " timezone=" + timezone);
            return DEFAULT_LOCATION;
        }

        // parsing received String to XML-Doc and get content from created
        // XML-Doc
        try {

            DocumentBuilderFactory fctr = DocumentBuilderFactory.newInstance();
            DocumentBuilder bldr = fctr.newDocumentBuilder();
            InputSource insrc = new InputSource(new StringReader(result));

            Document doc = bldr.parse(insrc);
            result = doc.getFirstChild().getTextContent();

        } catch (ParserConfigurationException | IOException e1) {
            logger.info("XML or IO error!");
            return DEFAULT_LOCATION;

        } catch (SAXException e2) {

            logger.info("Fehlerhafter EingabeString" + e2.getMessage());
            return DEFAULT_LOCATION;
        }

        // string formatting (deleting '"' etc)
        result = result.substring(1, result.length() - 1);

        if (result.equals(DEFAULT_LOCATION)) {
            return DEFAULT_LOCATION;
        }

        result = result.trim();
        return result;
    }

    /**
     * Returns number of queries and located queries
     * 
     * @return Entryno. in return array: 1: number of queries, 2: number of
     *         located queries
     */
    public long[] getStatistic() {
        long[] statistics = {countQuery, countLocatedQuery };
        return statistics;
    }

    @Override
    public void exit() {
        run = false;

    }

}
