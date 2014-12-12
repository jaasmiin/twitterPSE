package mysql.result;

/**
 * stores a location from the database
 * 
 * @author Holger Ebhart
 * @version 1.0
 */
public class ResultLocation extends Result {

    private String name;
    private String locCode;
    private ResultLocation parent;

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
    public ResultLocation(int id, String name, String locCode,
            ResultLocation parent) {
        super(id);
        this.name = name;
        this.locCode = locCode;
        this.parent = parent;
    }

    /**
     * returns the name of the category
     * 
     * @return the name of the category as String
     */
    public String getName() {
        return name;
    }

    /**
     * returns the parent location of this location
     * 
     * @return the parent location of this location as ResultLocation
     */
    public ResultLocation getParent() {
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

}
