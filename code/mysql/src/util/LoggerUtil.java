package util;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * 
 * util class to handle loggers and to get an instance of them
 * 
 * @author Holger Ebhart
 * 
 */
public class LoggerUtil {

    /**
     * returns an instance of a logger that writes logs in the file LogFile.log
     * 
     * @param filename
     *            the filename of the logfile as String without the ending .log
     * @return an instance of a logger that writes logs in the file LogFile.log
     *         as Logger
     * @throws SecurityException
     *             thrown if the access-permissions weren't sufficient
     * @throws IOException
     *             thrown if an error with the filesystems occurs
     */
    public static Logger getLogger(String filename) throws SecurityException,
            IOException {

        // clear directory
        cleanUp(filename);

        // get logger instance
        Logger l = Logger.getLogger(filename);

        int success = 0;
        do {
            try {
                String name = filename + (success == 0 ? "" : success);

                // get/create logfile
                new File(name + ".log").createNewFile();
                FileHandler fh = new FileHandler(name + ".log", true);
                SimpleFormatter formatter = new SimpleFormatter();
                // set format of the logs
                fh.setFormatter(formatter);
                // set logfile
                l.addHandler(fh);
                // true: print output on console and into file
                // false: only store output in logFile
                l.setUseParentHandlers(false);

                success = 0;
            } catch (AccessDeniedException e) {
                success++;
            }

        } while (success > 0 && success < 10);

        return l;
    }

    /**
     * returns an instance of a logger that writes logs in the file LogFile.log
     * 
     * @return an instance of a logger that writes logs in the file LogFile.log
     *         as Logger
     * @throws SecurityException
     *             thrown if the access-permissions weren't sufficient
     * @throws IOException
     *             thrown if an error with the filesystems occurs
     */
    public static Logger getLogger() throws SecurityException, IOException {
        return getLogger("LogFile");
    }

    // deletes all temporary log-files
    private static void cleanUp(String filename) {
        File directory = new File(".");
        File[] files = directory.listFiles();

        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()
                    && files[i].getName().startsWith(filename + ".log.")) {
                files[i].delete();
            }
        }
    }

}