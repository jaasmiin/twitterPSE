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
 * @version 1.0
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
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
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

        try {
            connect();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        Statement s = null;
        try {
            s = c.createStatement();
            s.executeUpdate(sql);
        } catch (SQLException e) {
            logger.warning("Couldn't execute sql query\n" + e.getMessage());
        } finally {
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
