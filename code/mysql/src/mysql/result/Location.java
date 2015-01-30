package mysql.result;

/**
 * stores a location from the database
 * 
 * @author Holger Ebhart
 * @version 1.0
 */
public class Location extends Result {

    private String name;
    private String locCode;
    private Location parent;

    /**
     * store a location from the database
     * 
     * @param id
     *            the id of the location in the database as int
     * @param name
     *            the name of the location as String
     * @param locCode
     *            the code (3 characters) of the location (eg. GER) as String
     * @param parent
     *            the parent location of this location as ResultLocation
     */
    public Location(int id, String name, String locCode, Location parent) {
        super(id);
        this.name = name;
        this.locCode = locCode;
        this.parent = parent;
    }

    /**
     * Returns the name of the category
     * 
     * @return the name of the category as String
     * @deprecated replaced by {@link #toString()}
     */
    public String getName() {
        return name;
    }

    /**
     * returns the parent location of this location
     * 
     * @return the parent location of this location as ResultLocation
     */
    public Location getParent() {
        return parent;
    }

    /**
     * returns the location code of the location (3 characters)
     * 
     * @return the location code of the location (3 characters) as String
     */
    public String getLocCode() {
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

    /**
     * set the parent location of this location
     * 
     * @param parentLocation
     *            the parent location as Location
     */
    public void setParent(Location parentLocation) {
        parent = parentLocation;
    }
    
    @Override
    public boolean equals(Object o) {
    	boolean equal = false;
		if (o != null && o.getClass() == this.getClass()) {
			equal = ((Result) o).getId() == getId();
		} 
    	return equal;
    }
}
