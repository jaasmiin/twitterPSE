
package unfolding;

/**
 * 
 * @author Lidia
 * @version 1.0
 *
 */

public class MyDataEntry {

    private String countryName;
    private String countryId3Chars;
    private String countryId2Chars;
    private double value;
    
    /**
     * the number of retweets in this land
     */
    private int retweetsLand; 
    
    /**
     * the number of retweets in this land in the chosen filter combination
     */
    private int retweetsLandFiltered;
    
    /**
     * Constructor.
     */
    public MyDataEntry() {
        this(0, "", 0, 0);
    }
    
    /**
     * Constructor setting the specified parameters.
     * @param value (relative) of the country
     * @param countryName
     * @param retweetsLand
     * @param retweetsLandFiltered
     */
    public MyDataEntry(double value, String countryName, int retweetsLand, int retweetsLandFiltered) {
        this.value = value;
        this.countryName = countryName;
        this.retweetsLand = retweetsLand;
        this.retweetsLandFiltered = retweetsLandFiltered;
    }
    
    /**
     * Returns name of the country
     * @return countryName
     */
    public String getCountryName() {
        return countryName;
    }
    
    /**
     * Sets the name of the country
     * @param countryName name of the country
     */
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    /**
     * Returns country id (3 chars)
     * @return countryId3Chars
     */
    public String getCountryId3Chars() {
        return countryId3Chars;
    }
    
    /**
     * Returns country id (2 chars)
     * @return countryId2Chars
     */
    public String getCountryId2Chars() {
        return countryId2Chars;
    }

    /**
     * Sets 3 char country id
     * @param countryId 3 char id of the country
     */
    public void setCountryId3Chars(String countryId) {
        this.countryId3Chars = countryId;
    }
    
    /**
     * Sets 2 char country id
     * @param countryId 2 char id of the country
     */
    public void setCountryId2Chars(String countryId) {
        this.countryId2Chars = countryId;
    }

    /**
     * Returns (relative) value of the MyDataEntry
     * @return value of MyDataEntry
     */
    public Double getValue() {
        return value;
    }

    /**
     * Sets the value of the MyDataEntry
     * @param d value to be set
     */
    public void setValue(double d) {
        this.value = d;
    }
    
    /**
     * returns the number of retweets in this land
     * @return the number of retweets in this land
     */
    public int getRetweetsLand() {
        return retweetsLand;
    }
    
    /**
     * sets the number of retweets in this land
     * @param retweetsLand the number of retweets in this land
     */
    public void setRetweetsLand(int retweetsLand) {
        this.retweetsLand = retweetsLand;
    }
    
    /**
     * returns the number of retweets in this land with the chosen filter
     * @return the number of retweets in this land with the chosen filter
     */
    public int getRetweetsLandFiltered() {
        return retweetsLandFiltered;
    }
    
    /**
     * sets the number of retweets in this land with the chosen filter
     * @param retweetsLandFiltered the number of retweets in this land with the chosen filter
     */
    public void setRetweetsLandFiltered(int retweetsLandFiltered) {
        this.retweetsLandFiltered = retweetsLandFiltered;
    }
}

