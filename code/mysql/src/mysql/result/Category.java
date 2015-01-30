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
    private boolean used;

    /**
     * store the categories of an account
     * 
     * @param id
     *            the id of the category as int
     * 
     * @param name
     *            the name of the category as String
     * @param parentId
     *            the parentId of this category as int (0 if root)
     * @param used
     *            true if this category is mapped at minimum to one account,
     *            false otherwise
     */
    public Category(int id, String name, int parentId, boolean used) {
        super(id);
        childs = new ArrayList<Category>();
        this.parent = parentId;
        if (name.length() > MAX_LENGTH) {
            name = name.substring(0, MAX_LENGTH - 1);
        }
        this.category = name;
        this.used = used;
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
    @Deprecated
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

    /**
     * True if an account is associated with this category, false otherwise.
     * 
     * @return true if this category is mapped at minimum to one account, else
     *         false
     */
    public boolean isUsed() {
        return used;
    }

    /**
     * specifies whether this category is used as an account-category pair or not
     * 
     * @param used
     *            true if the category is used, false otherwise
     */
    public void setUsed(boolean used) {
        this.used = used;
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
