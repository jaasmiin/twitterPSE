package categorizer;

import java.util.List;

import mysql.DBcategorizer;
import mysql.result.Account;

/**
 * categorizes the twitter accounts in our database
 * with the categories given by the DMOZ.org project
 * 
 * @author Paul Jungeblut
 */
public class Categorizer {
    //the db connection
    private DBcategorizer db;
    
    /**
     * initializes the categorizer by storing the database connection
     * 
     * @param db the database connection, must not be null
     */
    public Categorizer(DBcategorizer db) {
        if (db == null) {
            System.out.println("Please give a valid Dbcategorizer instance!");
        }
        
        this.db = db;
    }
    
    /**
     * does the actual categorization
     * gets a list of uncategorized accounts, looks for categories
     * and writes them into the db
     */
    public void start() {
        List<Account> accounts = db.getNonCategorized();
        System.out.println("Number of uncategorized accounts: " + accounts.size());
        for (Account account : accounts) {
            String url = account.getUrl();
            List<Integer> categories = db.getCategoriesForAccount(url);
            
            for (Integer category : categories) {
                db.addCategoryToAccount(account.getId(), category);
            }
            System.out.println("Categorized " + account.getName());
        }
    }
}
