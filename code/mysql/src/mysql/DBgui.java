package mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
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
        String sqlCommand = "SELECT Count(AccountId), c.Id, Name, ParentId,  AccountId "
                + "FROM category c "
                + "LEFT JOIN accountCategory ac ON c.Id=ac.CategoryId "
                + "GROUP BY c.Id " // new
                + "ORDER By Name";

        // execute query on database
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

        if (res == null || Thread.interrupted()) {
            return null;
        }

        // create a list with all categories
        List<Category> categories = new ArrayList<Category>();
        try {
            while (res.next()) {
                // build the category and add her to the list
                boolean used = res.getInt("AccountId") == 0 ? false : true;
                Category c = new Category(res.getInt("Id"),
                        res.getString("Name"), res.getInt("ParentId"), used);
                c.setMatchedAccounts(res.getInt(1));
                categories.add(c);
            }
        } catch (SQLException e) {
            sqlExceptionResultLog(e);
            return null;
        } catch (NullPointerException e) {
            // do nothing
        } finally {
            // close mysql-statement
            closeResultAndStatement(stmt, res);
        }

        // build the category tree with the categories from the list
        return getCategoryTree(categories);
    }

    @Override
    public List<Location> getLocations() {
        String sqlCommand = "SELECT Id, Name, Code FROM location ORDER BY Name, Code;";

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

        if (res == null || Thread.interrupted()) {
            return new ArrayList<Location>();
        }

        // read all the locations from the sql-result
        List<Location> ret = new ArrayList<Location>();
        try {
            while (res.next()) {
                // add the builded location to the return list
                // 1 - Id
                // 2 - Name
                // 3 - Code
                ret.add(new Location(res.getInt(1), Util
                        .getUppercaseCountry(res.getString(2)), res
                        .getString(3), null));
            }
        } catch (SQLException e) {
            sqlExceptionResultLog(e);
            return new ArrayList<Location>();
        } catch (NullPointerException e) {
            // do nothing
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
        runningRequest = true;
        try {
            stmt = c.prepareStatement("SELECT accounts.Id, TwitterAccountId, AccountName, Verified, Follower, URL, Code FROM accounts "
                    + "JOIN location ON accounts.LocationId=location.Id WHERE AccountName LIKE ? ORDER BY Follower DESC LIMIT 100;");
            stmt.setString(1, "%" + search + "%");
            res = stmt.executeQuery();
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        } finally {
            runningRequest = false;
        }

        // check sql-result
        if (res == null || Thread.interrupted())
            return new ArrayList<Account>();

        HashMap<Integer, Account> ret = new HashMap<Integer, Account>(100);
        try {
            while (res.next()) {
                // build the accounts with the data from the sql-result
                // 1 - Id
                // 2 - TwitterAccountId
                // 3 - AccountName
                // 4 - Verified
                // 5 - Follower
                // 6 - URL
                // 7 - Code (LocationCode)
                int id = res.getInt(1);
                ret.put(id,
                        new Account(id, res.getLong(2), res.getString(3), res
                                .getBoolean(4), res.getString(6),
                                res.getInt(5), res.getString(7)));
            }
        } catch (SQLException e) {
            sqlExceptionResultLog(e);
            return new ArrayList<Account>();
        } catch (NullPointerException e) {
            // do nothing
        } finally {
            closeResultAndStatement(stmt, res);
        }

        // add the categories from the database to the accounts
        addCategories(ret);

        List<Account> list = Util.collectionToList(ret.values());
        Collections.sort(list);

        return list;
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

        // check sql-result
        if (res == null || Thread.interrupted())
            return null;

        Account ret = null;
        try {
            // read account information from the sql-result and build an Account
            // object
            res.next();
            ret = new Account(res.getInt("Id"),
                    res.getLong("TwitterAccountId"),
                    res.getString("AccountName"), res.getBoolean("Verified"),
                    res.getString("URL"), res.getInt("Follower"),
                    res.getString("Code"));
        } catch (SQLException e) {
            sqlExceptionResultLog(e);
            return ret;
        } catch (NullPointerException e) {
            // do nothing
        } finally {
            closeResult(res);
        }

        // use method addCategories
        HashMap<Integer, Account> temp = new HashMap<Integer, Account>(1);
        temp.put(ret.getId(), ret);
        addCategories(temp);

        return ret;
    }

    private void addCategories(HashMap<Integer, Account> list) {

        // validate parameter
        if (list.size() > 0) {

            // build query with all the accountIds where you need the categories
            Iterator<Integer> it = list.keySet().iterator();
            String query = "SELECT AccountId, CategoryId FROM accountCategory WHERE AccountId="
                    + it.next();
            while (it.hasNext()) {
                query += " OR AccountId=" + it.next();
            }
            query += ";";

            // execute statement on database
            Statement stmt = null;
            ResultSet res = null;
            try {
                stmt = c.createStatement();
                res = stmt.executeQuery(query);
            } catch (SQLException e) {
                sqlExceptionLog(e, stmt);
                return;
            }

            if (res == null || Thread.interrupted()) {
                return;
            }

            try {
                // read result and add the categories to the corresponding
                // account
                while (res.next()) {
                    // 1 - AccountId
                    // 2 - CategoryId
                    int id = res.getInt(1);
                    if (list.containsKey(id)) {
                        list.get(id).addCategoryId(res.getInt(2));
                    }
                }
            } catch (SQLException e) {
                sqlExceptionResultLog(e);
            } catch (NullPointerException e) {
                // do nothing
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

        if (accountId <= 0 || locationId <= 0) {
            return false;
        }

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
    public HashMap<String, Integer> getAllRetweetsPerLocation() {

        // select the total number of retweets for each country

        String sqlCommand = "SELECT SUM(Counter), Code FROM retweets JOIN location ON retweets.locationId=location.Id GROUP BY locationId;";

        ResultSet res = null;
        Statement stmt = null;
        runningRequest = true;
        try {
            // create and execute sql-query
            stmt = c.createStatement();
            res = stmt.executeQuery(sqlCommand);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
            return new HashMap<String, Integer>();
        } finally {
            runningRequest = false;
        }

        if (res == null || Thread.interrupted()) {
            return new HashMap<String, Integer>();
        }

        // read the sql-result line per line
        HashMap<String, Integer> ret = new HashMap<String, Integer>();
        try {
            while (res.next()) {
                // write mapping between number and country into a hashmap
                // 1 - SUM(Counter)
                // 2 - Code
                ret.put(res.getString(2), res.getInt(1));
            }
        } catch (SQLException e) {
            sqlExceptionResultLog(e);
            return new HashMap<String, Integer>();
        } catch (NullPointerException e) {
            // do nothing
        } finally {
            closeResultAndStatement(stmt, res);
        }
        return ret;
    }

    @Override
    public TweetsAndRetweets getSumOfData(List<Integer> categoryIDs,
            List<Integer> locationIDs, List<Integer> accountIDs, boolean byDates) {

        Statement stmt;
        TweetsAndRetweets ret = new TweetsAndRetweets();
        try {
            // build sql-statments
            stmt = createBasicStatement(categoryIDs, locationIDs, accountIDs);
            // execute sql-queries and receive result
            ret = getTweetSum(stmt, byDates);
        } catch (SQLException e) {
            logger.warning("SQL-Exception by gatSumData: " + e.getMessage());
        } catch (IllegalArgumentException e) {
        }

        return ret;
    }

    private TweetsAndRetweets getTweetSum(Statement stmt, boolean byDate) {

        // provide statements to ask for all tweets or for the tweets per day
        String a = "SELECT SUM(Counter), Day FROM tweets JOIN final ON tweets.AccountId=final.val JOIN day ON tweets.DayId=day.Id GROUP BY DayId;";
        String b = "SELECT SUM(Counter) FROM tweets JOIN final ON tweets.AccountId=final.val;";

        ResultSet res = null;
        runningRequest = true;
        try {
            // execute the prepared statements to build a list of selected
            // accounts in the database
            stmt.executeBatch();
            // execute the select statement for the tweets
            res = stmt.executeQuery(byDate ? a : b);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        } finally {
            runningRequest = false;
        }

        if (res == null || Thread.interrupted()) {
            return new TweetsAndRetweets();
        }

        // read the result and add the tweets to a result list
        List<Tweets> tweets = new ArrayList<Tweets>();
        if (res != null) {
            try {
                while (res.next()) {
                    // build a Tweets object for this line of the sql-result
                    // 1 - SUM(Counter)
                    // 2 - Day (opt.)
                    tweets.add(new Tweets((byDate ? res.getDate(2) : null), res
                            .getInt(1)));
                }
            } catch (SQLException e) {
                sqlExceptionResultLog(e);
            } catch (NullPointerException e) {
                // do nothing
            } finally {
                closeResult(res);
            }
        }

        // build and set return values
        TweetsAndRetweets ret = new TweetsAndRetweets();
        ret.setTweets(tweets);
        // set and get the requested retweets
        ret.setRetweets(getRetweetSum(stmt, byDate));
        return ret;
    }

    private List<Retweets> getRetweetSum(Statement stmt, boolean byDate) {

        // provide statements to separate retweets by date or to sum over all
        // dates
        String a = "SELECT SUM(Counter), Code, Day FROM retweets "
                + "JOIN final ON retweets.AccountId=final.val JOIN day ON retweets.DayId=day.Id JOIN location ON retweets.LocationId=location.Id "
                + "GROUP BY LocationId, DayId;";
        String b = "SELECT SUM(Counter), Code FROM retweets "
                + "JOIN final ON retweets.AccountId=final.val JOIN location ON retweets.LocationId=location.Id GROUP BY LocationId;";

        ResultSet res = null;
        runningRequest = true;
        try {
            // execute select-query on the database by using the temporary table
            // with the selected accounts (created by the getTweets-Method
            // below)
            res = stmt.executeQuery(byDate ? a : b);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        } finally {
            runningRequest = false;
        }

        // check result
        if (res == null || Thread.interrupted())
            return new ArrayList<Retweets>();

        // read sql-result line per line
        List<Retweets> ret = new ArrayList<Retweets>();
        try {
            while (res.next()) {
                // for each line in the result build a new Retweets-object
                // 1 - SUM(Counter)
                // 2 - Code
                // 3 - Day (opt.)
                Retweets element = new Retweets(
                        (byDate ? res.getDate(3) : null), res.getInt(1),
                        res.getString(2));
                // add Retweets to the return list
                ret.add(element);
            }
        } catch (SQLException e) {
            sqlExceptionResultLog(e);
            return new ArrayList<Retweets>();
        } catch (NullPointerException e) {
            // do nothing
        } finally {
            closeResultAndStatement(stmt, res);
        }
        return ret;
    }

    // TODO add Comments below

    @Override
    public List<Account> getAllData(List<Integer> categoryIDs,
            List<Integer> locationIDs, List<Integer> accountIDs, boolean byDates) {

        double t = System.currentTimeMillis();

        Statement stmt;
        List<Account> ret = new ArrayList<Account>();
        try {
            stmt = createBasicStatement(categoryIDs, locationIDs, accountIDs);
            ret = getTweetSumPerAccount(stmt, byDates);
        } catch (SQLException e) {
            logger.warning("SQL-Exception by gatAllData: " + e.getMessage());
        } catch (IllegalArgumentException e) {
        }
        System.out.println("GetAllData: " + (System.currentTimeMillis() - t));

        return ret;
    }

    private HashMap<Integer, Account> getAccounts(Statement stmt) {

        String query = "SELECT Id, AccountName, Follower FROM final LEFT JOIN accounts ON final.val=accounts.Id;"; // ORDER
                                                                                                                   // BY
                                                                                                                   // Id
                                                                                                                   // DESC;";
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
        if (res == null || Thread.interrupted()) {
            return new HashMap<Integer, Account>();
        }

        HashMap<Integer, Account> ret = new HashMap<Integer, Account>();
        try {
            while (res.next()) {
                // 1 - Id
                // 2 - AccountName
                // 3 - Follower
                ret.put(res.getInt(1),
                        new Account(res.getInt(1), res.getString(2), res
                                .getInt(3)));
            }
        } catch (SQLException e) {
            sqlExceptionResultLog(e);
            return new HashMap<Integer, Account>();
        } catch (NullPointerException e) {
            // do nothing
        } finally {
            closeResult(res);
        }
        return ret;
    }

    private List<Account> getTweetSumPerAccount(Statement stmt, boolean byDate) {

        HashMap<Integer, Account> accounts = getAccounts(stmt);

        String a = "SELECT Counter, tweets.AccountId, Day FROM tweets "
                + "JOIN final ON tweets.AccountId=final.val JOIN day ON tweets.DayId=day.Id;";
        String b = "SELECT SUM(Counter), tweets.AccountId FROM tweets "
                + "JOIN final ON tweets.AccountId=final.val GROUP BY AccountId;";
        ResultSet res = null;
        runningRequest = true;
        double t = System.currentTimeMillis();
        try {
            res = stmt.executeQuery(byDate ? a : b);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        } finally {
            runningRequest = false;
        }
        System.out.println("Datenbankquery für Tweets: "
                + (System.currentTimeMillis() - t));

        if (res == null || Thread.interrupted())
            return Util.collectionToList(accounts.values());

        t = System.currentTimeMillis();
        try {
            while (res.next()) {
                // 1 - Counter / SUM(Counter)
                // 2 - AccountId
                // 3 - Day (opt.)
                int id = res.getInt(2);
                accounts.get(id).addTweet(
                        new Tweets(byDate ? res.getDate(3) : null, res
                                .getInt(1)));
            }
        } catch (SQLException e) {
            sqlExceptionResultLog(e);
            return Util.collectionToList(accounts.values());
        } catch (NullPointerException e) {
            // do nothing
        } finally {
            closeResult(res);
        }
        System.out.println("Java Verarbeitung Tweets: "
                + (System.currentTimeMillis() - t));

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
        double t = System.currentTimeMillis();
        try {
            runningRequest = true;
            res = stmt.executeQuery(byDate ? a : b);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        } finally {
            runningRequest = false;
        }
        System.out.println("Datenbankquery für Retweets: "
                + (System.currentTimeMillis() - t));

        if (res == null || Thread.interrupted())
            return;

        t = System.currentTimeMillis();
        try {
            while (res.next()) {
                // 1 - Counter / SUM(Counter)
                // 2 - AccountId
                // 3 - Code
                // 4 - Day (opt.)
                int id = res.getInt(2);
                Retweets element = new Retweets(
                        (byDate ? res.getDate(4) : null), res.getInt(1),
                        res.getString(3));
                ret.get(id).addRetweet(element);
            }
        } catch (SQLException e) {
            sqlExceptionResultLog(e);
        } catch (NullPointerException e) {
            // do nothing
        } finally {
            closeResultAndStatement(stmt, res);
        }
        System.out.println("Java Verarbeitung Retweets: "
                + (System.currentTimeMillis() - t));
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
