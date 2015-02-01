package gui;

import gui.GUIElement.UpdateType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

import mysql.AccessData;
import mysql.DBgui;
import mysql.result.Account;
import mysql.result.Category;
import mysql.result.Location;
import mysql.result.TweetsAndRetweets;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import twitter4j.User;
import unfolding.MyDataEntry;
import util.LoggerUtil;

/**
 * Supercontroller which provides information from db and informs subcontroller
 * on data changes.
 * 
 * @author Maximilian Awiszus and Paul Jungeblut
 * @version 1.0
 * 
 */


public class GUIController extends Application implements Initializable {

    public Category categoryRoot;

    private static GUIController instance = null;
    private static ArrayList<GUIElement> guiElements = new ArrayList<GUIElement>();
    private static DBgui db;

    @FXML
    private Pane paSelectionOfQuery;
    @FXML
    private TextField txtSearch;
    @FXML
    private ListView<String> lstInfo;

    private SelectionHashList<Location> locations = new SelectionHashList<Location>();
    private SelectionHashList<Account> accounts = new SelectionHashList<Account>();
    private TweetsAndRetweets dataByLocation = new TweetsAndRetweets();
    private List<Account> dataByAccount = new ArrayList<Account>();

    private HashSet<Integer> selectedCategories = new HashSet<Integer>();
    private HashMap<Integer, Category> categories = new HashMap<Integer, Category>();

    private boolean dateRange = false;
    private String accountSearchText = "";
    private MyDataEntry mapDetailInformation = null;

    public static GUIController getInstance() {
        if (instance == null) {
            System.out.println("Fehler in GUIController getInstance(). "
                    + "Application nicht gestartet. (instance == null).");
        }
        return instance;
    }

    /**
     * Create a GUIController and set the singelton instance.
     */
    public GUIController() {
        super();
        instance = this; // TODO: JavaFX creates two instances of GUIController?
        System.out.println("public GUIController()");
    }

