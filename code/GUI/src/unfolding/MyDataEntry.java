package unfolding;


/**
 * 
 * @author Lidia
 *
 */

public class MyDataEntry {

    private String countryName;
    private String countryId3Chars;
    private String countryId2Chars;
    private Double value;
    
    public MyDataEntry() {
        this.value = (double) -1;
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
        return countryId3Chars;
    }

    public void setCountryId3Chars(String countryId) {
        this.countryId2Chars = countryId;
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
}
