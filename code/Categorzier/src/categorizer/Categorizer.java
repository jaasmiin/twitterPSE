package categorizer;

import java.sql.SQLException;
import java.util.List;

import mysql.DBcategorizer;
import mysql.result.Account;

/**
 * categorizes the twitter accounts in our database with the categories given by
 * the DMOZ.org project
 * 
 * @author Paul Jungeblut
 */
public class Categorizer {
    
    /**
     * the database connection
     */
    private DBcategorizer db;

    /**
     * initializes the categorizer by storing the database connection and
     * opening the MySQL connection
     * 
     * @param db
     *            the database connection, must not be null
     */
    public Categorizer(DBcategorizer db) {
        if (db == null) {
            System.out.println("Please give a valid Dbcategorizer instance!");
        }

        this.db = db;

        try {
            this.db.connect();
        } catch (SQLException e) {
            System.out.println("Could not open a mysql connection: "
                    + e.getMessage());
        }
    }

    /**
     * does the actual categorization gets a list of uncategorized accounts,
     * looks for categories and writes them into the db
     * 
     * categorization goes by account name and account url
     */
    public void start() {
        List<Account> accounts = db.getNonCategorized();
        for (Account account : accounts) {
            System.out.println("Account: " + account.getName());

            // get the name and url
            String url = account.getUrl();
            String name = account.getName();
            if (url == null && name == null) {
                db.setCategorized(account.getId());
                continue;
            }
            if (url == null)
                url = "someStupidSequenceThatDoesNotOccur";
            // second condition should lower the number of false positives with
            // to many matches
            if (name == null || name.length() < 8)
                name = "someStupidSequenceThatDoesNotOccur";
            url = normalizeUrl(url);
            name = normalizeName(name);

            // find and insert each category
            List<Integer> categories = db.getCategoriesForAccount(url, name);
            for (Integer category : categories) {
                // get upwards in the category tree
                do {
                    System.out.println("     Category: " + category);
                    db.addCategoryToAccount(account.getId(),
                            category.intValue());
                    category = db.getParentId(category);
                } while (category != 0 && category != -1);
            }

            // could not find a category
            if (categories.size() == 0) {
                db.setCategorized(account.getId());
            }
        }
    }

    private String normalizeUrl(String url) {
        // remove / at the end
        if (url.length() != 0 && url.charAt(url.length() - 1) == '/') {
            url = url.substring(0, url.length() - 1);
        }

        // remove http:// or https://
        if (url.startsWith("http://")) {
            url = url.substring(7);
        }
        if (url.startsWith("https://")) {
            url = url.substring(8);
        }

        // remove www.
        if (url.startsWith("www.")) {
            url = url.substring(4);
        }

        return url;
    }

    private String normalizeName(String name) {
        // switch to lower case for case insensitive matching
        name = name.toLowerCase();

        // replace spaces, underscores with wildcrads (%)
        name = name.replace(' ', '%');
        name = name.replace('_', '%');

        return name;
    }
}
