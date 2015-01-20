package locate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.geonames.GeoNamesException;
import org.geonames.WebService;

import twitter4j.GeoLocation;
import twitter4j.Place;

/**
 * 
 * @author Matthias Schimek
 * @version 1.0
 * 
 */
public class Locator {

    private String webServiceURL = "http://172.22.214.196/localhost/TweetLoc.asmx/getCountry?";

    public HashMap<String, String> map;
    private Logger logger;
    private int countMod = 0;

    private int numberOfReq = 0;
    private int numberOfLocReq = 0;
    private int numberOfPlaceLoc = 0;
    private int numberOfGeoTagLoc = 0;
    private int numberOfLocationTagLoc = 0;
    private int numberOfWebserviceLoc = 0;
    private int numberOfHashMapLoc = 0;
    private int reqWithPlace = 0;
    private int reqWithGeoTag = 0;
    private int reqWithLocation = 0;

    /**
     * Initiates a new instance of 'Locator'
     * 
     * @param log
     *            Logger to log all remarkable events
     */
    public Locator(Logger log) {
        this.logger = log;
        map = new HashMap<String, String>();
        readFromFile(new File("HashNeu"));

    }

    private void writeToFile(File file) {
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        try {
            Writer writer = new FileWriter(file.getPath(), false);
            while (it.hasNext()) {
                Map.Entry<String, String> pairs = it.next();
                writer.write(pairs.getKey() + "#" + pairs.getValue());
                // System.out.println(pairs.getKey()+"#"+pairs.getValue());
                writer.append(System.getProperty("line.separator"));
            }
            writer.close();
        } catch (IOException e) {
            logger.warning("Cannot write HashMap to file" + e.getMessage());
        }
    }

    private void readFromFile(File file) {
        String value = null;
        String key = null;
        String input = null;

        try {
            BufferedReader b = new BufferedReader(
                    new FileReader(file.getPath()));
            // System.out.println(file.getAbsolutePath());
            input = b.readLine();
            while (input != null) {
                // System.out.println(input);
                String[] tmp = input.split("#");
                if (tmp.length < 2) {
                    logger.info("Wrong content in file, input does not fit pattern 'key#value'");
                } else {
                    if (tmp[0] != null && tmp[1] != null) {
                        key = tmp[0].toLowerCase();
                        value = tmp[1];
                        // only insert in hashMap if hashMap does not already
                        // contain that key
                        if (!map.containsKey(key)) {
                            map.put(key, value);
                        }
                    }
                }
                input = b.readLine();
            }

            b.close();

        } catch (IOException e) {
            logger.warning("Cannot read from file to HashMap " + e.getMessage());
        }

    }

