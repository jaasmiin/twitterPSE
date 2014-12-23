package locate;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;

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
    String webServiceURL = "http://172.22.214.196/localhost/TweetLoc.asmx/getCountry?";
    HashMap<String, String> map;

    public Locator() {
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
     * @param timeZone
     *            timezone delivered by twitter (use the name you get from
     *            twitter status object)
     * @param geotag
     *            the geo-coordinates as GeoLocation
     * @return the code/name of the country/location on success and null else as
     *         String
     */
    public String getLocation(GeoLocation geotag, String timeZone) {
        // Bitte entscheide ob der Ländername oder der Ländercode
        // zurückgeliefert wir

        //
        // geotag.getLatitude()
        // geotag.getLongitude()
        return geotag.toString();
    }

    /**
     * try's to determine the country/location of a given name or word
     * 
     * @param location
     *            the input name or word to determine the country/location as
     *            Sring
     * 
     * @param timeZone
     *            timezone delivered by twitter (use the name you get from
     *            twitter status object)
     * @return the code of the country/location on success and null else as
     *         String
     */
    public String getLocation(String location, String timezone) {
        // the placeholder "nope" is just for debugging and can be replaced by
        // null
        // look for matches in HashMap to avoid calling WebService
        if (location != null && map.containsKey(location.toLowerCase())) {
            return map.get(location.toLowerCase()) + " no WEBSERVICE";
        }

        String result = "nope";
        String webServiceURL = "http://172.22.214.196/localhost/TweetLoc.asmx/getCountry?";
        if (location == null && timezone == null) {
            return null;
        }
        if (location != null) {
            location = location.replace(' ', '+');
        }
        if (timezone != null) {
            timezone = timezone.replace(' ', '+');
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
        } catch (MalformedURLException e) {
            // return null;
        } catch (IOException e) {
            // return null;
        }
        if (!result.equals("nope")) {
            // position of the country code
            result = result.substring(75, 78);
        }
        if (result.equals("nope")) {
            return null;
        }
        return result.trim();
    }

}
