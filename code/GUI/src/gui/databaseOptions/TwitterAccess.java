package gui.databaseOptions;

import java.util.List;

import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;

/**
 * Class that provides necessary Methods to get access to the TwitterSearchAPI
 * 
 * @author Matthias
 * 
 */
public class TwitterAccess {

    private Twitter twitter;

    /**
     * delivers users that matches the query
     * 
     * @param accountName
     *            string to look for
     * @return list of users that where delivered by 'Twitter' for the query or
     *         null if there is no user matching the query
     */
    public static List<User> getUser(String accountName) {
        ResponseList<User> list = null;
        try {
            list = TwitterFactory.getSingleton().searchUsers(accountName, 1);

        } catch (TwitterException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void main(String[] args) {
        System.out.println(TwitterAccess.getUser("katy").get(0).getName());
    }

}
