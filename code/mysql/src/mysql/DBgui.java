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
    public List<Account> getAllData(List<Integer> categoryIDs, List<Integer> locationIDs, List<Integer> accountIDs, boolean byDate) {
    	List<Account> accounts = new ArrayList<Account>();
    	
    	String sqlCommand = "SELECT a.Id AS AccountId, a.TwitterAccountId, a.AccountName, "
    			+ "a.Verified, a.URL, a.Follower, a.LocationId, ";
    	if (byDate) {
    		sqlCommand += "rd.Day AS Day, ";
    	}
    	sqlCommand += "SUM(r.Counter) AS SumOfRetweets, SUM(t.Counter) AS SumOfTweets "
    			    + "FROM accounts a "
		    			+ "LEFT JOIN accountCategory ac ON a.Id = ac.AccountId "
		    			+ "LEFT JOIN location al ON a.LocationId = al.Id "
		    			+ "LEFT JOIN retweets r ON a.Id = r.AccountId "
		    				+ "LEFT JOIN day rd ON r.DayID = rd.Id "
	    				+ "LEFT JOIN tweets t ON a.Id = t.AccountId "
		    				+ "LEFT JOIN day td ON t.DayID = td.Id ";
    	if (!categoryIDs.isEmpty() || !locationIDs.isEmpty() || !accountIDs.isEmpty()) {
    		sqlCommand += "WHERE ";
	    	if (!categoryIDs.isEmpty()) {
	    		sqlCommand += "ac.CategoryId IN (";
	    		for (Integer id : categoryIDs) {
	    			sqlCommand += id + ",";
	    		}
	    		sqlCommand = sqlCommand.substring(0, sqlCommand.length() - 1) + ") " + sqlCommand.substring(sqlCommand.length(), sqlCommand.length());
	    	}
	    	if (!locationIDs.isEmpty()) {
	    		if (!categoryIDs.isEmpty()) {
	    			sqlCommand += "and ";
	    		}
	    		sqlCommand += "rl.Id IN (";
	    		for (Integer id : locationIDs) {
	    			sqlCommand += id + ",";
	    		}
	    		sqlCommand = sqlCommand.substring(0, sqlCommand.length() - 1) + ") " + sqlCommand.substring(sqlCommand.length(), sqlCommand.length());
	    	}
	    	if (!accountIDs.isEmpty()) {
	    		if (!locationIDs.isEmpty() || !categoryIDs.isEmpty()) {
	    			sqlCommand += "and ";
	    		}
	    		sqlCommand += "a.Id IN (";
	    		for (Integer id : accountIDs) {
	    			sqlCommand += id + ",";
	    		}
	    		sqlCommand = sqlCommand.substring(0, sqlCommand.length() - 1) + ") " + sqlCommand.substring(sqlCommand.length(), sqlCommand.length());
	    	}
    	}
    	if (byDate) {
    		sqlCommand += !categoryIDs.isEmpty() || !locationIDs.isEmpty() || !accountIDs.isEmpty() ? "WHERE " : "AND "
    			+ "rd.Id=td.Id ";
    	}
    	sqlCommand += "GROUP BY a.AccountName";
    	if (byDate) {
    		sqlCommand += ", rd.Day ";
    	}
    	sqlCommand += " ORDER BY SUM(r.Counter) DESC;";
        ResultSet rs = null;
        Statement stmt = null;
        runningRequest = true;
        try {
            stmt = c.createStatement();
            rs = stmt.executeQuery(sqlCommand);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
            return accounts;
        } finally {
            runningRequest = false;
        }
        
        try {
            while (rs.next()) {
            	Account a = new Account(rs.getInt("AccountId"), rs.getLong("TwitterAccountId"),
            			rs.getString("AccountName"), rs.getBoolean("Verified"), rs.getString("url"), 
            			rs.getInt("Follower"), rs.getInt("LocationId"));
            	a.addRetweet(new Retweets(byDate ? rs.getDate("Day") : null, rs.getInt("SumOfRetweets"), rs.getInt("LocationId")));
            	a.addTweet(new Tweets(byDate ? rs.getDate("Day") : null, rs.getInt("SumOfTweets")));
            	accounts.add(a);
            }
        } catch (SQLException e) {
            sqlExceptionResultLog(e);
            return new ArrayList<Account>();
        } finally {
            closeResultAndStatement(stmt, rs);
        }
        
    	return accounts;
    }
    
    @Override
    public TweetsAndRetweets getSumOfData(List<Integer> categoryIDs, List<Integer> locationIDs, List<Integer> accountIDs, boolean byDate) {
    	TweetsAndRetweets tweetsAndRetweets = new TweetsAndRetweets();
    	tweetsAndRetweets.retweets = getRetweetSum(categoryIDs, locationIDs, accountIDs, byDate);
    	tweetsAndRetweets.tweets = getTweetSum(categoryIDs, locationIDs, accountIDs, byDate);
    	return tweetsAndRetweets;
    }

    private List<Retweets> getRetweetSum(List<Integer> categoryIDs, List<Integer> locationIDs, List<Integer> accountIDs, boolean byDate) {
    	String sqlCommand = "SELECT rl.Id AS LocationId, rl.Code AS LocationCode, ";
    	if (byDate) {
    		sqlCommand += "rd.Day AS Day, ";
    	}
    	sqlCommand += "SUM(r.Counter) AS SumOfRetweets "
    			    + "FROM accounts a "
		    			+ "LEFT JOIN accountCategory ac ON a.Id = ac.AccountId "
		    			+ "LEFT JOIN location al ON a.LocationId = al.Id "
		    			+ "INNER JOIN retweets r ON a.Id = r.AccountId "
		    				+ "LEFT JOIN day rd ON r.DayID = rd.Id "
		    				+ "LEFT JOIN location rl ON r.LocationId = rl.Id ";
    	if (!categoryIDs.isEmpty() || !locationIDs.isEmpty() || !accountIDs.isEmpty()) {
    		sqlCommand += "WHERE ";
	    	if (!categoryIDs.isEmpty()) {
	    		sqlCommand += "ac.CategoryId IN (";
	    		for (Integer id : categoryIDs) {
	    			sqlCommand += id + ",";
	    		}
	    		sqlCommand = sqlCommand.substring(0, sqlCommand.length() - 1) + ") " + sqlCommand.substring(sqlCommand.length(), sqlCommand.length());
	    	}
	    	if (!locationIDs.isEmpty()) {
	    		if (!categoryIDs.isEmpty()) {
	    			sqlCommand += "and ";
	    		}
	    		sqlCommand += "rl.Id IN (";
	    		for (Integer id : locationIDs) {
	    			sqlCommand += id + ",";
	    		}
	    		sqlCommand = sqlCommand.substring(0, sqlCommand.length() - 1) + ") " + sqlCommand.substring(sqlCommand.length(), sqlCommand.length());
	    	}
	    	if (!accountIDs.isEmpty()) {
	    		if (!locationIDs.isEmpty() || !categoryIDs.isEmpty()) {
	    			sqlCommand += "and ";
	    		}
	    		sqlCommand += "a.Id IN (";
	    		for (Integer id : accountIDs) {
	    			sqlCommand += id + ",";
	    		}
	    		sqlCommand = sqlCommand.substring(0, sqlCommand.length() - 1) + ") " + sqlCommand.substring(sqlCommand.length(), sqlCommand.length());
	    	}
    	}
    	sqlCommand += "GROUP BY rl.Id";
    	if (byDate) {
    		sqlCommand += ", rd.Day ";
    	}
    	sqlCommand += " ORDER BY SUM(r.Counter) DESC;";
    	
    	List<Retweets> retweets = new ArrayList<Retweets>();
        ResultSet rs = null;
        Statement stmt = null;
        runningRequest = true;
        try {
            stmt = c.createStatement();
            rs = stmt.executeQuery(sqlCommand);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
            return retweets;
        } finally {
            runningRequest = false;
        }
        
        try {
            while (rs.next()) {
            	Retweets r = new Retweets(byDate ? rs.getDate("Day") : null, rs.getInt("SumOfRetweets"), rs.getInt("LocationId"));
            	r.setLocationCode(rs.getString("LocationCode"));
            	retweets.add(r);
            }
        } catch (SQLException e) {
            sqlExceptionResultLog(e);
            return new ArrayList<Retweets>();
        } finally {
            closeResultAndStatement(stmt, rs);
        }
        
        return retweets;
    }
    
    private List<Tweets> getTweetSum(List<Integer> categoryIDs, List<Integer> locationIDs, List<Integer> accountIDs, boolean byDate) {
    	String sqlCommand = "SELECT SUM(t.Counter) AS SumOfTweets"; 
    	if (byDate) {
    		sqlCommand += ", rd.Day AS Day";
    	}
    	sqlCommand += " FROM accounts a "
		    			+ "LEFT JOIN accountCategory ac ON a.Id = ac.AccountId "
		    			+ "LEFT JOIN location al ON a.LocationId = al.Id "
		    			+ "INNER JOIN tweets t ON a.Id = t.AccountId "
		    				+ "LEFT JOIN day rd ON r.DayID = rd.Id ";
    	if (!categoryIDs.isEmpty() || !locationIDs.isEmpty() || !accountIDs.isEmpty()) {
    		sqlCommand += "WHERE ";
	    	if (!categoryIDs.isEmpty()) {
	    		sqlCommand += "ac.CategoryId IN (";
	    		for (Integer id : categoryIDs) {
	    			sqlCommand += id + ",";
	    		}
	    		sqlCommand = sqlCommand.substring(0, sqlCommand.length() - 1) + ") " + sqlCommand.substring(sqlCommand.length(), sqlCommand.length());
	    	}
	    	if (!locationIDs.isEmpty()) {
	    		if (!categoryIDs.isEmpty()) {
	    			sqlCommand += "and ";
	    		}
	    		sqlCommand += "al.Id IN (";
	    		for (Integer id : locationIDs) {
	    			sqlCommand += id + ",";
	    		}
	    		sqlCommand = sqlCommand.substring(0, sqlCommand.length() - 1) + ") " + sqlCommand.substring(sqlCommand.length(), sqlCommand.length());
	    	}
	    	if (!accountIDs.isEmpty()) {
	    		if (!locationIDs.isEmpty() || !categoryIDs.isEmpty()) {
	    			sqlCommand += "and ";
	    		}
	    		sqlCommand += "a.Id IN (";
	    		for (Integer id : accountIDs) {
	    			sqlCommand += id + ",";
	    		}
	    		sqlCommand = sqlCommand.substring(0, sqlCommand.length() - 1) + ") " + sqlCommand.substring(sqlCommand.length(), sqlCommand.length());
	    	}
    	}
    	sqlCommand += ";";
    	
    	List<Tweets> tweets = new ArrayList<Tweets>();
        ResultSet rs = null;
        Statement stmt = null;
        runningRequest = true;
        try {
            stmt = c.createStatement();
            rs = stmt.executeQuery(sqlCommand);
        } catch (SQLException e) {
            sqlExceptionLog(e, stmt);
            return tweets;
        } finally {
            runningRequest = false;
        }
        
        try {
            if (rs.next()) {
            	Tweets t = new Tweets(byDate ? rs.getDate("Day") : null, rs.getInt("SumOfRetweets"));
            	tweets.add(t);
            }
        } catch (SQLException e) {
            sqlExceptionResultLog(e);
            e.printStackTrace();
            return new ArrayList<Tweets>();
        } finally {
            closeResultAndStatement(stmt, rs);
        }
        
        return tweets;
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
