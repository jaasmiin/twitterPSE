package test;

import java.util.Date;

import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.User;

/**
 * class who implements twitter4j.user (necessary for testing)
 * 
 * @author Holger Ebhart
 * 
 */
public class MyUser implements User {

    private static final long serialVersionUID = 1L;
    private String name;
    private long id;
    private String timeZone;
    private String location;
    private String url;
    private int follower;
    private boolean isVerified;

    /**
     * create a new twitter4j.user
     * 
     * @param name
     *            the name of the user as String
     * @param id
     *            the twitter-id of the user as long
     * @param timeZone
     *            the timeZone of the user as String
     * @param location
     *            the location of the user as String
     * @param url
     *            the url of the website of the user as String
     * @param follower
     *            the number of follower of this user as int
     * @param isVerified
     *            true if the user has been verified, else false
     */
    public MyUser(String name, long id, String timeZone, String location,
            String url, int follower, boolean isVerified) {
        super();
        this.name = name;
        this.id = id;
        this.timeZone = timeZone;
        this.location = location;
        this.url = url;
        this.follower = follower;
        this.isVerified = isVerified;
    }

    @Override
    public int compareTo(User o) {
        return 0;
    }

    @Override
    public int getAccessLevel() {
        return 0;
    }

    @Override
    public RateLimitStatus getRateLimitStatus() {
        return null;
    }

    @Override
    public String getBiggerProfileImageURL() {
        return null;
    }

    @Override
    public String getBiggerProfileImageURLHttps() {
        return null;
    }

    @Override
    public Date getCreatedAt() {
        return new Date();
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public URLEntity[] getDescriptionURLEntities() {
        return null;
    }

    @Override
    public int getFavouritesCount() {
        return 0;
    }

    @Override
    public int getFollowersCount() {
        return follower;
    }

    @Override
    public int getFriendsCount() {
        return 0;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getLang() {
        return null;
    }

    @Override
    public int getListedCount() {
        return 0;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public String getMiniProfileImageURL() {
        return null;
    }

    @Override
    public String getMiniProfileImageURLHttps() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalProfileImageURL() {
        return null;
    }

    @Override
    public String getOriginalProfileImageURLHttps() {
        return null;
    }

    @Override
    public String getProfileBackgroundColor() {
        return null;
    }

    @Override
    public String getProfileBackgroundImageURL() {
        return null;
    }

    @Override
    public String getProfileBackgroundImageUrlHttps() {
        return null;
    }

    @Override
    public String getProfileBannerIPadRetinaURL() {
        return null;
    }

    @Override
    public String getProfileBannerIPadURL() {
        return null;
    }

    @Override
    public String getProfileBannerMobileRetinaURL() {
        return null;
    }

    @Override
    public String getProfileBannerMobileURL() {
        return null;
    }

    @Override
    public String getProfileBannerRetinaURL() {
        return null;
    }

    @Override
    public String getProfileBannerURL() {
        return null;
    }

    @Override
    public String getProfileImageURL() {
        return null;
    }

    @Override
    public String getProfileImageURLHttps() {
        return null;
    }

    @Override
    public String getProfileLinkColor() {
        return null;
    }

    @Override
    public String getProfileSidebarBorderColor() {
        return null;
    }

    @Override
    public String getProfileSidebarFillColor() {
        return null;
    }

    @Override
    public String getProfileTextColor() {
        return null;
    }

    @Override
    public String getScreenName() {
        return name;
    }

    @Override
    public Status getStatus() {
        return null;
    }

    @Override
    public int getStatusesCount() {
        return 0;
    }

    @Override
    public String getTimeZone() {
        return timeZone;
    }

    @Override
    public String getURL() {
        return url;
    }

    @Override
    public URLEntity getURLEntity() {
        return null;
    }

    @Override
    public int getUtcOffset() {
        return 0;
    }

    @Override
    public boolean isContributorsEnabled() {
        return false;
    }

    @Override
    public boolean isDefaultProfile() {
        return false;
    }

    @Override
    public boolean isDefaultProfileImage() {
        return false;
    }

    @Override
    public boolean isFollowRequestSent() {
        return false;
    }

    @Override
    public boolean isGeoEnabled() {
        return false;
    }

    @Override
    public boolean isProfileBackgroundTiled() {
        return false;
    }

    @Override
    public boolean isProfileUseBackgroundImage() {
        return false;
    }

    @Override
    public boolean isProtected() {
        return false;
    }

    @Override
    public boolean isShowAllInlineMedia() {
        return false;
    }

    @Override
    public boolean isTranslator() {
        return false;
    }

    @Override
    public boolean isVerified() {
        return isVerified;
    }

}
