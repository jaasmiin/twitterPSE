package mysql.result;

/**
 * class to store the database-id's of the results
 * 
 * @author Holger Ebhart
 */
public abstract class Result {

    private int id;

    /**
     * store the database id of the entry
     * 
     * @param id
     *            the id of the database-entry
     */
    public Result(int id) {
        this.id = id;
    }

    /**
     * returns the id of the database-entry
     * 
     * @return the id of the database-entry as int
     */
    public int getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return getId();
    }
}
