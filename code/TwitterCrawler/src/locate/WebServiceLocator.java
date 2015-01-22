package locate;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
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
 * @author
 * @version 1.0
 * 
 */
public class WebServiceLocator implements RunnableListener {

    // TODO noch keine statistic-Funktion implementiert
    
    private final static String WEB_SERVICE_URL = "http://172.22.214.196/localhost/TweetLoc.asmx/getCountry?";
    private Logger logger;
    private ConcurrentLinkedQueue<StatusAccount> locateAccountQueue;
    private ConcurrentLinkedQueue<StatusRetweet> locateRetweetQueue;
    private DBcrawler dbc;
    private boolean run;
    private boolean locateAccounts;

    /**
     * 
     * @param accessData
     * @param logger
     * @param locateAccountQueue
     * @param locateRetweetQueue
     * @param locateAccounts
     * @throws InstantiationException
     */
    public WebServiceLocator(AccessData accessData, Logger logger,
            ConcurrentLinkedQueue<StatusAccount> locateAccountQueue,
            ConcurrentLinkedQueue<StatusRetweet> locateRetweetQueue,
            boolean locateAccounts) throws InstantiationException {
        run = true;
        this.logger = logger;
        this.locateAccountQueue = locateAccountQueue;
        this.locateRetweetQueue = locateRetweetQueue;
        this.locateAccounts = locateAccounts;
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
                // logger.info("StatusProcessor interrupted\n" +
                // e.getMessage());
            }

            if (locateAccounts) {
                while (!locateAccountQueue.isEmpty()) {

                    StatusAccount account = null;
                    try {
                        account = locateAccountQueue.poll();
                    } catch (Exception e) {
                        account = null;
                    }

                    if (account != null) {
                        locateAccount(account);
                    }
                }
            } else {
                while (!locateRetweetQueue.isEmpty()) {

                    StatusRetweet retweet = null;
                    try {
                        retweet = locateRetweetQueue.poll();
                    } catch (Exception e) {
                        retweet = null;
                    }

                    if (retweet != null) {
                        locateRetweet(retweet);
                    }
                }
            }
        }

        dbc.disconnect();

    }

    private void locateRetweet(StatusRetweet retweet) {

        String location = "0";

        // TODO Zähler aktualisieren
        location = callWebservice(retweet.getLocation(), retweet.getTimeZone());
        if (location != "0") {
            dbc.addLocationString(location, retweet.getLocation());
        }

        dbc.addRetweet(retweet.getId(), location, retweet.getDate());
    }

    private void locateAccount(StatusAccount account) {

        String location = "0";

        // TODO Zähler aktualisieren
        User user = account.getStatus().getUser();
        location = callWebservice(user.getLocation(), user.getTimeZone());
        if (location != "0") {
            dbc.addLocationString(location, user.getLocation());
        }

        dbc.addAccount(user, location, account.getStatus().getCreatedAt(),
                account.isTweet());

    }

    /**
     * Method that actually calls webservice, does not check input strings for
     * forbidden characters e.g. '&','@' etc.
     * 
     * @param location
     *            location attribute
     * @param timezone
     *            timezone attribue
     * @return countrycode in case of success, '0' otherwise
     */

    private String callWebservice(String location, String timezone) {
        String result = "0";
        try {

            URL u = new URL(WEB_SERVICE_URL + "userlocation=" + location
                    + "&timezone=" + timezone);
            // nur zu Testzwecken
            if (u == null) {
                logger.severe("URI is null  Location = " + location
                        + "  timezone = " + timezone);
            }
            InputStream stream = u.openStream();
            Scanner scanner = new Scanner(stream);
            result = scanner.useDelimiter("//Z").next();
            stream.close();
            scanner.close();
        } catch (MalformedURLException e1) {
            logger.info("URL nicht korrekt: " + e1.getMessage()
                    + "   location= " + location + " timezone=" + timezone);
            return "0";
        } catch (IOException e2) {
            logger.info("Webservice meldet Fehler: " + e2.getMessage()
                    + "   location= " + location + " timezone=" + timezone);
            return "0";
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
            return "0";
        } catch (SAXException e2) {

            logger.info("Fehlerhafter EingabeString" + e2.getMessage());
            return "0";
        }

        // string formatting (deleting '"' etc)
        result = result.substring(1, result.length() - 1);
        if (result.equals("0")) {

            return "0";
        }

        result = result.trim();
        return result;
    }

    @Override
    public void exit() {
        run = false;

    }

}
