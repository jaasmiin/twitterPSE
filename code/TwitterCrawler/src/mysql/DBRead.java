package mysql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Stack;
import java.util.logging.Logger;

/**
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
    public long[] getNonVerifiedAccounts() throws SQLException {
        String sqlCommand = "SELECT AccountId FROM accounts WHERE Verified = 0";
        Statement s = c.createStatement();
        ResultSet res = s.executeQuery(sqlCommand);

        Stack<Integer> st = new Stack<Integer>();
        while (res.next()) {
            st.push(res.getInt("AccountId"));
        }
        long[] ret = new long[st.size()];
        for (int i = 0; i < st.size(); i++) {
            ret[i] = (long) st.pop();
        }
        return ret;
    }

}
