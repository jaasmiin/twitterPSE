package mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;
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

    private HashSet<String> categories;

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
        categories = new HashSet<String>();
    }

    @Override
    public List<Account> getNonCategorized() {

        String sqlCommand = "SELECT Id, URL FROM accounts WHERE Categorized = 0 LIMIT 100;";

        ResultSet result = null;
        Statement stmt = null;
        try {
            stmt = c.createStatement();
            result = stmt.executeQuery(sqlCommand);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
            return new ArrayList<Account>();
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
        } finally {
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

        return ret;
    }

    @Override
    public boolean addCategoryToAccount(int accountId, Category category) {

        // add categories with hashtable lookup
        boolean result1 = false;
        Category temp = category;

        // add categories in reverse order to working-stack
        Stack<Category> stack = new Stack<Category>();
        while (temp != null) {
            stack.push(temp);
            temp = temp.getParent();
        }

        while (stack.size() > 0) {

            temp = stack.pop();

            // hashtable lookup
            if (categories.contains(temp.toString())) {
                result1 = true;
            } else {

                // prevent SQL-injection
                PreparedStatement stmt = null;
                try {

                    if (temp.getParent() == null) {
                        stmt = c.prepareStatement("INSERT IGNORE INTO category (Name, ParentId) VALUES "
                                + "(?, ?);");
                        stmt.setNull(2, Types.INTEGER);
                    } else {
                        // set parent
                        stmt = c.prepareStatement("INSERT IGNORE INTO category (Name, ParentId) SELECT "
                                + "?, Id FROM category WHERE Name = ?;");
                        stmt.setString(2, temp.getParent().toString());
                    }
                    stmt.setString(1, temp.toString());

                    result1 = stmt.executeUpdate() >= 0 ? true : false;
                } catch (SQLException e) {
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

                // on success
                if (result1) {
                    categories.add(temp.toString());
                }
            }
        }

        // add category to account
        boolean result2 = false;
        if (result1) {
            // prevent SQL-injection
            PreparedStatement stmt = null;
            try {
                stmt = c.prepareStatement("INSERT IGNORE INTO accountCategory (AccountId, CategoryId) VALUES (?, (SELECT Id FROM category WHERE Name = ? LIMIT 1));");
                stmt.setInt(1, accountId);
                stmt.setString(2, category.toString());
                result2 = stmt.executeUpdate() >= 0 ? true : false;
            } catch (SQLException e) {
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
        }

        // set categorized = true
        String sqlCommand = "UPDATE accounts SET categorized = 1 WHERE Id = "
                + accountId + ";";

        boolean result3 = false;
        if (result2) {
            Statement stmt = null;
            try {
                stmt = c.createStatement();
                result3 = stmt.executeUpdate(sqlCommand) >= 0 ? true : false;
            } catch (SQLException e) {
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
        }

        return result1 && result2 && result3;
    }

    @Override
    public List<Integer> getCategoriesForAccount(String url) {

        ResultSet result = null;
        PreparedStatement stmt = null;
        try {
            stmt = c.prepareStatement("SELECT CategoryId FROM page WHERE Page LIKE ? LIMIT 100;");
            stmt.setString(1, url);
            result = stmt.executeQuery();
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
            return new ArrayList<Integer>();
        }

        List<Integer> ret = new ArrayList<Integer>();
        try {
            while (result.next()) {
                ret.add(result.getInt(1));
            }
        } catch (SQLException e) {
            logger.warning("Couldn't read sql result\n" + e.getMessage());
            ret.remove(ret.size() - 1);
        } finally {
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

        return ret;
    }

    private void sqlExceptionLog(SQLException e) {
        logger.warning("SQL-Exception: SQL-Status: " + e.getSQLState()
                + "\n Message: " + e.getMessage());
    }

    private void sqlExceptionLog(SQLException e, Statement statement) {
        logger.warning("Couldn't execute sql query! SQL-Status: "
                + e.getSQLState() + "\n Message: " + e.getMessage()
                + "\n SQL-Query: " + statement + "\n");
    }

}
