package unfolding;


/**
 * 
 * @author Lidia
 *
 */

public class DataEntry {

    private String countryName;
    private String countryId;
    private float value;
    
    public DataEntry() {
        this.value = -1;
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

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }    
}
