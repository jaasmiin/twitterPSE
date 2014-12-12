package locate;

import twitter4j.GeoLocation;

/**
 * class to locate words with a webservice
 * 
 * @author Holger Ebhart
 * @version 1.0
 * 
 */
public class Locator {

    public Locator() {

    }

    // return {name , code}
    public String getLocation(GeoLocation geotag) {
        return geotag.toString();
    }

    // return {name, code}
    public String getLocation(String location) {
        return location;
    }

}
