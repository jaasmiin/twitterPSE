package mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Logger;

import twitter4j.User;
import util.Util;
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
    public Category getCategories() {

        // get all categories from the database
        String sqlCommand = "SELECT c.Id, Name, ParentId,  AccountId "
                + "FROM category c "
                + "LEFT JOIN accountCategory ac ON c.Id=ac.CategoryId "
                + "ORDER By Name";

        ResultSet res = null;
        Statement stmt = null;
        runningRequest = true;
        try {
            stmt = c.createStatement();
            res = stmt.executeQuery(sqlCommand);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
            return null;
        } finally {
            runningRequest = false;
        }

        // create a list with all categories
        List<Category> categories = new ArrayList<Category>();
        try {
            while (res.next()) {
                int parent = res.getInt("ParentId");
                int id = res.getInt("Id");
                boolean used = res.getInt("AccountId") == 0 ? false : true;
                Category c = new Category(id, res.getString("Name"), parent,
                        used);
                categories.add(c);
            }
        } catch (SQLException e) {
            sqlExceptionResultLog(e);
            return null;
        } finally {
            // close mysql-statement
            closeResultAndStatement(stmt, res);
        }

        return getCategoryTree(categories);
    }

    @Override
    public List<Location> getLocations() {
        String sqlCommand = "SELECT Id, Name, Code, ParentId FROM location ORDER BY Name, Code;";

        ResultSet res = null;
        Statement stmt = null;
        runningRequest = true;
        try {
            stmt = c.createStatement();
            res = stmt.executeQuery(sqlCommand);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
            return new ArrayList<Location>();
        } finally {
            runningRequest = false;
        }

        List<Location> ret = new ArrayList<Location>();
        try {
            while (res.next()) {
                ret.add(new Location(res.getInt("Id"), res.getString("Name"),
                        res.getString("Code"), null));
            }
        } catch (SQLException e) {
            sqlExceptionResultLog(e);
            return new ArrayList<Location>();
        } finally {
            closeResultAndStatement(stmt, res);
        }

        assert (ret.size() == 249);

        return ret;
    }

    @Override
    public List<Account> getAccounts(String search) {

        // prevent SQL-injection
        PreparedStatement stmt = null;
        ResultSet res = null;
        runningRequest = true;
        try {
            stmt = c.prepareStatement("SELECT accounts.Id, TwitterAccountId, AccountName,Verified, Follower, URL, Code FROM accounts "
                    + "JOIN location ON accounts.LocationId=location.Id WHERE AccountName LIKE ? ORDER BY Follower DESC LIMIT 100;");
            stmt.setString(1, "%" + search + "%");
            res = stmt.executeQuery();
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        } finally {
            runningRequest = false;
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
                                .getString("Code")));
            }
        } catch (SQLException e) {
            sqlExceptionResultLog(e);
            return new ArrayList<Account>();
        } finally {
            closeResultAndStatement(stmt, res);
        }

        addCategories(ret);

        return ret;
    }

    @Override
    public Account getAccount(int id) {

        // get informations about the account
        PreparedStatement stmt = null;
        ResultSet res = null;
        runningRequest = true;
        try {
            stmt = c.prepareStatement("SELECT accounts.Id, TwitterAccountId, AccountName,Verified, Follower, URL, Code FROM accounts "
                    + "JOIN location ON accounts.LocationId=location.Id WHERE accounts.Id=? LIMIT 1;");
            stmt.setInt(1, id);
            res = stmt.executeQuery();
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        } finally {
            runningRequest = false;
        }

        if (res == null)
            return null;

        Account ret = null;
        try {
            res.next();
            ret = new Account(res.getInt("Id"),
                    res.getLong("TwitterAccountId"),
                    res.getString("AccountName"), res.getBoolean("Verified"),
                    res.getString("URL"), res.getInt("Follower"),
                    res.getString("Code"));
        } catch (SQLException e) {
            sqlExceptionResultLog(e);
            return ret;
        } finally {
            closeResult(res);
        }

        // get the categories for the account
        stmt = null;
        res = null;
        runningRequest = true;
        try {
            stmt = c.prepareStatement("SELECT CategoryId FROM accountCategory WHERE AccountId=?;");
            stmt.setInt(1, id);
            res = stmt.executeQuery();
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        } finally {
            runningRequest = false;
        }

        if (res == null)
            return null;

        try {
            while (res.next()) {
                ret.addCategoryId(res.getInt(1));
            }
        } catch (SQLException e) {
            sqlExceptionResultLog(e);
            return ret;
        } finally {
            closeResultAndStatement(stmt, res);
        }

        return ret;
    }

    private void addCategories(List<Account> list) {

        if (list.size() > 0) {
            Iterator<Account> it = list.iterator();
            String query = "SELECT AccountId, CategoryId FROM accountCategory WHERE AccountId="
                    + it.next().getId();
            while (it.hasNext()) {
                query += " OR AccountId=" + it.next().getId();
            }
            query += ";";

            Statement stmt = null;
            ResultSet res = null;
            try {
                stmt = c.createStatement();
                res = stmt.executeQuery(query);
            } catch (SQLException e) {
                sqlExceptionLog(e, stmt);
                return;
            }

            try {
                while (res.next()) {
                    for (Account temp : list) {
                        if (temp.getId() == res.getInt(1)) {
                            temp.addCategoryId(res.getInt(2));
                        }
                    }
                }
            } catch (SQLException e) {
                sqlExceptionResultLog(e);
                return;
            } finally {
                closeResultAndStatement(stmt, res);
            }
        }

    }

    @Override
    public boolean setCategory(int accountId, int categoryId) {

        // add an account-category pair to the database
        PreparedStatement stmt = null;
        try {
            stmt = c.prepareStatement("INSERT IGNORE INTO accountCategory (AccountId, CategoryId) VALUES (?, ?);");
            stmt.setInt(1, accountId);
            stmt.setInt(2, categoryId);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        }

        // execute first query
        if (executeStatementUpdate(stmt, false)) {
            // set account as categprized
            try {
                stmt = c.prepareStatement("UPDATE accounts SET Categorized=1 WHERE Id=?;");
                stmt.setInt(1, accountId);
            } catch (SQLException e) {
                sqlExceptionLog(e, stmt);
            }
            return executeStatementUpdate(stmt, false);
        }

        return false;
    }

    @Override
    public boolean setLocation(int accountId, int locationId) {

        // prevent SQL-injection
        PreparedStatement stmt = null;
        try {
            stmt = c.prepareStatement("UPDATE accounts SET LocationId = ? WHERE Id = ?;");
            stmt.setInt(1, locationId);
            stmt.setInt(2, accountId);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        }

        return executeStatementUpdate(stmt, false);
    }

    @Override
    public boolean addAccount(User user, int locationId) {
        // prevent SQL-injection
        PreparedStatement stmt = null;
        try {
            stmt = c.prepareStatement("INSERT IGNORE INTO accounts (TwitterAccountId, AccountName, Verified, Follower, LocationId, URL, Categorized)"
                    + " VALUES (?, ?, "
                    + (user.isVerified() ? "1" : "0")
                    + ", ?, ?, ?, 1);");
            stmt.setLong(1, user.getId());
            stmt.setString(2, user.getScreenName());
            stmt.setInt(3, user.getFollowersCount());
            stmt.setInt(4, locationId);
            stmt.setString(5, Util.checkURL(user.getURL()));
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        }

        return executeStatementUpdate(stmt, false);
    }

    /**
     * creates the category tree
     * 
     * @param categories
     *            the list of categories
     * @return the top level category
     */
    private Category getCategoryTree(List<Category> categories) {
        // topological sort list of categories in reverse order
        categories = topSortCategories(categories);

        // get positions of categories in the list for fast access
        HashMap<Integer, Integer> idx = new HashMap<Integer, Integer>();
        Iterator<Category> it = categories.iterator();
        int i = 0;
        while (it.hasNext()) {
            idx.put(it.next().getId(), i);
            i++;
        }

        Category ret = null;

        it = categories.iterator();
        while (it.hasNext()) {
            Category category = it.next();

            if (category.isUsed()) {
                int parentId = category.getParentId();
                if (parentId == 0) {
                    ret = category;
                    continue;
                }

                int parentPosition = idx.get(parentId);
                Category parent = categories.get(parentPosition);
                parent.setUsed(true);
                parent.addChild(category);
            }
        }

        return ret;
    }

    /**
     * does a topological sort of categories
     * 
     * if category i has parent j, j will be before i
     * 
     * @param categories
     *            the list of categories
     * @return the sorted list of categories
     */
    private List<Category> topSortCategories(List<Category> categories) {
        int[] inDegree = new int[categories.size()];
        HashMap<Integer, Integer> idx = new HashMap<Integer, Integer>();

        // get a mapping: CategoryId to array list index
        Iterator<Category> it = categories.iterator();
        int i = 0;
        while (it.hasNext()) {
            idx.put(it.next().getId(), i);
            i++;
        }

        // count the inDegree of each node in the DAG
        // in this case the number of children of each category
        it = categories.iterator();
        while (it.hasNext()) {
            Category category = it.next();
            int parentId = category.getParentId();
            if (parentId != 0)
                inDegree[idx.get(parentId)]++;
        }

        // look for nodes with inDegree 0
        // those are the start nodes for the topological sorting
        // insert them into a queue
        Queue<Integer> q = new LinkedList<Integer>();
        for (i = 0; i < categories.size(); i++) {
            if (inDegree[i] == 0) {
                q.add(i);
            }
        }

        // create topological sorting
        List<Category> topSort = new ArrayList<Category>();
        while (!q.isEmpty()) {
            Integer node = q.poll();
            Category category = categories.get(node);
            topSort.add(category);

            // delete edge in implicit graph
            int parentId = category.getParentId();
            if (parentId == 0)
                continue;
            int parentPosition = idx.get(parentId);
            inDegree[parentPosition]--;

            if (inDegree[parentPosition] == 0) {
                q.add(parentPosition);
            }
        }

        return topSort;
    }

    @Override
    public TweetsAndRetweets getSumOfData(List<Integer> categoryIDs,
            List<Integer> locationIDs, List<Integer> accountIDs, boolean byDates) {

        Statement stmt;
        TweetsAndRetweets ret = new TweetsAndRetweets();
        try {
            stmt = createBasicStatement(categoryIDs, locationIDs, accountIDs);
            ret = getTweetSum(stmt, byDates);
        } catch (SQLException  e) {
            logger.warning("SQL-Exception by gatSumData: " + e.getMessage());
        }catch (IllegalArgumentException e){
        }
        
        return ret;
    }

    private TweetsAndRetweets getTweetSum(Statement stmt, boolean byDate) {

        String a = "SELECT SUM(Counter), Day FROM tweets JOIN final ON tweets.AccountId=final.val JOIN day ON tweets.DayId=day.Id GROUP BY DayId;";
        String b = "SELECT SUM(Counter) FROM tweets JOIN final ON tweets.AccountId=final.val;";

        ResultSet res = null;
        runningRequest = true;
        try {
            stmt.executeBatch();
            res = stmt.executeQuery(byDate ? a : b);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        } finally {
            runningRequest = false;
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
                closeResult(res);
            }
        }
        TweetsAndRetweets ret = new TweetsAndRetweets();
        ret.setTweets(tweets);
        ret.setRetweets(getRetweetSum(stmt, byDate));
        return ret;
    }

    private List<Retweets> getRetweetSum(Statement stmt, boolean byDate) {

        String a = "SELECT SUM(Counter), Code, Day FROM retweets "
                + "JOIN final ON retweets.AccountId=final.val JOIN day ON retweets.DayId=day.Id JOIN location ON retweets.LocationId=location.Id "
                + "GROUP BY LocationId, DayId;";
        String b = "SELECT SUM(Counter), Code FROM retweets "
                + "JOIN final ON retweets.AccountId=final.val JOIN location ON retweets.LocationId=location.Id GROUP BY LocationId;";

        ResultSet res = null;
        runningRequest = true;
        try {
            res = stmt.executeQuery(byDate ? a : b);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        } finally {
            runningRequest = false;
        }
        if (res == null)
            return new ArrayList<Retweets>();
        List<Retweets> ret = new ArrayList<Retweets>();
        try {
            while (res.next()) {
                Retweets element = new Retweets((byDate ? res.getDate("Day")
                        : null), res.getInt(1), res.getString("Code"));
                ret.add(element);
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
    public HashMap<String, Integer> getAllRetweetsPerLocation() {

        String sqlCommand = "SELECT COUNT(*), Code FROM retweets JOIN location ON retweets.locationId=location.Id GROUP BY locationId;";

        ResultSet res = null;
        Statement stmt = null;
        runningRequest = true;
        try {
            stmt = c.createStatement();
            res = stmt.executeQuery(sqlCommand);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
            return new HashMap<String, Integer>();
        } finally {
            runningRequest = false;
        }

        HashMap<String, Integer> ret = new HashMap<String, Integer>();
        try {
            while (res.next()) {
                ret.put(res.getString("Code"), res.getInt(1));
            }
        } catch (SQLException e) {
            sqlExceptionResultLog(e);
            return new HashMap<String, Integer>();
        } finally {
            closeResultAndStatement(stmt, res);
        }
        return ret;
    }

    @Override
    public List<Account> getAllData(List<Integer> categoryIDs,
            List<Integer> locationIDs, List<Integer> accountIDs, boolean byDates) {

        Statement stmt;
        List<Account> ret = new ArrayList<Account>();
        try {
            stmt = createBasicStatement(categoryIDs, locationIDs, accountIDs);
            ret = getTweetSumPerAccount(stmt, byDates);
        } catch (SQLException e) {
            logger.warning("SQL-Exception by gatAllData: " + e.getMessage());
        }catch (IllegalArgumentException e){
        }

        return ret;
    }

    private HashMap<Integer, Account> getAccounts(Statement stmt) {

        String query = "SELECT Id, AccountName, Follower FROM final LEFT JOIN accounts ON final.val=accounts.Id ORDER BY Id DESC;";
        ResultSet res = null;
        runningRequest = true;
        try {
            stmt.executeBatch();
            res = stmt.executeQuery(query);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        } finally {
            runningRequest = false;
        }
        if (res == null)
            return new HashMap<Integer, Account>();
        HashMap<Integer, Account> ret = new HashMap<Integer, Account>();
        try {
            while (res.next()) {
                ret.put(res.getInt(1),
                        new Account(res.getInt(1), res.getString(2), res
                                .getInt("Follower")));
            }
        } catch (SQLException e) {
            sqlExceptionResultLog(e);
            return new HashMap<Integer, Account>();
        } finally {
            closeResult(res);
        }
        return ret;
    }

    private List<Account> getTweetSumPerAccount(Statement stmt, boolean byDate) {

        HashMap<Integer, Account> accounts = getAccounts(stmt);

        String a = "SELECT Counter, AccountName, tweets.AccountId, Day FROM tweets "
                + "JOIN final ON tweets.AccountId=final.val JOIN day ON tweets.DayId=day.Id JOIN accounts ON final.val=accounts.Id;";
        String b = "SELECT SUM(Counter),AccountName, tweets.AccountId FROM tweets "
                + "JOIN final ON tweets.AccountId=final.val JOIN accounts ON final.val=accounts.Id GROUP BY AccountId;";
        ResultSet res = null;
        runningRequest = true;
        try {
            res = stmt.executeQuery(byDate ? a : b);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        } finally {
            runningRequest = false;
        }
        if (res == null)
            return Util.collectionToList(accounts.values());
        try {
            while (res.next()) {
                int id = res.getInt(3);
                accounts.get(id).addTweet(
                        new Tweets(byDate ? res.getDate("Day") : null, res
                                .getInt(1)));
            }
        } catch (SQLException e) {
            sqlExceptionResultLog(e);
            return Util.collectionToList(accounts.values());
        } finally {
            closeResult(res);
        }
        // get retweets
        getRetweetSumPerAccount(stmt, byDate, accounts);

        return Util.collectionToList(accounts.values());
    }

    private void getRetweetSumPerAccount(Statement stmt, boolean byDate,
            HashMap<Integer, Account> ret) {
        String a = "SELECT Counter, AccountId, Code, Day FROM retweets "
                + "JOIN final ON retweets.AccountId=final.val JOIN day ON retweets.DayId=day.Id JOIN location ON retweets.LocationId=location.Id;";
        String b = "SELECT SUM(Counter), AccountId, Code FROM retweets "
                + "JOIN final ON retweets.AccountId=final.val JOIN location ON retweets.LocationId=location.Id GROUP BY AccountId, LocationId;";
        ResultSet res = null;
        try {
            runningRequest = true;
            res = stmt.executeQuery(byDate ? a : b);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        } finally {
            runningRequest = false;
        }
        if (res == null)
            return;
        try {
            while (res.next()) {
                int id = res.getInt("AccountId");
                Retweets element = new Retweets((byDate ? res.getDate("Day")
                        : null), res.getInt(1), res.getString("Code"));
                ret.get(id).addRetweet(element);
            }
        } catch (SQLException e) {
            sqlExceptionResultLog(e);
        } finally {
            closeResultAndStatement(stmt, res);
        }
    }

    private Statement createBasicStatement(List<Integer> categoryIDs,
            List<Integer> locationIDs, List<Integer> accountIDs)
            throws SQLException {
        boolean categoryIsSet = categoryIDs != null && categoryIDs.size() > 0;
        boolean locationIsSet = locationIDs != null && locationIDs.size() > 0;
        boolean accountIsSet = accountIDs != null && accountIDs.size() > 0;
        if (!categoryIsSet && !locationIsSet && !accountIsSet) {
            throw new IllegalArgumentException();
        }

        Integer[] categories = new Integer[categoryIDs.size()];
        categoryIDs.toArray(categories);
        Integer[] locations = new Integer[locationIDs.size()];
        locationIDs.toArray(locations);
        Integer[] accounts = new Integer[accountIDs.size()];
        accountIDs.toArray(accounts);

        Statement stmt = c.createStatement();
        stmt.addBatch("CREATE TEMPORARY TABLE IF NOT EXISTS final (val int PRIMARY KEY);");
        stmt.addBatch("TRUNCATE final;");
        if (!categoryIsSet && locationIsSet) {
            String c = "INSERT IGNORE INTO final (val) SELECT Id FROM accounts WHERE (LocationId="
                    + locations[0];
            for (int i = 1; i < locations.length; i++) {
                c += " OR LocationId=" + locations[i];
            }
            c += ");";
            stmt.addBatch(c);
        } else if (categoryIsSet || locationIsSet) {
            String c = "INSERT IGNORE INTO final (val) "
                    + "SELECT accounts.Id FROM accountCategory JOIN accounts ON accountCategory.AccountId=accounts.Id WHERE ";
            if (categoryIsSet) {
                c += "(CategoryId=" + categories[0];
                for (int i = 1; i < categories.length; i++) {
                    c += " OR CategoryId=" + categories[i];
                }
            }
            if (categoryIsSet && locationIsSet) {
                c += ") AND ";
            }
            if (locationIsSet) {
                c += "(LocationId=" + locations[0];
                for (int i = 1; i < locations.length; i++) {
                    c += " OR LocationId=" + locations[i];
                }
            }
            c += ");";
            stmt.addBatch(c);
        }
        if (accountIsSet) {
            // add accounts
            String ca = "INSERT IGNORE INTO final (val) VALUES (" + accounts[0]
                    + ")";
            for (int i = 1; i < accounts.length; i++) {
                ca += ", (" + accounts[i] + ")";
            }
            ca += ";";
            stmt.addBatch(ca);
        }
        return stmt;
    }

}
