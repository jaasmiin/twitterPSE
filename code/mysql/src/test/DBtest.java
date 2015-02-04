package test;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import mysql.AccessData;
import mysql.DBConnection;

/**
 * class to execute custom sql-queries on the database
 * 
 * @author Holger Ebhart
 */
class DBtest extends DBConnection {

    /**
     * creates an object to execute custom sql-queries
     * 
     * @param accessData
     *            the accessdata for the database as AccessData
     * @param logger
     *            a logger to log exceptions as Logger
     * @throws InstantiationException
     *             thrown if a connection to the database is impossible
     * @throws IllegalAccessException
     *             thrown if a connection to the database is impossible
     * @throws ClassNotFoundException
     *             thrown if a connection to the database is impossible, due to
     *             a missing driver-class
     */
    public DBtest(AccessData accessData, Logger logger)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        super(accessData, logger);
    }

    /**
     * executes an sql command, but returns no result
     * 
     * @param sql
     *            the complete sql command as String
     */
    public void sql(String sql) {

        // try to connect to database
        try {
            connect();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        Statement s = null;
        try {
            // execute the sql statement
            s = c.createStatement();
            s.executeUpdate(sql);
        } catch (SQLException e) {
            logger.warning("Couldn't execute sql query\n" + e.getMessage());
        } finally {
            // close the sql statement
            if (s != null) {
                try {
                    s.close();
                } catch (SQLException e) {
                    logger.warning("Couldn't execute sql query\n"
                            + e.getMessage());
                }
            }
        }
        disconnect();
    }
}
