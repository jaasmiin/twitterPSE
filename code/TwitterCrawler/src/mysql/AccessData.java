package mysql;

/**
 * class to save access data for a mysql database
 * 
 * @author Holger Ebhart
 * @version1.0
 */
public class AccessData {
    private final String hostname;
    private final String port;
    private final String db;
    private final String user;
    private final String pw;

    /**
     * set access data for a mysql database
     * 
     * @param hostName
     *            the hostname of the mysql database as String
     * @param port
     *            the port of the mysql database as String
     * @param dbName
     *            the database-name as String
     * @param userName
     *            the user name for the mysql database as String
     * @param password
     *            the password for the mysql database as String
     */
    public AccessData(String hostName, String port, String dbName,
            String userName, String password) {
        // set acces-data
        this.hostname = hostName;
        this.port = port;
        this.db = dbName;
        this.user = userName;
        this.pw = password;
    }

    /**
     * return the hostname for the database
     * 
     * @return the hostname for the database as String
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * return the port for the database
     * 
     * @return the port for the database as String
     */
    public String getPort() {
        return port;
    }

    /**
     * return the name for the database
     * 
     * @return the name for the database as String
     */
    public String getName() {
        return db;
    }

    /**
     * return the user for the database
     * 
     * @return the user for the database as String
     */
    public String getUser() {
        return user;
    }

    /**
     * return the password for the database
     * 
     * @return the password for the database as String
     */
    public String getPassword() {
        return pw;
    }

}
