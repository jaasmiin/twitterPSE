package mysql.result;

import java.util.ArrayList;
import java.util.List;

/**
 * store the categories of an account
 * 
 * @author Holger Ebhart
 * @version 1.1
 * 
 */
public class Category extends Result {

    private final static int MAX_LENGTH = 50;
    private List<Category> childs;
    private String category;
    private int parent;

    /**
     * store the categories of an account
     * 
     * @param id
     *            the id of the category as int
     * 
     * @param name
     *            the name of the category as String
     * @param the
     *            parentId of this category as int (0 if root)
     */
    public Category(int id, String name, int parentId) {
        super(id);
        childs = new ArrayList<Category>();
        this.parent = parentId;
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
    public List<Category> getChilds() {
        return childs;
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
     * adds a new child category of this category
     * 
     * @param childCategory
     *            the child category to add as Category
     */
    public void addChild(Category childCategory) {
        childs.add(childCategory);
    }

    /**
     * returns the parentId of this category
     * 
     * @return the parentId of this category as int
     */
    public int getParentId() {
        return parent;
    }
}
