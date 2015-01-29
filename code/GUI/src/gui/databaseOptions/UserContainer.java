package gui.databaseOptions;

import twitter4j.User;
/**
 * wraps an twitter4j user
 * @author Matthias
 *
 */
public class UserContainer {
	private User user;
	/**
	 * instantiates new UserWraper
	 * @param user User to wrap
	 */
	public UserContainer(User user) {
		this.user = user;
	}
	@Override
	public String toString() {
		return user.getScreenName();
	}
	/**
	 * Returns wrapped user
	 * @return wrapped user
	 */
	public User getUser() {
		return user;
	}
}
