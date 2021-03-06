package mysql;

import java.sql.Connection;
import java.util.logging.Logger;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * class to connect to a MySQL database
 * 
 * @author Holger Ebhart
 * @version 1.0
 * 
 */
public abstract class DBConnection {

    private final AccessData accessData;
    protected Connection c;
    protected DateFormat dateFormat;
    protected Logger logger;

    /**
     * configure the connection to the database
     * 
     * @param accessData
     *            the access data to the specified mysql-database as AccessData
     * @param logger
     *            a global logger for the whole program as Logger
     * @throws InstantiationException
     *             thrown if the mysql-driver hasn't been found
     * @throws IllegalAccessException
     *             thrown if the mysql-driver hasn't been found
     * @throws ClassNotFoundException
     *             thrown if the mysql-driver hasn't been found
     */
    public DBConnection(AccessData accessData, Logger logger)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {

        // load drivers
        Class.forName("org.gjt.mm.mysql.Driver").newInstance();

        this.accessData = accessData;
        this.logger = logger;

        // create date format for the database
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    /**
     * starts the connection to the database
     * 
     * @throws SQLException
     *             thrown if there is no connection to the database possible
     */
    public void connect() throws SQLException {
        // connect to database
        c = DriverManager.getConnection(accessData.getConnectionString(),
                accessData.getUser(), accessData.getPassword());
        logger.info("Connected to database " + accessData.getName()
                + " with user " + accessData.getUser());
    }

    /**
     * cuts the connection to the database off
     */
    public void disconnect() {
        try {
            c.close();
            logger.info("Disonnected from database " + accessData.getName()
                    + " with user " + accessData.getUser());
        } catch (SQLException e) {
            logger.warning("SQL-Status: " + e.getSQLState() + "\nMessage: "
                    + e.getMessage() + "\n");
        }
    }
}
