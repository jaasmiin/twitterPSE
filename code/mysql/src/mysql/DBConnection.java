package mysql;

import java.sql.Connection;
import java.util.logging.Logger;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * class to connect to a MySQL database
 * 
 * @author Holger Ebhart
 * @version 1.1
 * 
 */
public abstract class DBConnection {

    private final AccessData accessData;
    protected Connection c;
    protected DateFormat dateFormat;
    protected Logger logger;
    private boolean connected;

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
        this.connected = false;

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
        connected = false;
        // connect to database
        c = DriverManager.getConnection(accessData.getConnectionString(),
                accessData.getUser(), accessData.getPassword());
        connected = true;
        // logger.info("Connected to database " + accessData.getName()
        // + " with user " + accessData.getUser());
    }

    /**
     * cuts the connection to the database off
     */
    public void disconnect() {
        try {
            c.close();
            // logger.info("Disonnected from database " + accessData.getName()
            // + " with user " + accessData.getUser());
        } catch (SQLException e) {
            logger.warning("SQL-Status: " + e.getSQLState() + "\nMessage: "
                    + e.getMessage() + "\n");
        } finally {
            connected = false;
        }
    }

    /**
     * returns true if a database-connection is established, false if there's no
     * database-connection
     * 
     * @return true if a database-connection is established, false if there's no
     *         database-connection
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * executes the update method of a PreparedStatement
     * 
     * @param stmt
     *            the statement to execute as update as PreparedStatement
     * @return true if the update on the database was successfully, else false
     */
    protected boolean executeStatementUpdate(PreparedStatement stmt,
            boolean resultNotNull) {

        if (stmt == null) {
            return false;
        }

        boolean ret = false;
        try {
            if (resultNotNull) {
                ret = stmt.executeUpdate() != 0 ? true : false;
            } else {
                ret = stmt.executeUpdate() >= 0 ? true : false;
            }
        } catch (SQLException e) {
            //e.printStackTrace();
            sqlExceptionLog(e, stmt);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    sqlExceptionLog(e);
                }
            }
        }
        return ret;
    }

    /**
     * closes a Statement and it's result
     * 
     * @param stmt
     *            the statement to close as Statement
     * @param result
     *            the result to close as ResultSet
     */
    protected void closeResultAndStatement(Statement stmt, ResultSet result) {
        if (result != null) {
            try {
                result.close();
            } catch (SQLException e) {
                sqlExceptionLog(e);
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                sqlExceptionLog(e);
            }
        }
    }

    /**
     * logs an SQLException
     * 
     * @param e
     *            the SQLException to log
     */
    protected void sqlExceptionLog(SQLException e) {
        logger.warning("SQL-Exception: SQL-Status: " + e.getSQLState()
                + "\n Message: " + e.getMessage());
    }

    /**
     * logs an SQLException that has been thrown by reading the resultset
     * 
     * @param e
     *            the SQLException to log
     */
    protected void sqlExceptionResultLog(SQLException e) {
        logger.warning("Couldn't read sql result: \n" + e.getMessage());
    }

    /**
     * logs an SQLException with the responsible statement
     * 
     * @param e
     *            the SQLException to log
     * @param statement
     *            the statement that causes the exception as Statement
     */
    protected void sqlExceptionLog(SQLException e, Statement statement) {
        logger.warning("Couldn't execute sql query! SQL-Status: "
                + e.getSQLState() + "\n Message: " + e.getMessage()
                + "\n SQL-Query: " + statement.toString() + "\n");
    }

}
