package categorizer;

import java.sql.SQLException;
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
     * and opening the mysql connection
     * 
     * @param db the database connection, must not be null
     */
    public Categorizer(DBcategorizer db) {
        if (db == null) {
            System.out.println("Please give a valid Dbcategorizer instance!");
        }
        
        this.db = db;
        
        try {
            this.db.connect();
        } catch(SQLException e) {
            System.out.println("Could not open a mysql connection: " + e.getMessage());
        }
    }
    
    /**
     * does the actual categorization
     * gets a list of uncategorized accounts, looks for categories
     * and writes them into the db
     */
    public void start() {
        List<Account> accounts = db.getNonCategorized();
        for (Account account : accounts) {
            System.out.println("Categorize " + account.getUrl());
            String url = account.getUrl();
            
            if (url == null) continue;
            url = normalizeUrl(url);
            
            List<Integer> categories = db.getCategoriesForAccount(url);
            for (Integer category : categories) {
                System.out.println("   " + category);
                db.addCategoryToAccount(account.getId(), category);
            }
        }
    }
    
    private String normalizeUrl(String url) {
        // / at the end
        if (url.charAt(url.length() - 1) == '/') {
            url = url.substring(0, url.length() - 1);
        }
        
        //http or https
        if (url.startsWith("http://")) {
            url = url.substring(7);
        }
        if (url.startsWith("https://")) {
            url = url.substring(8);
        }
        
        //www
        if (url.startsWith("www.")) {
            url = url.substring(4);
        }
        
        return url;
    }
}
