package categorizer;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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
            logger = getLogger();
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
    }
    
    private static Logger getLogger() throws SecurityException, IOException {
        Logger l = Logger.getLogger("logger");
        new File("LogFile.log").createNewFile();
        FileHandler fh = new FileHandler("LogFile.log", true);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
        l.addHandler(fh);
        // true: print output on console and into file
        // false: only store output in logFile
        l.setUseParentHandlers(false);
        return l;
    }
}
