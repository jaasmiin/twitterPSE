package locate;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import mysql.AccessData;
import mysql.DBIcrawler;
import mysql.DBcrawler;

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

    private ConcurrentHashMap<String, String> map;
    private boolean updateHashMap = false;
    private Logger logger;
    private Formatter formatter;
    private DBIcrawler dbc;
    private int numberOfReq = 0;
    private int numberOfLocReq = 0;
    private int numberOfPlaceLoc = 0;
    private int numberOfGeoTagLoc = 0;
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
    public Locator(AccessData accessData, Logger logger) throws InstantiationException {
    	
        this.logger = logger;
        formatter = new Formatter(logger);
        
        try {
        	
            this.dbc = new DBcrawler(accessData, logger);
         
        } catch (IllegalAccessException | ClassNotFoundException | SQLException e) {
            dbc = null;
            logger.severe(e.getMessage() + "\n");
            throw new InstantiationException(
                    "Not able to instantiate Databaseconnection.");
        }
       
        map = getHashMap();
    }
    
    private ConcurrentHashMap<String,String> getHashMap() {
    	return dbc.getLocationStrings();
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
     * tries to determine the country/location of a given name or word via HashMap
     * 
     * @param location
     *            the input name or word to determine the country/location as
     *            String

     * @return the code of the country/location on success and "0" otherwise as
     *         String
     */
    private String getLocation(String location) {
    	String result = "0";
    	location = formatter.formatString(location);
    	
    	if(map.containsKey(location)) {
    		result = map.get(location);
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
        if (updateHashMap) {
        	map = getHashMap();
        }
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

        } else if (location != null) {
            
          result = getLocation(location);
             if (!result.equals("0")) {
            	 
            	 numberOfHashMapLoc++;
             }
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
     *         place-attribute 3: number of requests located via geotag 4: number of requests located
     *         via hashmap 5: number of requests containing place-attribute 6:
     *         number of requests containing geotag 7: number of request
     *         containing location information
     */
    public int[] getStatistic() {
        int[] statistics = {numberOfReq, numberOfLocReq, numberOfPlaceLoc,
                numberOfGeoTagLoc, numberOfHashMapLoc, reqWithPlace,
                reqWithGeoTag, reqWithLocation };
        return statistics;

    }

}
/*
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
*/