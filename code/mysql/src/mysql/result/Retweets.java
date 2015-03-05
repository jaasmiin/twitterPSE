package mysql.result;

import java.sql.Date;

/**
 * store the retweets of a day or the total number of retweets
 * 
 * @author Holger Ebhart
 */
public class Retweets extends Tweets {

    private String locationCode;

    /**
     * store the retweets of a day
     * 
     * @param date
     *            the date of the retweets as Date
     * @param counter
     *            the number of localized retweets as int
     * @param locationCode
     *            the code of the location of this retweets as String (max. 3
     *            chars)
     */
    public Retweets(Date date, int counter, String locationCode) {
        super(date, counter);
        this.locationCode = "";
        setLocationCode(locationCode);
    }

    /**
     * returns the location code of this retweets
     * 
     * @return the location code of this retweets as String (max. 3 chars)
     */
    public String getLocationCode() {
        return locationCode;
    }

    /**
     * sets the location code of the location of this retweets
     * 
     * @param locationCode
     *            ths location code to set as String (max. 3 chars)
     */
    public void setLocationCode(String locationCode) {

        // check if the length of the code is smaller then 3, because of
        // database limit and because of ISO-Norm
        if (locationCode != null && locationCode.length() > 3) {
            this.locationCode = locationCode.substring(0, 3);
        } else {
            this.locationCode = locationCode;
        }
    }

}
