package mysql.result;

/**
 * store the categories of an account
 * 
 * @author Holger Ebhart
 * @version 1.0
 * 
 */
public class Category extends Result {

    private Category parent;
    private String category;

    /**
     * store the categories of an account
     * 
     * @param parent
     *            the parent-category if available as ResultCategory otherwise
     *            null
     * @param name
     *            the name of the category as String
     * @param id
     *            the id of the category as int
     */
    public Category(Category parent, String name, int id) {
        super(id);
        this.parent = parent;
        this.category = name;
    }

    /**
     * returns the parent-category
     * 
     * @return the parent-category as ResultCategory
     */
    public Category getParent() {
        return parent;
    }

    /**
     * returns the name of the category
     * 
     * @return the name of the category as String
     */
    public String getCategory() {
        return category;
    }

    // /**
    // * returns the id of the category
    // *
    // * @return the id of the category as int
    // */
    // public int getId();

}
