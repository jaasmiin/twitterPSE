package mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Stack;
import java.util.logging.Logger;

/**
 * class to address a database with read-only Access
 * 
 * @author Holger Ebhart
 * @version 1.0
 * 
 */
public class DBRead extends DBConnection implements Read {

    /**
     * configurate the connection to the database
     * 
     * @param accessData
     *            the access data to the specified mysql-database as AccessData
     * @param logger
     *            a global logger for the whole program as Logger
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public DBRead(AccessData accessData, Logger logger)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        super(accessData, logger);
    }

    @Override
    public long[] getNonVerifiedAccounts() {
        String sqlCommand = "SELECT AccountId FROM accounts WHERE Verified = 0";

        ResultSet res = null;
        try {
            Statement s = c.createStatement();
            res = s.executeQuery(sqlCommand);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        Stack<Integer> st = new Stack<Integer>();
        try {
            while (res.next()) {
                st.push(res.getInt("AccountId"));
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        long[] ret = new long[st.size()];
        for (int i = 0; i < st.size(); i++) {
            ret[i] = (long) st.pop();
        }
        return ret;
    }

    @Override
    public Result[] getNonCategorized() {
        // TODO Auto-generated method stub
        return null;
    }

}
