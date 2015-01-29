package mysql.result;

import java.util.Date;

/**
 * store the tweets of a day
 * 
 * @author Holger Ebhart
 * @version 1.0
 * 
 */
public class Tweets {

	private Date date;
	private int counter;
	private String locationCode;

	/**
	 * store the tweets of a day
	 * 
	 * @param date
	 *            the date of the tweets as Date
	 * @param counter
	 *            the number of localized tweets as int
	 */
	public Tweets(Date date, int counter) {
		this.date = date;
		this.counter = counter;
		this.locationCode = "";
	}

	/**
	 * returns the date of the tweets
	 * 
	 * @return the date of the tweets as Date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * returns the number of tweets
	 * 
	 * @return the number of tweets as int
	 */
	public int getCounter() {
		return counter;
	}

	/**
	 * returns the location code of this retweets
	 * 
	 * @return the location code of this retweets as String (max. 3 chars)
	 */
	public String getLocationCode() {
		return locationCode;
	}

	/**
	 * sets the location code of the location of this retweets
	 * 
	 * @param locationCode
	 *            ths location code to set as String (max. 3 chars)
	 */
	public void setLocationCode(String locationCode) {
		if (locationCode != null && locationCode.length() > 3) {
			this.locationCode = locationCode.substring(0, 3);
		} else {
			this.locationCode = locationCode;
		}
	}

}
