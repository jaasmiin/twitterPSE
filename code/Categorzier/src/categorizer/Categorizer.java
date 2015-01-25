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
    public Categorizer(DBcategorizer db) {
        if (db == null) {
            System.out.println("ERROR");
        }
        
        List<Account> accounts = db.getNonCategorized();
        for (Account account : accounts) {
            String url = account.getUrl();
            List<Integer> categories = db.getCategoriesForAccount(url);
            
            for (Integer category : categories) {
                db.addCategoryToAccount(account.getId(), category);
            }
        }
    }
}
