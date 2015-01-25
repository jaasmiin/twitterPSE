package categorizer;

import java.io.IOException;
import java.util.logging.Logger;

import util.LoggerUtil;
import mysql.AccessData;
import mysql.DBcategorizer;

/**
 * this class instantiates the categorizer
 * 
 * @author Paul Jungeblut
 */
public class CategorizerMain {
    public static void main(String[] args) {
        AccessData accData = new AccessData(args[0], args[1], args[2], args[3], args[4]);
        Logger logger;
        DBcategorizer db;
        
        try {
            logger = LoggerUtil.getLogger();
        } catch (IOException e) {
            System.out.println("Could not instantiate a logger! Aborting");
            return;
        }
        
        try {
            db = new DBcategorizer(accData, logger);
        } catch(ClassNotFoundException | IllegalAccessException | InstantiationException a) {
            logger.warning("Could not establish a mysql connection.");
            return;
        }
        
        //instantiate the categorizer
        Categorizer categorizer = new Categorizer(db);
        categorizer.start();
    }
}