    /**
     * determine the country/location of given geo-coordinates
     * 
     * @param geotag
     *            the geo-coordinates as GeoLocation
     * @return the code/name of the country/location on success and "0"
     *         otherwise as String
     * @throws GeoNamesException
     * @throws IOException
     */
    public String getLocation(GeoLocation geotag) {

        String res = "0";
        WebService.setGeoNamesServerFailover(null);
        WebService.setUserName("KIT_PSE");
        WebService.setConnectTimeOut(1000);
        try {
            res = WebService.countryCode(geotag.getLatitude(),
                    geotag.getLongitude());
        } catch (GeoNamesException e1) {
            logger.info("Geotag localiser: Geonames Excep: " + e1.getMessage());
            return "0";
        } catch (IOException e2) {
            logger.info("Geotag localiser: IO exeption: " + e2.getMessage());
            return "0";
        }
        return res;
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

            URL u = new URL(webServiceURL + "userlocation=" + location
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

    /**
     * tries to determine the country/location of a given name or word
     * 
     * @param location
     *            the input name or word to determine the country/location as
     *            String
     * 
     * @param timezone
     *            timezone delivered by twitter (use the name you get from
     *            twitter status object)
     * @return the code of the country/location on success and "0" otherwise as
     *         String
     */
    private String getLocation(String location, String timezone) {

        String result = "0";

        // format given parameter

        if (location == null || location.equals("")) {
            return "0";
        }

        // format strings
        if (location != null) {
            location = location.replace(' ', '+');
            location = location.replaceAll(",", "+");
            location = location.replaceAll("[.!#$%&'()*,/:;=?@\\[\\]]", "");

        }

        if (timezone != null) {
            timezone = timezone.replace(' ', '+');
            timezone = timezone.replaceAll(",", "+");
            timezone = timezone.replaceAll("[.!#$%&'()*,/:;=?@\\[\\]]", "");
        } else {
            timezone = "";
        }
        // build URL
        try {
            location = URLEncoder.encode(location.trim(), "UTF-8");
            timezone = URLEncoder.encode(timezone.trim(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.info("unsupported URL Encodign Exceptin" + e.getMessage());
        }
        //

        // lookup in Hashtable to avoid calling the webservice

        if (location != null && map.containsKey(location.toLowerCase())) {
            // **** just for statistics
            numberOfHashMapLoc++;
            return map.get(location.toLowerCase()) + "  from hashtable";
        }

        // connection to Webservice
        result = callWebservice(location, timezone);

        // add positive result to Hashtable and save results periodically
        if (!result.equals("0") && result != null && !result.equals("")) {
            numberOfWebserviceLoc++;
            countMod++;

            map.put(location.toLowerCase(), result);
            if (countMod >= 5) {
                readFromFile(new File("HashNeu"));
                writeToFile(new File("HashNeu"));

                countMod = 0;
            }
        }

        return result;
    }

    /**
     * Tries to locate a 'tweet', specified by the given parameters
     * 
     * @param place
     *            Twitter-Place-Object
     * @param geotag
     *            Twitter-Geotag-Object
     * @param location
     *            String containing information about the location of a 'tweet'
     * @param timeZone
     *            String containing information about the timezone of a 'tweet'
     * @return countrycode if 'Tweet' could be located, "0" otherwise
     */
    // presumption: whenever place != null the request for location is positive!
    public String locate(Place place, GeoLocation geotag, String location) {
        // ,String timeZone) {
        String result = "0";

        // statistics
        numberOfReq++;
        if (place != null) {
            reqWithPlace++;
        }
        if (geotag != null) {
            reqWithGeoTag++;
        }
        if (location != null && !location.equals("")) {
            reqWithLocation++;
        }

        if (place != null) {

            result = place.getCountryCode();
            if (!result.equals("0")) {
                numberOfPlaceLoc++;
            }

        } else if (geotag != null) {

            result = getLocation(geotag);
            if (!result.equals("0")) {
                numberOfGeoTagLoc++;
            }

            // } else if (location != null && location != "") {
            //
            // result = getLocation(location, timeZone);
            // if (!result.equals("0")) {
            // numberOfLocationTagLoc++;
            // }
        }
        if (!result.equals("0")) {
            numberOfLocReq++;
        }
        return result;
    }

    /**
     * Returns the statistics for this instance of the 'Locator'
     * 
     * @return Content of the single values in the result array: 0: number of
     *         requests 1: number of successfully located requests (a
     *         countrycode could be returned) 2: number of requests located via
     *         place-attribute 3: number of requests located via geotag 4:
     *         number of requests located via location and timezone
     *         (webservice+hashmap, actually redundant information :)) 5: number
     *         of requests located via webservice 6: number of requests located
     *         via hashmap 7: number of requests containing place-attribute 8:
     *         number of requests containing geotag 9: number of request
     *         containing location information
     */
    public int[] getStatistic() {
        int[] statistics = {numberOfReq, numberOfLocReq, numberOfPlaceLoc,
                numberOfGeoTagLoc, numberOfLocationTagLoc,
                numberOfWebserviceLoc, numberOfHashMapLoc, reqWithPlace,
                reqWithGeoTag, reqWithLocation };
        return statistics;

    }

}
