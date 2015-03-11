package test.gui;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import mysql.result.Account;
import mysql.result.Location;
import mysql.result.Retweets;
import mysql.result.Tweets;
import gui.GUIController;
import gui.GUIElement;
import gui.GUIElement.UpdateType;
import gui.table.ContentTableController;

/**
 * This class is a simplified version of GUIController
 * for testing of ContentTableController.
 * 
 * It contains the locations Germany, France, Ireland, United States of America and Great Britain,
 * and Accounts can be chosen from:
 * TestAccount0 to TestAccount4.
 * These accounts have got 10*i tweets and retweets, as well as 100*i follower.
 * 
 * @author Philipp
 *
 */
public class GuiControllerMock extends GUIController {

	private ContentTableController subscriber;
	
	private List<Account> selectedAccounts;
	
	private Account[] accountsInDB;
	private Location[] locations;
	
	private String[] names = new String[]{"Germany", "France", "Ireland", 
			"United States of America", "Great Britain"};
	
	/**
	 * Creates an instacne of GuiControllerMock.
	 */
	public GuiControllerMock() {
		super();
		selectedAccounts = new LinkedList<Account>();
		
		locations = new Location[5];
		String[] codes = new String[]{"GER", "FRA", "IRL", "USA", "GBR"};

		for (int i = 0; i < 5; i++) {
			locations[i] = new Location(i, names[i], codes[i]);
		}
		
		accountsInDB = new Account[5];
    	long twitterId = 0;
    	String name = "TestAccount";
    	boolean verified = true;
    	String url = null;
    	int follower = 100;
    	List<Integer> categoryIds = null;
    	List<Tweets> tweets = new LinkedList<Tweets>();
    	List<Retweets> retweets = new LinkedList<Retweets>();       	
    	for (int i = 0; i < 5; i++) {
    		tweets.clear();
    		tweets.add(new Tweets(null, i * 10));
    		retweets.clear();
    		retweets.add(new Retweets(null, i * 10, codes[i]));
    		accountsInDB[i] = new Account(i, twitterId, name + i, verified, url, follower * i,
    				codes[i], categoryIds, tweets, retweets);
    	}
	}
	
	@Override
	public void subscribe(GUIElement element) {
		if (element instanceof ContentTableController) {
			subscriber = (ContentTableController) element;
		}
	}
	
	@Override
	public void setSelectedAccount(int id, boolean selected) {
		if (subscriber == null) {
			throw new IllegalStateException("Subscriber not set!");
		}
		
		if (selected && !selectedAccounts.contains(accountsInDB[id])) {
			selectedAccounts.add(accountsInDB[id]);
			subscriber.update(UpdateType.TWEET_BY_ACCOUNT);
		} else if (!selected) {
			selectedAccounts.remove(accountsInDB[id]);
			subscriber.update(UpdateType.TWEET_BY_ACCOUNT);
		}
		
	}
	
	@Override
	public List<Account> getDataByAccount() {
		return selectedAccounts;		
	}
	
	@Override
	public List<Location> getLocations() {
		return Arrays.asList(locations);
	}
	
	/**
	 * Gets an array containing the names of the locations this mock knows.
	 * 
	 * @return an array containing the names of the locations this mock knows
	 */
	public String[] getLocationNames() {
		return names; 
	}
	
	/**
	 * Clears the list of selected accounts.
	 */
	public void clear() {
		if (selectedAccounts != null) {
			selectedAccounts.clear();
		}
	}
	
}
