package mysql.result;

import java.util.ArrayList;
import java.util.List;

/**
 * store the categories of an account
 * 
 * @author Holger Ebhart
 * 
 */
public class Category extends Result {

    private static final int MAX_LENGTH = 50;
    private List<Category> childs;
    private String category;
    private int parent;
    private boolean used;
    private int matchedAccounts;

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

        // check for max length of name, because of database limit
        if (name != null && name.length() > MAX_LENGTH) {
            name = name.substring(0, MAX_LENGTH);
        }
        this.category = name;
        this.used = used;
    }

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
     * @param matchedAccounts
     *            the number of Accounts that were inferior of this category
     */
    public Category(int id, String name, int parentId, boolean used,
            int matchedAccounts) {
        this(id, name, parentId, used);
        this.matchedAccounts = matchedAccounts;
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
     * Returns the name of the category.
     * 
     * @return the name of the category as string
     */
    public String getText() {
        return category;
    }

    /**
     * Returns the name of the category with the number of matches
     * 
     * @return the name of the category as String
     */
    @Override
    public String toString() {
        return category + " (" + Integer.toString(matchedAccounts) + ")";
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
     * returns the parentId of this category (0 if this category is root)
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
     * specifies whether this category is used as an account-category pair or
     * not
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
        if (o != null && o instanceof Category) {
            equal = ((Result) o).getId() == getId();
        }
        return equal;
    }

    /**
     * returns the number of Accounts that were inferior of this category
     * 
     * @return the number of Accounts that were inferior of this category as int
     */
    public int getMatchedAccounts() {
        return matchedAccounts;
    }

    /**
     * set the number of Accounts that were below this category
     * 
     * @param matchedAccounts
     *            the number of Accounts that were inferior of this category as
     *            int
     */
    public void setMatchedAccounts(int matchedAccounts) {
        this.matchedAccounts = matchedAccounts;
    }

}
