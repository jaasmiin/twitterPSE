package gui.table;

import java.util.HashMap;
import java.util.List;

import mysql.result.Retweets;

/**
 * This class is a simplification of class accounts in mysql.result,
 * needed for faster computing of the table
 * 
 * @author Philipp
 *
 */
class InternAccount {

	/**
	 * The name of this account.
	 */
	private String accountName;
	
	/**
	 * The number of followers of this account.
	 */
	private int follower;
	
	/**
	 * The sum of retweets to tweets of this account in all locations.
	 */
	private int totalRetweets;
	
	/**
	 * This HashMap maps the locationCode of a retweets object
	 * to it's counter value, which contains the sum of 
	 * retweets to tweets of this account in that location. 
	 */
	private HashMap<String, Integer> retweetsPerLocation;
	
	
	/**
	 * Generates an instance of InternAccount.
	 * 
	 * @param accountName the name of the account 
	 * @param follower the number of followers of that account
	 * @param retweetList a list containing one retweet object per location containing
	 * 		  the sum of retweets to tweets of that account in that location
	 */
	InternAccount(String accountName, int follower, List<Retweets> retweetList) {
		this.accountName = accountName;
		this.follower = follower;
		this.totalRetweets = 0;
		
		// optimize number of rehashs
		double initialCapacity = retweetList.size() * (1 / 0.75);
		retweetsPerLocation = new HashMap<>((int) initialCapacity);
		
		for (Retweets r : retweetList) {
			int counter = r.getCounter();
			totalRetweets += counter;
			retweetsPerLocation.put(r.getLocationCode(), counter);			
		}
	}

	/**
	 * Gets the name of this account.
	 * 
	 * @return the name of this account
	 */
	String getAccountName() {
		return accountName;
	}

	/**
	 * Gets the number of followers of this account.
	 * 
	 * @return the number of followers of this account.
	 */
	int getFollower() {
		return follower;
	}


	/**
	 * Gets the number of retweets in a specified location.
	 * 
	 * @param locationCode a unique identifier for that specified location
	 * @return the number of retweets in that location
	 */
	int getRetweetNumber(String locationCode) {
		int result = 0;		
		
		Integer retweetNumber = retweetsPerLocation.get(locationCode);
		if (retweetNumber != null) {
			result = retweetNumber;
		}
		
		return result;
	}

	/**
	 * Gets the total number of retweets to tweets of this account.
	 * 
	 * @return the total number of retweets to tweets of this account
	 */
	int getTotalRetweets() {
		return totalRetweets;
	}

}