    @Override
    public void start(final Stage primaryStage) {
        try {
            Parent parent = FXMLLoader.load(GUIController.class
                    .getResource("GUIView.fxml"));
            Scene scene = new Scene(parent, 800, 600);
            scene.getStylesheets().add(
                    getClass().getResource("application.css").toExternalForm());
            primaryStage.setTitle(Labels.PSE_TWITTER);
            primaryStage.setMinHeight(500);
            primaryStage.setMinWidth(600);
            primaryStage.setScene(scene);
            primaryStage.show();
            scene.getWindow().setOnCloseRequest(
                    new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent event) {
                            event.consume();
                            close();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Close the application and disconnect from db, if there has been a
     * connection.
     */
    public void close() {
        update(UpdateType.CLOSE);
        if (db != null && db.isConnected()) {
            System.out.println(Labels.DB_CONNECTION_CLOSING);
            db.disconnect();
            System.out.println(Labels.DB_CONNECTION_CLOSED);
        }
        Platform.exit();
    }

    /**
     * Get whether the GUIController is connected to the database.
     * 
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        return db != null && db.isConnected();
    }

    /**
     * Get if the application is ready meaning all locations, categories and
     * accounts are already loaded from db.
     * 
     * @return true if data is loaded, false otherwise
     */
    public boolean isReady() {
        return !locations.get().isEmpty() && !categories.isEmpty()
                && !accounts.get().isEmpty();
    }

    /**
     * Start the application.
     * 
     * @param args
     *            this parameter is not used
     */
    public static void main(String[] args) {
        launch();
    }

    private Runnable rnbInitDBConnection = new Runnable() {
        @Override
        public void run() {
            boolean success = true;
            String info = Labels.DB_CONNECTING;
            setInfo(info);
            AccessData accessData = null;
            try {
                accessData = getDBAccessData();
            } catch (IOException e1) {
                success = false;
            }
            if (success) {
                try {
                    db = new DBgui(accessData, LoggerUtil.getLogger());
                } catch (SecurityException | IOException
                        | InstantiationException | IllegalAccessException
                        | ClassNotFoundException e) {
                    e.printStackTrace();
                    success = false;
                }
                if (success) {
                    try {
                        db.connect();
                    } catch (SQLException e) {
                        success = false;
                    }
                } else {
                    setInfo(Labels.DB_CONNECTING_ERROR, info);
                }
            } else {
                setInfo(Labels.NO_LOGIN_DATA_FOUND_ERROR, info);
            }
            if (success) {
                setInfo(Labels.DB_CONNECTED, info);
                reloadAll();
            }
        }
    };

    private AccessData getDBAccessData() throws IOException {
        String path = System.getenv("APPDATA")
                + "\\KIT\\twitterPSE\\dblogin.txt";
        if (!(new File(path)).isFile()) {
            path = System.getenv("APPDATA") + "\\KIT";
            File file = new File(path);
            if (!file.isDirectory()) {
                if (!file.mkdir()) {
                    throw new IOException();
                }
            }
            path += "\\twitterPSE";
            file = new File(path);
            if (!file.isDirectory()) {
                if (!file.mkdir()) {
                    throw new IOException();
                }
            }
            path += "\\dblogin.txt";
            file = new File(path);
            if (!file.isFile()) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                Scanner in = new Scanner(System.in);
                System.out.print("Host: ");
                writer.write(in.nextLine() + "\n");
                System.out.print("Port: ");
                writer.write(in.nextLine() + "\n");
                System.out.print("Database: ");
                writer.write(in.nextLine() + "\n");
                System.out.print("Username: ");
                writer.write(in.nextLine() + "\n");
                System.out.print("Password: ");
                writer.write(in.nextLine() + "\n");
                in.close();
                writer.close();
            }

        }
        BufferedReader in = new BufferedReader(new FileReader(path));

        String host = in.readLine();
        String port = in.readLine();
        String dbName = in.readLine();
        String userName = in.readLine();
        String password = in.readLine();
        in.close();
        return new AccessData(host, port, dbName, userName, password);
    }

    private void reloadLocation() {
        String info = Labels.LOCATIONS_LOADING;
        setInfo(info);
        locations.removeAll();
        locations.updateAll(db.getLocations());
        update(UpdateType.LOCATION);
        setInfo(Labels.LOCATIONS_LOADED, info);
    }

    private void reloadAccounts() {
        String info = Labels.ACCOUNTS_LOADING;
        setInfo(info);
        accounts.removeAll();
        accounts.updateAll(db.getAccounts(accountSearchText));
        update(UpdateType.ACCOUNT);
        setInfo(Labels.ACCOUNTS_LOADED, info);
    }

    private void reloadCategories() {
        String info = Labels.CATEGORIES_LOADING;
        setInfo(info);
        categoryRoot = db.getCategories();
        if (categoryRoot != null) {
            reloadCategoryHashMap();
            update(UpdateType.CATEGORY);
            setInfo(Labels.CATEGORIES_LOADED, info);
        } else {
            categoryRoot = new Category(0, Labels.ERROR, 0, false);
            update(UpdateType.ERROR);
            setInfo(Labels.DB_CONNECTION_ERROR, info);
        }
    }

    private void reloadCategoryHashMap() {
        categories.clear();
        reloadCategoryHashMapRec(categoryRoot);
    }

    private void reloadCategoryHashMapRec(Category category) {
        for (Category child : category.getChilds()) {
            reloadCategoryHashMapRec(child);
        }
        categories.put(category.getId(), category);
    }

    /**
     * Get all childs of a category recursively including own id.
     * 
     * @param id
     *            of category
     * @return a set of all childs including own id or a empty set if id could
     *         not be found in categories HashMap
     */
    private Set<Integer> getSelectedChildCategories(int id) {
        Set<Integer> idList = new HashSet<Integer>();
        if (categories.containsKey(id)) {
            Queue<Category> categories = new LinkedList<Category>();
            categories.add(this.categories.get(id));
            while (!categories.isEmpty()) {
                Category category = categories.poll();
                idList.add(category.getId());
                categories.addAll(category.getChilds());
            }
        }
        return idList;
    }

    private void reloadData() {
        String info = Labels.DATA_LOADING;
        setInfo(info);
        List<Integer> selectedLocations = new ArrayList<Integer>();
        for (Location l : locations.getSelected()) {
            selectedLocations.add(l.getId());
        }
        List<Integer> selectedAccounts = new ArrayList<Integer>();
        for (Account a : accounts.getSelected()) {
            selectedAccounts.add(a.getId());
        }
        List<Integer> allSelectedCategories = new ArrayList<Integer>();
        for (Integer id : selectedCategories) {
            allSelectedCategories.addAll(getSelectedChildCategories(id));
        }

        if (allSelectedCategories.size() + selectedLocations.size()
                + selectedAccounts.size() >= 1) {
            boolean success = true;
            try {
                dataByLocation = db.getSumOfData(allSelectedCategories,
                        selectedLocations, selectedAccounts, dateRange);
                dataByAccount = db.getAllData(allSelectedCategories,
                        selectedLocations, selectedAccounts, dateRange);
            } catch (IllegalArgumentException e) {
                success = false;
                setInfo(Labels.DB_CONNECTION_ERROR, info);
            }
            if (success) {
                setInfo(Labels.DATA_LOADED, info);
                update(UpdateType.TWEET);
            }
        } else {
            dataByLocation = new TweetsAndRetweets();
            dataByAccount = new ArrayList<Account>();
            setInfo(Labels.ERROR_NO_FILTER_SELECTED, info);
            update(UpdateType.TWEET);
        }
    }

    /**
     * Reload accounts, categories and locations parallel from db.
     */
    private void reloadAll() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                reloadAccounts();
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                reloadCategories();
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                reloadLocation();
            }
        }).start();
    }

    /**
     * Display information in information list.
     * 
     * @param info
     *            which should be displayed
     */
    private void setInfo(final String info) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                lstInfo.getItems().removeAll(info);
                lstInfo.getItems().add(info);
            }
        });
    }

    /**
     * Remove the old information and display the new information for some
     * seconds.
     * 
     * @param info
     *            which should be displayed
     * @param oldInfo
     *            which should be removed
     */
    private void setInfo(final String info, final String oldInfo) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                lstInfo.getItems().remove(oldInfo);
                lstInfo.getItems().removeAll(info);
                lstInfo.getItems().add(info);
                Platform.runLater(new InfoRunnable(lstInfo, info));
            }
        });
    }

    /**
     * Get list of all categories
     * 
     * @return list of categories
     */
    public Category getCategoryRoot() {
        return categoryRoot;
    }

    /**
     * Get categories containing text
     * 
     * @param text
     *            which categories should contain
     * @return list of categories containing text
     */
    public Category getCategoryRoot(String text) {
        HashMap<Integer, Category> categories = new HashMap<Integer, Category>();
        Stack<Category> toVisit = new Stack<Category>();
        Category newRoot = new Category(categoryRoot.getId(),
                categoryRoot.toString(), categoryRoot.getParentId(),
                categoryRoot.isUsed());
        HashSet<Integer> foundCategories = new HashSet<Integer>();

        categories.put(categoryRoot.getId(), categoryRoot);
        for (Category category : categoryRoot.getChilds()) {
            toVisit.push(category);
        }

        while (!toVisit.isEmpty()) {
            Category category = toVisit.pop();
            categories.put(category.getId(),
                    new Category(category.getId(), category.toString(),
                            category.getParentId(), category.isUsed()));
            for (Category child : category.getChilds()) {
                toVisit.push(child);
            }
            if (category.toString().toLowerCase().trim()
                    .contains(text.toLowerCase().trim())) {
                foundCategories.add(category.getId());
            }
        }
        Iterator<Integer> iterator = foundCategories.iterator();
        while (iterator.hasNext()) {
            int categoryID = iterator.next();
            Stack<Integer> pathToRoot = new Stack<Integer>();
            pathToRoot.push(categoryID);
            while (pathToRoot.peek() != newRoot.getId()) {
                pathToRoot
                        .push(categories.get(pathToRoot.peek()).getParentId());
            }
            pathToRoot.pop(); // remove root
            Category nodeToAdd = newRoot;
            while (!pathToRoot.isEmpty()) {
                Category category = categories.get(pathToRoot.pop());
                if (!nodeToAdd.getChilds().contains(category)) {
                    nodeToAdd.addChild(category);
                }
                for (Category child : nodeToAdd.getChilds()) {
                    if (child.equals(category)) {
                        nodeToAdd = child;
                        break;
                    }
                }
            }
        }
        return newRoot;
    }

    /**
     * Creates a category tree containing all Categories given in 'CategoryIds'
     * 
     * @param CategoryIds
     * @return root of the created tree, null if CategoryIds contains invalid Id
     *         or has length 0
     */
    public Category getCategoryRoot(int[] categoryIds) {

        if (categoryIds == null || categoryIds.length == 0) {
            return null;
        }
        Category root = null;
        Category currentCat = null;
        Boolean first = true;

        // contains all vertex of the tree
        HashMap<Integer, Category> tree = new HashMap<Integer, Category>();

        // iterate over CategoryIds and create tree
        for (int i = 0; i < categoryIds.length; i++) {

            currentCat = getCategory(categoryIds[i]);

            if (currentCat == null) {
                return null;
            }
            // create new category with old values but without childs
            currentCat = new Category(currentCat.getId(),
                    currentCat.toString(), currentCat.getParentId(),
                    currentCat.isUsed());

            tree.put(currentCat.getId(), currentCat);

            while (currentCat.getId() != 1) {
                // while currenCat is not root
                Category parent = getCategory(currentCat.getParentId());
                if (parent == null) {
                    return null;
                }
                // create new category with old values but without childs
                parent = new Category(parent.getId(), parent.toString(),
                        parent.getParentId(), parent.isUsed());

                if (tree.containsKey(parent.getId())) {
                    // look up parentId in the hashMap if found add currentCat
                    // and break
                    tree.get(parent.getId()).addChild(currentCat);
                    break;
                } else {
                    // add currentCat and go on
                    parent.addChild(currentCat);
                    tree.put(parent.getId(), parent);
                    currentCat = parent;
                }
            }
            // hoping that categories form a tree, and root id is still 1 set in
            // the first run to the root the root item
            if (first) {
                root = currentCat;
                first = false;
            }

        }
        return root;
    }

    /**
     * Get accounts containing the text.
     * 
     * @param text
     *            with which should be compared incase-sensitively.
     * @return list of accounts containing text
     */
    public List<Account> getAccounts(String text) {
        if (!accountSearchText.equals(text)) {
            accountSearchText = text;
            reloadAccounts();
        }
        return accounts.get();
    }

    /**
     * Get list of all locations.
     * 
     * @return list of locations
     */
    public List<Location> getLocations() {
        return locations.get();
    }

    /**
     * Get locations containing text
     * 
     * @param text
     *            which locations should contain
     * @return list of locations containing text
     */
    public List<Location> getLocations(String text) {
        ArrayList<Location> filteredLocations = new ArrayList<Location>();
        for (Location location : locations.get()) {
            if (location.toString().toLowerCase().trim()
                    .contains(text.toLowerCase().trim())) {
                filteredLocations.add(location);
            }
        }
        return filteredLocations;
    }

    /**
     * Set of an account is selected.
     * 
     * @param id
     *            of account
     * @param selected
     *            is true if account should be selected, false otherwise
     */
    public void setSelectedAccount(int id, boolean selected) {
        if (accounts.setSelected(id, selected)) {
            update(UpdateType.ACCOUNT_SELECTION);
            reloadData();
        }
    }

    /**
     * Set if all categories in the list are selected or not. Update is called
     * after all categories are (de)selected.
     * 
     * @param ids
     *            of all categories
     * @param selected
     *            true if category should be selected, false otherwise
     */
    public void setSelectedCategory(Set<Integer> ids, boolean selected) {
        for (Integer id : ids) {
            setSelectedCategory(id, selected, false);
        }
        update(UpdateType.CATEGORY_SELECTION);
        reloadData();
    }

    /**
     * Set if a category is selected.
     * 
     * @param id
     *            of category
     * @param selected
     *            is true if category should be selected, false otherwise
     */
    public void setSelectedCategory(int id, boolean selected) {
        setSelectedCategory(id, selected, true);
    }

    private void setSelectedCategory(int id, boolean selected, boolean update) {
        if (selected) {
            selectedCategories.add(id);
        } else {
            selectedCategories.remove(id);
        }
        if (update) {
            update(UpdateType.CATEGORY_SELECTION);
            reloadData();
        }
    }

    /**
     * Set if a location is selected.
     * 
     * @param id
     *            of location
     * @param selected
     *            is true if location should be selected, false otherwise
     */
    public void setSelectedLocation(int id, boolean selected) {
        if (locations.setSelected(id, selected)) {
            update(UpdateType.LOCATION_SELECTION);
            reloadData();
        }
    }

    /**
     * Get list of all accounts.
     * 
     * @return a list of all accounts
     */
    public List<Account> getSelectedAccounts() {
        return accounts.getSelected();
    }

    /**
     * Get list of selected categories.
     * 
     * @return selected categories
     */
    public List<Category> getSelectedCategories() {
        List<Category> selectedCategoriesList = new ArrayList<Category>();
        for (Integer categoryID : selectedCategories) { // TODO: faster?
            selectedCategoriesList.add(categories.get(categoryID));
        }
        return selectedCategoriesList;
    }

    /**
     * Get list of selected locations.
     * 
     * @return selected locations
     */
    public List<Location> getSelectedLocations() {
        return locations.getSelected();
    }

    /**
     * Get data grouped by account.
     * 
     * @return data grouped by account or null
     */
    public List<Account> getDataByAccount() {
        return dataByAccount;
    }

    /**
     * Get data grouped by location.
     * 
     * @return data grouped by location or null
     */
    public TweetsAndRetweets getDataByLocation() {
        return dataByLocation;
    }

    /**
     * Get the account by id. Only accounts which are cached in the
     * GUIController are available meaning accounts displayed in
     * SelectionOfQuery account list.
     * 
     * @param id
     *            of account
     * @return account or null if no account is found
     */
    public Account getAccount(Integer id) {
        Account a = accounts.getElement(id);
        // if (a == null) {
        // a = db.getAccount(id);
        // }
        // TODO: uncomment, if @Holger has programmed method.
        return a;
    }

    /**
     * Get the category by id
     * 
     * @param id
     *            of category
     * @return category or null if no category is found
     */
    public Category getCategory(Integer id) {
        return categories.get(id);
    }

    /**
     * Get the location by id
     * 
     * @param id
     *            of location
     * @return location or null if no location is found
     */
    public Location getLocation(Integer id) {
        return locations.getElement(id);
    }

    /**
     * Set if date information should be included in data got from
     * getDataByAccount and getDataByLocation
     * 
     * @param dateRange
     *            is true if date should be included, false otherwise
     */
    public void setDateRange(boolean dateRange) {
        this.dateRange = dateRange;
    }

    /**
     * Adds user who's tweets the crawler will be listening.
     * 
     * @param user
     *            the twitter user
     * @param locationID
     *            of location from user
     */
    public void addUserToWatch(User user, int locationID) {
        db.addAccount(user, locationID);
    }

    /**
     * Set the detail information.
     * 
     * @param detailInfo
     */
    public void setMapDetailInformation(MyDataEntry detailInfo) {
        mapDetailInformation = detailInfo;
        update(UpdateType.MAP_DETAIL_INFORMATION);
    }

    /**
     * Get the detail information
     * 
     * @return detail information or null if not set
     */
    public MyDataEntry getMapDetailInformation() {
        return mapDetailInformation;
    }

    /**
     * Add a category to an user.
     * 
     * @param accountID
     *            of user
     * @param categoryID
     *            of category
     */
    public void setCategory(int accountID, int categoryID) {
        db.setCategory(accountID, categoryID);
    }

    /**
     * Add a location to an user.
     * 
     * @param accountID
     *            of user
     * @param locationID
     *            of location
     */
    public void setLocation(int accountID, int locationID) {
        db.setLocation(accountID, locationID);
    }

    private void update(UpdateType type) {
        Platform.runLater(new RunnableParameter<UpdateType>(type) {
            @Override
            public void run() {
                for (GUIElement element : guiElements) {
                    element.update(parameter);
                }
            }
        });
    }

    /**
     * Add a GUIElement as subscriber
     * 
     * @param element
     *            which will later be notified on update.
     */
    public void subscribe(GUIElement element) {
        guiElements.add(element);
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        update(UpdateType.GUI_STARTED);
        new Thread(rnbInitDBConnection).start();
    }

    /**
     * Get the sum of all retweets per location
     * 
     * @return HashMap with location code and the sum of retweets as integer.
     */
    public HashMap<String, Integer> getSumOfRetweetsPerLocation() {
        return db.getAllRetweetsPerLocation();
    }

	/**
	 * calculates the displayed value per country
	 * 
	 * given: a category, country, accounts combination
	 * calculates: 
	 *  
	 *   number of retweets for that combination in that country                      1
	 *   -------------------------------------------------------  *  ------------------------------------ * scale
	 *          number of retweets for that combination               number of retweets in that country
	 * 
	 * @param retweetsPerLocation number of retweets for each country in a category/country/accounts combination
	 * @param scale the value in scale is multiplied with the calculated relative factor to point out differences, it has to be positive
	 * @return the hashmap mapping countries to the number quantifying the 
	 * retweet activity in this country or null if retweetsPerLocation contained invalid countrycode identifier, or scale is not positive
	 */
	public HashMap<String, MyDataEntry> getDisplayValuePerCountry( HashMap<String, Integer> retweetsPerLocation, double scale ) {
		if (scale <= 0.0000000000001) {
			return null;
		}
	    HashMap<String, MyDataEntry> result = new HashMap<String, MyDataEntry>();
	    HashMap<String, Integer> totalNumberOfRetweets = getSumOfRetweetsPerLocation();
	    
	    // calculate overall number of retweets in this special combination
	    Set<String> keySet = retweetsPerLocation.keySet(); 
	    int overallCounter = 0;
	    for(String key : keySet) {
	    	overallCounter += retweetsPerLocation.get(key);		
	    }
	   
	    System.out.println("1/overall value: " + ((double)1) /overallCounter);
	    
	    // calculate relative value  
	    for(String key : keySet) {
	    	if (!totalNumberOfRetweets.containsKey(key)) {
	    		System.out.println("ERROR");
	    		return null;
	    	}
	    	double relativeValue = retweetsPerLocation.get(key) / ((double) overallCounter * totalNumberOfRetweets.get(key));
	    	relativeValue *= scale;
	    	result.put(key, new MyDataEntry(relativeValue, key, totalNumberOfRetweets.get(key), retweetsPerLocation.get(key)));
	    }
	    
	    return result;
	}


}
