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
     * @param id
     *            the id of the category as int
     * 
     * @param name
     *            the name of the category as String
     * @param parent
     *            the parent-category if available as ResultCategory otherwise
     *            null
     */
    public Category(int id, String name, Category parent) {
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
}
