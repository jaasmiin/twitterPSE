package locate;

import twitter4j.Status;

public class StatusAccount {

    private Status status;
    private boolean tweet;

    public StatusAccount(Status status, boolean tweet) {
        super();
        this.status = status;
        this.tweet = tweet;
    }

    public Status getStatus() {
        return status;
    }

    public boolean isTweet() {
        return tweet;
    }

}
