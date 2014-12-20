package locate;

import java.util.Date;

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

    //
    //
    // ich liefer dir mal die Dates noch mit, falls du sie brauchst gut,
    // ansonsten werf sie raus
    //
    //

    /**
     * determine the country/location of given geo-coordinates
     * 
     * @param geotag
     *            the geo-coordinates as GeoLocation
     * @return the code/name of the country/location on success and null else as
     *         String
     */
    public String getLocation(GeoLocation geotag, Date date) {
        // Bitte entscheide ob der Ländername oder der Ländercode
        // zurückgeliefert wird

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
     * @return the code/name of the country/location on success and null else as
     *         String
     */
    public String getLocation(String location, Date date) {
        // Bitte entscheide ob der Ländername oder der Ländercode
        // zurückgeliefert wird

        return location == "" ? null : location;
    }

}
