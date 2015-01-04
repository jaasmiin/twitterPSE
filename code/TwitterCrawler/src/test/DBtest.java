package test;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import mysql.AccessData;
import mysql.DBConnection;

class DBtest extends DBConnection {

    public DBtest(AccessData accessData, Logger logger)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        super(accessData, logger);
    }

    public void sql(String sql) {

        try {
            connect();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        try {
            Statement s = c.createStatement();
            s.executeUpdate(sql);
        } catch (SQLException e) {
            logger.warning("Couldn't execute sql query\n" + e.getMessage());
        }
        disconnect();
    }
}
