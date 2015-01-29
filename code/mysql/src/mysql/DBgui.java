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

        //create a list with all categories
        List<Category> categories = new ArrayList<Category>();
        try {
            while (res.next()) {
                int parent = res.getInt("ParentId");
                int id = res.getInt("Id");
                boolean used = res.getInt("AccountId") == 0;
                Category c = new Category(id, res.getString("Name"), parent, used);
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

        return ret;
    }

    @Override
    public int getAccountId(String accountName) {

        PreparedStatement stmt = null;
        ResultSet res = null;
        runningRequest = true;
        try {
            stmt = c.prepareStatement("SELECT Id FROM accounts WHERE AccountName = ? LIMIT 1;");
            stmt.setString(1, accountName);
            res = stmt.executeQuery();
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
            return -1;
        } finally {
            runningRequest = false;
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
        runningRequest = true;
        try {
            stmt = c.prepareStatement("SELECT Id, TwitterAccountId, AccountName,Verified, Follower, URL, LocationId FROM accounts WHERE AccountName LIKE ? ORDER BY Follower DESC LIMIT 100;");
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
            stmt.setInt(1, accountId);
            stmt.setInt(2, locationId);
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
            stmt = c.prepareStatement("INSERT IGNORE INTO accounts (TwitterAccountId, AccountName, Verified, Follower, LocationId, URL, Categorized) VALUES (?, ?, "
                    + (user.isVerified() ? "1" : "0") + ", ?, ?, ?, 1);");
            stmt.setLong(1, user.getId());
            stmt.setString(2, user.getScreenName());
            stmt.setInt(3, user.getFollowersCount());
            stmt.setInt(4, locationId);
            stmt.setString(5,
                    user.getURL().replace("\\", "/").replace("\"", "\"\""));
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
        }

        return executeStatementUpdate(stmt, false);
    }

    @Override
    public TweetsAndRetweets getSumOfData(Integer[] categoryIDs,
            Integer[] locationIDs, Integer[] accountIDs, boolean byDates)
            throws IllegalArgumentException, SQLException {

        Statement stmt = createBasicStatement(categoryIDs, locationIDs,
                accountIDs);

        return getTweetSum(stmt, byDates);
    }

    /**
     * creates the category tree
     * 
     * @param categories the list of categories
     * @return the top level category
     */
    private Category getCategoryTree(List<Category> categories) {
        //topological sort list of categories in reverse order
        categories = topSortCategories(categories);
        
        //get positions of categories in the list for fast access
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
     * @param categories the list of categories
     * @return the sorted list of categories
     */
    private List<Category> topSortCategories(List<Category> categories) {
    	int[] inDegree = new int[categories.size()];
    	HashMap<Integer, Integer> idx = new HashMap<Integer, Integer>();
    	
    	//get a mapping: CategoryId to array list index
    	Iterator<Category> it = categories.iterator();
    	int i = 0;
    	while (it.hasNext()) {
    		idx.put(it.next().getId(), i);
    		i++;
    	}
    	
    	//count the inDegree of each node in the DAG
    	//in this case the number of children of each category
    	it = categories.iterator();
    	while (it.hasNext()) {
    		Category category = it.next();
    		int parentId = category.getParentId();
    		if (parentId != 0) inDegree[idx.get(parentId)]++;
    	}
    	
    	//look for nodes with inDegree 0
    	//those are the start nodes for the topological sorting
    	//insert them into a queue
    	Queue<Integer> q = new LinkedList<Integer>();
    	for (i = 0; i < categories.size(); i++) {
    		if (inDegree[i] == 0) {
    			q.add(i);
    		}
    	}
    	
    	//create topological sorting
    	List<Category> topSort = new ArrayList<Category>();
    	while (!q.isEmpty()) {
    		Integer node = q.poll();
    		Category category = categories.get(node);
    		topSort.add(category);
    		
    		//delete edge in implicit graph
    		int parentId = category.getParentId();
    		if (parentId == 0) continue;
    		int parentPosition = idx.get(parentId);
    		inDegree[parentPosition]--;
    		
    		if (inDegree[parentPosition] == 0) {
    			q.add(parentPosition);
    		}
    	}
    	
    	return topSort;
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
        ret.tweets = tweets;
        ret.retweets = getRetweetSum(stmt, byDate);

        return ret;
    }

    private List<Retweets> getRetweetSum(Statement stmt, boolean byDate) {

        String a = "SELECT SUM(Counter), LocationId, Code, Day FROM retweets JOIN final ON retweets.AccountId=final.val JOIN day ON retweets.DayId=day.Id JOIN location ON retweets.LocationId=location.Id GROUP BY LocationId, DayId;";
        String b = "SELECT SUM(Counter), LocationId, Code FROM retweets JOIN final ON retweets.AccountId=final.val JOIN location ON retweets.LocationId=location.Id GROUP BY LocationId;";

        ResultSet res = null;
        runningRequest = true;
        try {
            // stmt.executeBatch();
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
                        : null), res.getInt(1), res.getInt("LocationId"));
                element.setLocationCode(res.getString("Code"));
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
    public List<Account> getAllData(Integer[] categoryIDs,
            Integer[] locationIDs, Integer[] accountIDs, boolean byDates)
            throws IllegalArgumentException, SQLException {

        Statement stmt = createBasicStatement(categoryIDs, locationIDs,
                accountIDs);

        return getTweetSumPerAccount(stmt, byDates);
    }

    private List<Account> getTweetSumPerAccount(Statement stmt, boolean byDate) {

        String a = "SELECT Counter, AccountName, tweets.AccountId, Day FROM tweets JOIN final ON tweets.AccountId=final.val JOIN day ON tweets.DayId=day.Id JOIN accounts ON final.val=accounts.Id;";
        String b = "SELECT SUM(Counter),AccountName, tweets.AccountId FROM tweets JOIN final ON tweets.AccountId=final.val JOIN accounts ON final.val=accounts.Id GROUP BY AccountId;";

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
            closeResult(res);
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

        String a = "SELECT Counter, retweets.LocationId, AccountId, Code, Day FROM retweets JOIN final ON retweets.AccountId=final.val JOIN day ON retweets.DayId=day.Id JOIN location ON retweets.LocationId=location.Id;";
        String b = "SELECT SUM(Counter), retweets.LocationId, AccountId, Code FROM retweets JOIN final ON retweets.AccountId=final.val JOIN location ON retweets.LocationId=location.Id GROUP BY LocationId, AccountId;";

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
                    Retweets element = new Retweets(
                            (byDate ? res.getDate("Day") : null),
                            res.getInt(1), res.getInt(2));
                    element.setLocationCode(res.getString("Code"));
                    temp.addRetweet(element);
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

    private Statement createBasicStatement(Integer[] categoryIDs,
            Integer[] locationIDs, Integer[] accountIDs) throws SQLException {

        boolean categoryIsSet = categoryIDs != null && categoryIDs.length > 0;
        boolean locationIsSet = locationIDs != null && locationIDs.length > 0;
        boolean accountIsSet = accountIDs != null && accountIDs.length > 0;
        if (!categoryIsSet && !locationIsSet && !accountIsSet) {
            throw new IllegalArgumentException();
        }

        Statement stmt = c.createStatement();

        stmt.addBatch("CREATE TEMPORARY TABLE IF NOT EXISTS final (val int PRIMARY KEY);");

        if (categoryIsSet || locationIsSet) {
            String c = "INSERT IGNORE INTO final (val) SELECT accounts.Id FROM accountCategory JOIN accounts ON accountCategory.AccountId=accounts.Id WHERE ";

            if (categoryIsSet) {
                c += "(CategoryId=" + categoryIDs[0];

                for (int i = 1; i < categoryIDs.length; i++) {
                    c += " OR CategoryId=" + categoryIDs[i];
                }
            }

            if (categoryIsSet && locationIsSet) {
                c += ") AND ";
            }

            if (locationIsSet) {
                c += "(LocationId=" + locationIDs[0];

                for (int i = 1; i < locationIDs.length; i++) {
                    c += " OR LocationId=" + locationIDs[i];
                }
            }

            c += ");";
            stmt.addBatch(c);
        }

        if (accountIsSet) {
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

}
