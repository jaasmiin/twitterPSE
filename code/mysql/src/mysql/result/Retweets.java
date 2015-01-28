package mysql.result;

import java.util.Date;

/**
 * store the retweets of a day
 * 
 * @author Holger Ebhart
 * @version 1.0
 */
public class Retweets extends Tweets {

	private int locationId;

	/**
	 * store the retweets of a day
	 * 
	 * @param date
	 *            the date of the retweets as Date
	 * @param counter
	 *            the number of localized retweets as int
	 * @param locationId
	 *            the id of the location of this retweets as int
	 */
	public Retweets(Date date, int counter, int locationId) {
		super(date, counter);
		this.locationId = locationId;
	}

	/**
	 * returns the locationId of this retweets
	 * 
	 * @return the locationId of this retweets as int
	 */
	public int getLocation() {
		return locationId;
	}

}
