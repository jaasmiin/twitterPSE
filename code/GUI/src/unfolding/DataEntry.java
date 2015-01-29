package unfolding;


/**
 * 
 * @author Lidia
 *
 */

public class DataEntry {

    private String countryName;
    private String countryId;
    private Double value;
    
    public DataEntry() {
        this.value = (double) -1;
    }
    
    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double double1) {
        this.value = double1;
    }    
}
