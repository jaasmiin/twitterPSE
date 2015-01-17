package mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.logging.Logger;

import twitter4j.User;
import mysql.result.Account;
import mysql.result.Category;
import mysql.result.Location;
import mysql.result.Retweets;
import mysql.result.Tweets;
import mysql.result.TweetsAndRetweets;

/**
 * class to modify the database restricted and to get data from the database
 * 
 * @author Holger Ebhart
 * @version 1.1
 * 
 */
public class DBgui extends DBConnection implements DBIgui {

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
    public DBgui(AccessData accessData, Logger logger)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        super(accessData, logger);
    }

    @Override
    public List<Category> getCategories() {
        String sqlCommand = "SELECT Id, Name, ParentId  FROM category;";

        ResultSet res = null;
        Statement stmt = null;
        try {
            stmt = c.createStatement();
            res = stmt.executeQuery(sqlCommand);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
            return new ArrayList<Category>();
        }

        List<Category> ret = new ArrayList<Category>();
        try {
            while (res.next()) {
                ret.add(new Category(res.getInt("Id"), res.getString("Name"),
                        new Category(res.getInt("ParentId"), "", null)));
            }
        } catch (SQLException e) {
            sqlExceptionResultLog(e);
            return new ArrayList<Category>();
        } finally {
            closeResultAndStatement(stmt, res);
        }

        for (Category c : ret) {
            // parent relationship
            for (Category p : ret) {
                if (c.getParent().getId() == p.getId()) {
                    c.setParent(p);
                    break;
                }
            }
        }
        return ret;
    }

    @Override
    public List<Location> getLocations() {
        String sqlCommand = "SELECT Id, Name, Code, ParentId FROM location;";

        ResultSet res = null;
        Statement stmt = null;
        try {
            stmt = c.createStatement();
            res = stmt.executeQuery(sqlCommand);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
            return new ArrayList<Location>();
        }

        List<Location> ret = new ArrayList<Location>();
        try {
            while (res.next()) {
                ret.add(new Location(res.getInt("Id"), res.getString("Name"),
                        res.getString("Code"), new Location(res
                                .getInt("ParentId"), null, null, null)));
            }
        } catch (SQLException e) {
            sqlExceptionResultLog(e);
            return new ArrayList<Location>();
        } finally {
            closeResultAndStatement(stmt, res);
        }

        for (Location l : ret) {
            // parent relationship
            for (Location p : ret) {
                if (l.getParent().getId() == p.getId()) {
                    l.setParent(p);
                    break;
                }
            }
        }
        return ret;
    }

    /**
     * returns all dates from the database
     * 
     * @return all dates from the database as Date[]
     * @deprecated method should not be needed
     */
    @Deprecated
    public Date[] getDates() {
        String sqlCommand = "SELECT Id,Day FROM day;";

        ResultSet res = null;
        Statement stmt = null;
        try {
            stmt = c.createStatement();
            res = stmt.executeQuery(sqlCommand);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
            return new Date[0];
        }

        Stack<Date> sd = new Stack<Date>();
        Stack<Integer> si = new Stack<Integer>();
        // int max = 0;
        try {
            while (res.next()) {
                sd.push(res.getDate("Day"));
                int temp = res.getInt("Id");
                si.push(temp);
            }
        } catch (SQLException e) {
            sqlExceptionResultLog(e);
            return new Date[0];
        } finally {
            closeResultAndStatement(stmt, res);
        }

        Date[] ret = new Date[sd.size()];
        while (!sd.isEmpty()) {
            ret[si.pop() - 1] = sd.pop();
        }

        return ret;
    }

    @Override
    public int getAccountId(String accountName) {

        PreparedStatement stmt = null;
        ResultSet res = null;
        try {
            stmt = c.prepareStatement("SELECT Id FROM accounts WHERE AccountName = ? LIMIT 1;");
            stmt.setString(1, accountName);
            res = stmt.executeQuery();
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
            return -1;
        }

        int ret = -1;
        try {
            res.next();
            ret = res.getInt("Id");
        } catch (SQLException e) {
            ret = -1;
        } finally {
            closeResultAndStatement(stmt, res);
        }
        return ret;
    }

    @Override
    public List<Account> getAccounts(String search) {

        // prevent SQL-injection
        PreparedStatement stmt = null;
        ResultSet res = null;
        try {
            stmt = c.prepareStatement("SELECT Id, TwitterAccountId, AccountName,Verified, Follower, URL, LocationId FROM accounts WHERE AccountName LIKE ? LIMIT 50;");
            stmt.setString(1, "%" + search + "%");
            res = stmt.executeQuery();
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        }

        if (res == null)
            return new ArrayList<Account>();

        List<Account> ret = new ArrayList<Account>();
        try {
            while (res.next()) {
                ret.add(new Account(res.getInt("Id"), res
                        .getLong("TwitterAccountId"), res
                        .getString("AccountName"), res.getBoolean("Verified"),
                        res.getString("URL"), res.getInt("Follower"), res
                                .getInt("LocationId")));
            }
        } catch (SQLException e) {
            sqlExceptionResultLog(e);
            return new ArrayList<Account>();
        } finally {
            closeResultAndStatement(stmt, res);
        }

        return ret;
    }

    @Override
    public boolean setCategory(int accountId, int categoryId) {

        // prevent SQL-injection
        PreparedStatement stmt = null;
        try {
            stmt = c.prepareStatement("INSERT IGNORE INTO accountCategory (AccountId, CategoryId) VALUES (?, ?);");
            stmt.setInt(1, accountId);
            stmt.setInt(2, categoryId);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        }

        return executeStatementUpdate(stmt);
    }

    @Override
    public boolean setLocation(int accountId, int locationId, boolean active) {

        // prevent SQL-injection
        PreparedStatement stmt = null;
        try {
            stmt = c.prepareStatement("UPDATE accounts SET LocationId = ? WHERE Id = ?;");
            stmt.setInt(1, accountId);
            stmt.setInt(2, locationId);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        }

        return executeStatementUpdate(stmt);
    }

    @Override
    public boolean addAccount(User user, int locationId) {
        // prevent SQL-injection
        PreparedStatement stmt = null;
        try {
            stmt = c.prepareStatement("INSERT IGNORE INTO accounts (TwitterAccountId, AccountName, Verified, Follower, LocationId, URL, Categorized) VALUES (?, ?, "
                    + (user.isVerified() ? "1" : "0") + ", ?, ?, ?, 0);");
            stmt.setLong(1, user.getId());
            stmt.setString(2, user.getScreenName());
            stmt.setInt(3, user.getFollowersCount());
            stmt.setInt(4, locationId);
            stmt.setString(5,
                    user.getURL().replace("\\", "/").replace("\"", "\"\""));
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        }

        return executeStatementUpdate(stmt);
    }

    @Override
    public TweetsAndRetweets getSumOfData(int[] categoryIDs, int[] locationIDs,
            int[] accountIDs, boolean byDates) throws IllegalArgumentException,
            SQLException {

        if (categoryIDs == null || categoryIDs.length < 1
                || locationIDs == null || locationIDs.length < 1) {
            throw new IllegalArgumentException();
        }
        Statement stmt = createBasicStatement(categoryIDs, locationIDs,
                accountIDs);

        return getTweetSum(stmt, byDates);
    }

    @Deprecated
    public TweetsAndRetweets getSumOfDataWithDates(int[] categoryIDs,
            int[] locationIDs, int[] accountIDs)
            throws IllegalArgumentException, SQLException {
        if (categoryIDs == null || categoryIDs.length < 1
                || locationIDs == null || locationIDs.length < 1) {
            throw new IllegalArgumentException();
        }
        Statement stmt = createBasicStatement(categoryIDs, locationIDs,
                accountIDs);

        return getTweetSum(stmt, true);

    }

    private TweetsAndRetweets getTweetSum(Statement stmt, boolean byDate) {

        String a = "SELECT SUM(Counter), Day FROM tweets JOIN final ON tweets.AccountId=final.val JOIN day ON tweets.DayId=Day.Id GROUP BY DayId;";
        String b = "SELECT SUM(Counter) FROM tweets JOIN final ON tweets.AccountId=final.val;";

        ResultSet res = null;
        try {
            stmt.executeBatch();
            res = stmt.executeQuery(byDate ? a : b);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        }

        List<Tweets> tweets = new ArrayList<Tweets>();
        if (res != null) {
            try {
                while (res.next()) {
                    tweets.add(new Tweets((byDate ? res.getDate("Day") : null),
                            res.getInt(1)));
                }
            } catch (SQLException e) {
                sqlExceptionResultLog(e);
            } finally {
                if (res != null) {
                    try {
                        res.close();
                    } catch (SQLException e) {
                        sqlExceptionLog(e);
                    }
                }
            }
        }

        TweetsAndRetweets ret = new TweetsAndRetweets();
        ret.tweets = tweets;
        ret.retweets = getRetweetSum(stmt, byDate);

        return ret;
    }

    private List<Retweets> getRetweetSum(Statement stmt, boolean byDate) {

        String a = "SELECT SUM(Counter), LocationId, Day FROM retweets JOIN final ON retweets.AccountId=final.val JOIN day ON retweets.DayId=Day.Id GROUP BY LocationId, DayId;";
        String b = "SELECT SUM(Counter), LocationId FROM retweets JOIN final ON retweets.AccountId=final.val GROUP BY LocationId;";

        ResultSet res = null;
        try {
            // stmt.executeBatch();
            res = stmt.executeQuery(byDate ? a : b);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        }

        if (res == null)
            return new ArrayList<Retweets>();

        List<Retweets> ret = new ArrayList<Retweets>();
        try {
            while (res.next()) {
                ret.add(new Retweets((byDate ? res.getDate("Day") : null), res
                        .getInt(1), res.getInt("LocationId")));
            }
        } catch (SQLException e) {
            sqlExceptionResultLog(e);
            return new ArrayList<Retweets>();
        } finally {
            closeResultAndStatement(stmt, res);
        }

        return ret;
    }

    @Override
    public List<Account> getAllData(int[] categoryIDs, int[] locationIDs,
            int[] accountIDs, boolean byDates) throws IllegalArgumentException,
            SQLException {

        if (categoryIDs == null || categoryIDs.length < 1
                || locationIDs == null || locationIDs.length < 1) {
            throw new IllegalArgumentException();
        }
        Statement stmt = createBasicStatement(categoryIDs, locationIDs,
                accountIDs);

        return getTweetSumPerAccount(stmt, byDates);
    }

    @Deprecated
    public List<Account> getAllDataWithDates(int[] categoryIDs,
            int[] locationIDs, int[] accountIDs)
            throws IllegalArgumentException, SQLException {

        if (categoryIDs == null || categoryIDs.length < 1
                || locationIDs == null || locationIDs.length < 1) {
            throw new IllegalArgumentException();
        }
        Statement stmt = createBasicStatement(categoryIDs, locationIDs,
                accountIDs);

        return getTweetSumPerAccount(stmt, true);
    }

    private List<Account> getTweetSumPerAccount(Statement stmt, boolean byDate) {

        String a = "SELECT Counter, AccountName, tweets.AccountId, Day FROM tweets JOIN final ON tweets.AccountId=final.val JOIN day ON tweets.DayId=Day.Id JOIN accounts ON final.val=accounts.Id;";
        String b = "SELECT SUM(Counter),AccountName, tweets.AccountId FROM tweets JOIN final ON tweets.AccountId=final.val JOIN accounts ON final.val=accounts.Id GROUP BY AccountId;";

        ResultSet res = null;
        try {
            stmt.executeBatch();
            res = stmt.executeQuery(byDate ? a : b);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        }

        if (res == null)
            return new ArrayList<Account>();

        List<Account> accounts = new ArrayList<Account>();
        try {
            while (res.next()) {
                if (byDate) {

                    int id = res.getInt(3);
                    Account temp = null;
                    Account ac;
                    Iterator<Account> it = accounts.iterator();
                    while (it.hasNext() && temp == null) {
                        ac = it.next();
                        if (ac.getId() == id) {
                            temp = ac;
                        }
                    }

                    // add account and tweet
                    if (temp == null) {
                        accounts.add(new Account(id, res.getString(2),
                                new Tweets(null, res.getInt(1))));
                    } else {
                        // add tweets to account

                        temp.addTweet(new Tweets(res.getDate("Day"), res
                                .getInt(1)));
                    }
                } else {
                    accounts.add(new Account(res.getInt(3), res.getString(2),
                            new Tweets(null, res.getInt(1))));
                }
            }
        } catch (SQLException e) {
            sqlExceptionResultLog(e);
            return new ArrayList<Account>();
        } finally {
            if (res != null) {
                try {
                    res.close();
                } catch (SQLException e) {
                    sqlExceptionLog(e);
                }
            }
        }

        // get retweets
        List<Account> retweets = getRetweetSumPerAccount(stmt, byDate);

        // match Account lists
        for (Account account : accounts) {
            Iterator<Account> it = retweets.iterator();
            // add retweets
            boolean exit = false;
            while (it.hasNext() && !exit) {
                Account temp = it.next();

                if (temp.getId() == account.getId()) {
                    // match
                    for (Retweets r : temp.getRetweets()) {
                        account.addRetweet(r);
                    }
                    it.remove();
                    exit = true;
                }
            }
        }

        return accounts;
    }

    private List<Account> getRetweetSumPerAccount(Statement stmt, boolean byDate) {

        String a = "SELECT Counter, retweets.LocationId, AccountId, Day FROM retweets JOIN final ON retweets.AccountId=final.val JOIN day ON retweets.DayId=Day.Id;";
        String b = "SELECT SUM(Counter),retweets.LocationId, AccountId FROM retweets JOIN final ON retweets.AccountId=final.val GROUP BY LocationId, AccountId;";

        ResultSet res = null;
        try {
            res = stmt.executeQuery(byDate ? a : b);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        }

        if (res == null)
            return new ArrayList<Account>();

        List<Account> ret = new ArrayList<Account>();
        try {
            while (res.next()) {

                int id = res.getInt(3);
                Account temp = null;
                Iterator<Account> it = ret.iterator();
                while (it.hasNext() && temp == null) {
                    Account ac = it.next();
                    if (ac.getId() == id) {
                        temp = ac;
                    }
                }

                if (temp == null) {
                    // add account and retweet
                    ret.add(new Account(id, res.getString(2), new Retweets(
                            (byDate ? res.getDate(4) : null), res.getInt(1),
                            res.getInt(2))));
                } else {
                    // add retweets to account
                    temp.addRetweet(new Retweets((byDate ? res.getDate("Day")
                            : null), res.getInt(1), res.getInt(2)));
                }

            }
        } catch (SQLException e) {
            sqlExceptionResultLog(e);
            return new ArrayList<Account>();
        } finally {
            closeResultAndStatement(stmt, res);
        }

        return ret;
    }

    private Statement createBasicStatement(int[] categoryIDs,
            int[] locationIDs, int[] accountIDs) throws SQLException {

        Statement stmt = c.createStatement();

        stmt.addBatch("CREATE TEMPORARY TABLE IF NOT EXISTS final (val int PRIMARY KEY);");

        String c = " INSERT IGNORE INTO final (val) SELECT accounts.Id FROM accountCategory JOIN accounts ON accountCategory.AccountId=accounts.Id WHERE (CategoryId="
                + categoryIDs[0];

        for (int i = 1; i < categoryIDs.length; i++) {
            c += " OR CategoryId=" + categoryIDs[i];
        }
        c += ") AND (LocationId=" + locationIDs[0];

        for (int i = 1; i < locationIDs.length; i++) {
            c += " OR LocationId=" + locationIDs[i];
        }
        c += ");";
        stmt.addBatch(c);

        if (accountIDs.length > 0) {
            // add accounts
            String ca = "INSERT IGNORE INTO final (val) VALUES ("
                    + accountIDs[0] + ")";
            for (int i = 1; i < accountIDs.length; i++) {
                ca += ", (" + accountIDs[i] + ")";

            }
            ca += ";";
            stmt.addBatch(ca);
        }

        return stmt;
    }

}
