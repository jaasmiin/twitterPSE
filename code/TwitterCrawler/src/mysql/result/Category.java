package mysql.result;

/**
 * store the categories of an account
 * 
 * @author Holger Ebhart
 * @version 1.0
 * 
 */
public class Category extends Result {

    private final static int MAX_LENGTH = 50;
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
        if (name.length() > MAX_LENGTH) {
            name = name.substring(0, MAX_LENGTH - 1);
        }
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
     * Returns the name of the category
     * 
     * @return the name of the category as string
     * @deprecated replaced by {@link #toString()}
     */
    public String getCategory() {
        return category;
    }

    /**
     * Returns the name of the category
     * 
     * @return the name of the category as String
     */
    @Override
    public String toString() {
        return category;
    }

    /**
     * set the parent category of this category
     * 
     * @param parentCategory
     *            the parent category as Category
     */
    public void setParent(Category parentCategory) {
        parent = parentCategory;
    }
}
