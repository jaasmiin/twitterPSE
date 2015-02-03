package locate;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

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

    private static final String DEFAULT_LOCATION = "0";
    private HashMap<String, String> map;
    private Logger logger;
    private int numberOfReq = 0;
    private int numberOfLocReq = 0;
    private int numberOfPlaceLoc = 0;
    private int numberOfGeoTagLoc = 0;
    private int numberOfHashMapLoc = 0;
    private int reqWithPlace = 0;
    private int reqWithGeoTag = 0;
    private int reqWithLocation = 0;

    /**
     * 
     * Initiates a new instance of 'Locator'
     * 
     * @param map
     *            Reference to global HashMap to lookup already located
     *            locations
     * @param logger
     *            Logger to log all remarkable events
     * @throws IllegalArgumentException
     *             Is thrown if map == null.
     */
    public Locator(HashMap<String, String> map, Logger logger)
            throws IllegalArgumentException {

        this.logger = logger;
        if (map == null) {
            logger.severe("HashMap reference is null");
            throw new IllegalArgumentException("HashMap reference is null");
        }
        this.map = map;
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
    private String getLocation(GeoLocation geotag) {

        String res = DEFAULT_LOCATION;
        WebService.setGeoNamesServerFailover(null);
        WebService.setUserName("KIT_PSE");
        WebService.setConnectTimeOut(1000);
        try {
            res = WebService.countryCode(geotag.getLatitude(),
                    geotag.getLongitude());
        } catch (GeoNamesException e1) {
            logger.info("Geotag localiser: Geonames Excep: " + e1.getMessage());
            return DEFAULT_LOCATION;
        } catch (IOException e2) {
            logger.info("Geotag localiser: IO exeption: " + e2.getMessage());
            return DEFAULT_LOCATION;
        }
        return res;
    }

    /**
     * tries to determine the country/location of a given name or word via
     * HashMap
     * 
     * @param location
     *            the input name or word to determine the country/location as
     *            String
     * 
     * @return the code of the country/location on success and "0" otherwise as
     *         String
     */
    private String getLocation(String location, String timezone) {
        String result = DEFAULT_LOCATION;
        location = Formatter.formatString(location, logger);
        timezone = Formatter.formatString(timezone, logger);
        String query = location + "#" + timezone;
        if (map.containsKey(query)) {
            result = map.get(query);
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
     * @param timezone
     *            String containing information about the timezone of a 'tweet'
     * @return countrycode if 'Tweet' could be located, "0" otherwise
     */
    public String locate(Place place, GeoLocation geotag, String location,
            String timezone) {

        String result = DEFAULT_LOCATION;

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
            if (!result.equals(DEFAULT_LOCATION)) {

                numberOfPlaceLoc++;
            }

        } else if (geotag != null) {

            result = getLocation(geotag);
            if (!result.equals(DEFAULT_LOCATION)) {

                numberOfGeoTagLoc++;
            }

        } else if (location != null) {

            result = getLocation(location, timezone);
            if (!result.equals(DEFAULT_LOCATION)) {

                numberOfHashMapLoc++;
            }
        }

        if (!result.equals(DEFAULT_LOCATION)) {

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
     *         number of requests located via hashmap 5: number of requests
     *         containing place-attribute 6: number of requests containing
     *         geotag 7: number of request containing location information
     */
    public int[] getStatistic() {
        int[] statistics = {numberOfReq, numberOfLocReq, numberOfPlaceLoc,
                numberOfGeoTagLoc, numberOfHashMapLoc, reqWithPlace,
                reqWithGeoTag, reqWithLocation };
        return statistics;

    }

}