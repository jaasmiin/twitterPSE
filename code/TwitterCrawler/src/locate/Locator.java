package locate;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
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

/**
 * class to locate words with a webservice !!! HashMap is just an idea and shoul
 * be discussed !!!
 * 
 * @author Matthias Schimek
 * @version 1.0
 * 
 */
public class Locator {

    private String webServiceURL = "http://172.22.214.196/localhost/TweetLoc.asmx/getCountry?";

    private HashMap<String, String> map;
    private Logger logger;


    public Locator(Logger log) {
        this.logger = log;
        map = new HashMap<String, String>();

        map.put("tokyo", "JP");
        map.put("baghdad", "IQ");
        map.put("irkutsk", "RU");
        map.put("seoul", "KR");
        map.put("budapest", "HU");

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
    public String getLocation(GeoLocation geotag) throws IOException,
            GeoNamesException {
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

    public String getLocation(String location, String timezone) {    
        String result = "0";
        
        // format given parameter

        if (location == null && timezone == null) {
            return "0";
        }
        if (location != null) {
            location = location.replace(' ', '+');
            location = location.replaceAll("#","");
            
        }
        if (timezone != null) {
            timezone = timezone.replace(' ', '+');
            timezone = location.replaceAll("#", "");
        }
        
        // lookup in Hashtable to avoid calling the webservice
        if(location != null && map.containsKey(location.toLowerCase())) {
            return map.get(location.toLowerCase())+ "  from hashtable";
        }


        // connection to Webservice
        try {
            URL u = new URL(webServiceURL + "userlocation=" + location
                    + "&timezone=" + timezone);
            InputStream stream = u.openStream();
            Scanner scanner = new Scanner(stream);
            result = scanner.useDelimiter("//Z").next();
            stream.close();
            scanner.close();
        } catch (MalformedURLException e1) {
            logger.info("URL nicht korrekt: " + e1.getMessage());
            return "0";
        } catch (IOException e2) {
            logger.info("Webservice meldet Fehler: " + e2.getMessage());
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
            // System.out.println("Error 1!");
            return "0";
        } catch (SAXException e2) {
            // System.out.println("Error 2!");
            logger.info("Fehlerhafter EingabeString" + e2.getMessage());
            return "0";
        }
        
        // string formatting (deleting '"' etc)
        result = result.substring(1, result.length() - 1);
        if (result.equals("0")) {
            return "0";
        }
        return result.trim();
    }

    result = result.trim();
    
    // add positive result to Hashtable
    map.put(location, result);
    return result;

}
