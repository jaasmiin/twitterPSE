package mysql.result;

/**
 * stores a location from the database
 * 
 * @author Holger Ebhart
 */
public class Location extends Result {

    private String name;
    private String locCode;

    /**
     * store a location from the database
     * 
     * @param id
     *            the id of the location in the database as int
     * @param name
     *            the name of the location as String
     * @param locCode
     *            the code (3 characters) of the location (eg. GER) as String
     */
    public Location(int id, String name, String locCode) {
        super(id);
        this.name = name == null ? "" : name;
        this.locCode = locCode == null ? "" : locCode;
    }

    /**
     * returns the location code of the location (3 characters)
     * 
     * @return the location code of the location (3 characters) as String
     */
    public String getLocationCode() {
        return locCode;
    }

    /**
     * Returns the name of the category
     * 
     * @return the name of the category as string
     */
    @Override
    public String toString() {
        return name.isEmpty() ? locCode : name;
    }

    @Override
    public boolean equals(Object o) {
        boolean equal = false;
        if (o != null && o instanceof Location) {
            equal = ((Result) o).getId() == getId();
        }
        return equal;
    }
}
