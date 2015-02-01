package gui;

/**
 * Class for labels of all GUI labels in buttons, labels, ...
 * 
 * @author Maximilian Awiszus
 * @version 1.0
 * 
 */
public class Labels {
    public static final String PSE_TWITTER = "PSE-Twitter";
    public static final String CATEGORY = "category";
    public static final String CATEGORIES = "categories";
    public static final String ACCOUNT = "account";
    public static final String ACCOUNTS = "accounts";
    public static final String LOCATION = "location";
    public static final String LOCATIONS = "locations";
    public static final String WORLD = "world";
    public static final String DB_CONNECTION_CLOSED = "Connection to database closed.";
    public static final String DB_CONNECTION_CLOSING = "Closing connection to database.";
    public static final String DB_CONNECTING = "Connecting to database.";

    public static final String CATEGORIES_LOADING = "Loading " + CATEGORIES
            + "...";
    public static final String CATEGORIES_LOADED = Util
            .getUppercaseStart(CATEGORIES) + " loaded.";
    public static final String LOCATIONS_LOADING = "Loading " + LOCATIONS
            + "...";
    public static final String LOCATIONS_LOADED = Util
            .getUppercaseStart(LOCATIONS) + " loaded.";
    public static final String ACCOUNTS_LOADING = "Loading " + ACCOUNTS + "...";
    public static final String ACCOUNTS_LOADED = Util
            .getUppercaseStart(ACCOUNTS) + " loaded.";
    public static final String DB_CONNECTION_ERROR = "Error, problem with the connection to databse.";
    public static final String ERROR = "Error.";
    public static final String DATA_LOADING = "Loading data...";
    public static final String DATA_LOADED = "Data loaded.";
    public static final String ERROR_NO_FILTER_SELECTED = "Can not load data. Please select minimum one filter.";
    protected static final String DB_CONNECTING_ERROR = "Error, could not connect to database.";
    protected static final String NO_LOGIN_DATA_FOUND_ERROR = "Error, could not find login data.";
    protected static final String DB_CONNECTED = "Connected to databse.";
}
