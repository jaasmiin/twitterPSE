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
import main.RunnableListener;
import mysql.AccessData;
import mysql.DBcrawler;

/**
 * 
 * @author Matthias Schimek, Holger Ebhart
 * @version 1.1
 * 
 */
public class WebServiceLocator implements RunnableListener {

    private final static String WEB_SERVICE_URL = "http://172.22.214.196/localhost/TweetLoc.asmx/getCountry?";
    private final static String DEFAULT_LOCATION = "0";
    private Logger logger;
    private ConcurrentLinkedQueue<LocateStatus> locateQueue;
    private DBcrawler dbc;
    private Formatter formatter;
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
     */
    public WebServiceLocator(AccessData accessData, Logger logger,

    ConcurrentLinkedQueue<LocateStatus> locateQueue)
            throws InstantiationException {
        run = true;
        this.logger = logger;
        this.locateQueue = locateQueue;
        formatter = new Formatter(logger);
        try {
            dbc = new DBcrawler(accessData, logger);
        } catch (IllegalAccessException | ClassNotFoundException | SQLException e) {
            dbc = null;
            logger.severe(e.getMessage() + "\n");
            throw new InstantiationException(
                    "Not able to instantiate Databaseconnection.");
        }
    }

    @Override
    public void run() {

        if (dbc == null) {
            logger.severe("A WebServiceLocator couldn't been started: No database connection!");
            return;
        }

        try {
            dbc.connect();
        } catch (SQLException e) {
            logger.severe(e.getMessage() + "\n");
            return;
        }

        while (run) {

            try {
                Thread.sleep(50); // sleep for 0.05s
            } catch (InterruptedException e) {
                // logger.info("WebServiceLocator interrupted\n" +
                // e.getMessage());
            }
            while (!locateQueue.isEmpty()) {

                LocateStatus status = null;
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
            // add account
            locateAccount(status.getStatus().getUser(), status.getStatus()
                    .getCreatedAt(), status.isTweet());
        } else {
            // add account and retweet
            if (!status.isAccountLocated()) {
                // locate and add account
                locateAccount(status.getStatus().getUser(), status.getStatus()
                        .getCreatedAt(), status.isTweet());
            }
            if (status.isRetweetLocated()) {
                dbc.addRetweet(status.getId(), status.getLocation(),
                        status.getDate());
            } else {
                locateRetweet(status);
            }
        }
    }

    private void locateRetweet(LocateStatus retweet) {

        String location = formatter.formatString(retweet.getLocation());
        String timezone = formatter.formatString(retweet.getTimeZone());
        String countryCode = DEFAULT_LOCATION;

        // update counter
        countQuery++;

        countryCode = callWebservice(location, timezone);
        if (!countryCode.equals(DEFAULT_LOCATION)) {
            dbc.addLocationString(countryCode, location, timezone);
            countLocatedQuery++;
        }

        dbc.addRetweet(retweet.getId(), countryCode, retweet.getDate());
    }

    private void locateAccount(User user, Date createdAt, boolean tweet) {

        String countryCode = DEFAULT_LOCATION;
        String timezone = formatter.formatString(user.getTimeZone());
        String location = formatter.formatString(user.getLocation());

        // update counter
        countQuery++;

        countryCode = callWebservice(location, timezone);
        if (!countryCode.equals(DEFAULT_LOCATION)) {
            dbc.addLocationString(countryCode, location, timezone);
            countLocatedQuery++;
        }

        dbc.addAccount(user, countryCode, createdAt, tweet);

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
