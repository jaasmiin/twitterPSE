package mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import mysql.result.Account;
import mysql.result.Category;

/**
 * class to address a database with the categorizer
 * 
 * @author Holger Ebhart
 * @version 1.0
 * 
 */
public class DBcategorizer extends DBConnection implements DBIcategorizer {

    /**
     * configure the connection to the database
     * 
     * @param accessData
     *            the access data to the specified mysql-database as AccessData
     * @param logger
     *            a global logger for the whole program as Logger
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public DBcategorizer(AccessData accessData, Logger logger)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        super(accessData, logger);
    }

    @Override
    public List<Account> getNonCategorized() {

        String sqlCommand = "SELECT (Id,URL) FROM accounts WHERE Categorized = 0 LIMIT 100;";

        ResultSet result = null;
        try {
            Statement s = c.createStatement();
            result = s.executeQuery(sqlCommand);
        } catch (SQLException e) {
            logger.warning("Couldn't execute sql query\n" + e.getMessage());
            return null;
        }

        List<Account> ret = new ArrayList<Account>();
        try {
            while (result.next()) {
                ret.add(new Account(result.getInt("Id"), result
                        .getString("URL")));
            }
        } catch (SQLException e) {
            logger.warning("Couldn't read sql result\n" + e.getMessage());
            ret.remove(ret.size() - 1);
        }

        return ret;
    }

    @Override
    public boolean addCategoryToAccount(int accountId, Category category) {
        // TODO Auto-generated method stub

        // add category to account

        // set categorized = true

        return false;
    }

}
