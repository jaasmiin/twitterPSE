package main;

import java.util.Date;

public class StatusRetweet {

    private long id;
    private Date date;
    private String location;
    private String timeZone;

    public StatusRetweet(long id, Date date, String location, String timeZone) {
        super();
        this.id = id;
        this.date = date;
        this.location = location;
        this.timeZone = timeZone;
    }

    public long getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public String getTimeZone() {
        return timeZone;
    }

}
