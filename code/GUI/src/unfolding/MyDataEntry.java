
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
    
    public MyDataEntry() {
        this(-1, "", -1, -1);
    }
    
    public MyDataEntry(double value, String countryName, int retweetsLand, int retweetsLandFiltered) {
        this.value = value;
        this.countryName = countryName;
        this.retweetsLand = retweetsLand;
        this.retweetsLandFiltered = retweetsLandFiltered;
    }
    
    public String getCountryName() {
        return countryName;
    }
    
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryId3Chars() {
        return countryId3Chars;
    }
    
    public String getCountryId2Chars() {
        return countryId2Chars;
    }

    public void setCountryId3Chars(String countryId) {
        this.countryId3Chars = countryId;
    }
    
    public void setCountryId2Chars(String countryId) {
        this.countryId2Chars = countryId;
    }

    public Double getValue() {
        return value;
    }

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

