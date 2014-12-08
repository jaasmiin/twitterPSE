package mysql;

/**
 * class to transmit and save the results from the database
 * 
 * @author Holger Ebhart
 * @version 1.0
 */
public class Result {

    private String name;
    private String url;
    private long id;

    /**
     * 
     * @param id
     * @param name
     * @param url
     */
    public Result(long id, String name, String url) {
        this.id = id;
        this.name = name;
        this.url = url;
    }

    /**
     * 
     * @return
     */
    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public long getId() {
        return id;
    }

}
