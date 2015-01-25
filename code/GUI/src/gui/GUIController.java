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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.Stack;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import twitter4j.User;

public class GUIController extends Application implements Initializable {
	@FXML
	private Pane paSelectionOfQuery;
	@FXML
	private TextField txtSearch;
	@FXML
	private Label lblInfo;
	
	private static GUIController instance = null;
	private ArrayList<GUIElement> guiElements = new ArrayList<GUIElement>();
	
	private static DBgui db;
	private Category categoryRoot;
	private SelectionHashList<Location> locations = new SelectionHashList<Location>();
	private SelectionHashList<Account> accounts = new SelectionHashList<Account>();
	private TweetsAndRetweets dataByLocation = new TweetsAndRetweets();
	private List<Account> dataByAccount = new ArrayList<Account>();
	
	private HashSet<Integer> selectedCategories = new HashSet<Integer>();
	private HashMap<Integer, Category> categories = new HashMap<Integer, Category>();
	private Date selectedStartDate, selectedEndDate;
	private String accountSearchText = "";
	private String errorMessage = "";
	private boolean ready = false; 
	public static GUIController getInstance() {
		if (instance == null) {
			System.out.println("Fehler in GUIController getInstance(). Application nicht gestartet. (instance == null).");
		}
		return instance;
	}
	
	public GUIController() {
		super();
		instance = this; // TODO: JavaFX creates two instances of GUIController?
		System.out.println("public GUIController()");
	}
	
	@Override
	public void start(final Stage primaryStage) {
			try {
				Parent parent = FXMLLoader.load(GUIController.class.getResource("GUIView.fxml"));
				Scene scene = new Scene(parent, 800, 600);
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				primaryStage.setTitle("PSE-Twitter");
				primaryStage.setMinHeight(400);
				primaryStage.setMinWidth(600);
				primaryStage.setScene(scene);
				primaryStage.show();
				scene.getWindow().setOnCloseRequest(new EventHandler<WindowEvent>() {
					@Override
					public void handle(WindowEvent event) {
						close();
					}
				});
			} catch(Exception e) {
				e.printStackTrace();
			}
	}
	public void close() {
		if (db != null && db.isConnected()) {
			System.out.println("Verbindung mit Datenbank wird geschlossen...");
			db.disconnect();
			System.out.println("Verbindung geschlossen.");
		}
		Platform.exit();
	}
	
	public boolean isConnected() {
		return db != null && db.isConnected();
	}
	public boolean isReady() {
		return ready;
	}
	public static void main(String[] args) {
		launch(args);
	}
	
	private Runnable rnbInitDBConnection = new Runnable() {
		@Override
		public void run() {
			boolean success = true;
			lblInfo.setText("Verbindung mit DB wird aufgebaut...");
			AccessData accessData = null;
			try {
				accessData = getDBAccessData();
			} catch (IOException e1) {
				success = false;
			}
			if (success) {
				try {
					db = new DBgui(accessData, getLogger() );
				} catch (SecurityException | IOException | InstantiationException | IllegalAccessException
						| ClassNotFoundException e) {
					success = false;
				}
				if (success) {
					try {
						db.connect();
					} catch (SQLException e) {
	//					e.printStackTrace();
						success = false;
					}
				} else {
					setInfo("Fehler, es konnte keine Verbindung zur DB hergestellt werden.");
				}
			} else {
				setInfo("Fehler, es konnten konnten keine Login Daten geladen werden.");
			}
			if (success) {
				setInfo("Erfolreich mit DB verbunden.");
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						reloadAll();
					}
				});
			}
		}
	};
    
	private AccessData getDBAccessData() throws IOException {
		String path = System.getenv("APPDATA") + "\\KIT\\twitterPSE\\dblogin.txt";
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
		BufferedReader in = new BufferedReader(new FileReader(path));;
		String host = in.readLine();
		String port = in.readLine();
		String dbName = in.readLine();
		String userName = in.readLine();
		String password = in.readLine();
		in.close();
		return new AccessData(host, port, dbName, userName, password);
	}
	
	private Logger getLogger() throws SecurityException, IOException {
		File directory = new File("logs");
		if (!directory.isDirectory()) {
			directory.mkdir();
		}
        Logger l = Logger.getLogger("logger");
        new File(directory.getPath() + "\\LogFile.log").createNewFile();
        FileHandler fh = new FileHandler(directory.getPath() + "\\LogFile.log", true);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
        l.addHandler(fh);
        // true: print output on console and into file
        // false: only store output in logFile
        l.setUseParentHandlers(false);
        return l;
    }
	
	private void reloadLocation() {
		locations.clear();
		locations.addAll(db.getLocations());
		update(UpdateType.LOCATION);
	}
	
	private void reloadAccounts() {
		accounts.clear();
		accounts.addAll(db.getAccounts(accountSearchText));
		update(UpdateType.ACCOUNT);
	}
	
	private void reloadCategories() {		
		categoryRoot = db.getCategories();
		if (categoryRoot != null) {
			reloadCategoryHashMap();
			update(UpdateType.CATEGORY);
		} else {
			categoryRoot = new Category(0, "Fehler", 0, false);
			errorMessage = "Fehler bei der Kommunikation mir der Datenbank.";
			update(UpdateType.ERROR);
			setInfo(errorMessage);
		}
	}
	
	private void reloadCategoryHashMap() {
		categories.clear();
		reloadCategoryHashMap(categoryRoot);
	}
	private void reloadCategoryHashMap(Category category) {
		for (Category child : category.getChilds()) {
			reloadCategoryHashMap(child);	
		}
		categories.put(category.getId(), category);
	}
	
	private void reloadData() {
		Integer[] selectedCategoriesArray = selectedCategories.toArray(new Integer[selectedCategories.size()]);
		List<Location> selectedLocations = locations.getSelected();
		Integer[] selectedLocationsArray = new Integer[selectedLocations.size()];
		int i = 0;
		for (Location l : selectedLocations) {
			selectedLocationsArray[i++] = l.getId();
		}
		List<Account> selectedAccounts = accounts.getSelected();
		Integer[] selectedAccountsArray = new Integer[selectedAccounts.size()];
		i = 0;
		for (Account a : selectedAccounts) {
			selectedAccountsArray[i++] = a.getId();
		}
		if (selectedCategoriesArray.length + selectedLocationsArray.length + selectedAccountsArray.length >= 1) {

			boolean dateSelected = selectedStartDate != null && selectedEndDate != null;
			boolean success = true;
			try {
				dataByLocation = db.getSumOfData(selectedCategoriesArray, selectedLocationsArray, selectedAccountsArray, dateSelected);
				dataByAccount = db.getAllData(selectedCategoriesArray, selectedLocationsArray, selectedAccountsArray, dateSelected);
			} catch (IllegalArgumentException | SQLException e) {
				success = false;
				setInfo(e.getMessage());
			}
			if (success) {
				update(UpdateType.TWEET);
			}
		}
	}
	
	private void reloadAll() {
		reloadAccounts();
		reloadCategories();
		reloadLocation();
		ready = true;
	}
	
	private void setInfo(String text) {
		Platform.runLater(new InfoRunnable(lblInfo, text));
	}
	
	/**
	 * Get list of all categories
	 * @return list of categories
	 */
	public Category getCategoryRoot() {
		return categoryRoot;
	}
	
	/**
	 * Get categories containing text
	 * @param text which categories should contain
	 * @return list of categories containing text
	 */
	public Category getCategoryRoot(String text) {
		HashMap<Integer, Category> categories = new HashMap<Integer, Category>();
		Stack<Category> toVisit = new Stack<Category>();
		Category newRoot = new Category(categoryRoot.getId(), categoryRoot.toString(), categoryRoot.getParentId(), categoryRoot.isUsed());
		HashSet<Integer> foundCategories = new HashSet<Integer>();
		
		categories.put(categoryRoot.getId(), categoryRoot);
		for (Category category : categoryRoot.getChilds()) {
			toVisit.push(category);
		}
		
		while (!toVisit.isEmpty()) {
			Category category = toVisit.pop();
			categories.put(category.getId(), new Category(category.getId(), category.toString(), category.getParentId(), category.isUsed()));
			for (Category child : category.getChilds()) {
				toVisit.push(child);
			}
			if (category.toString().toLowerCase().trim().contains(text.toLowerCase().trim())) {
				foundCategories.add(category.getId());
			}
		}
		Iterator<Integer> iterator = foundCategories.iterator();
		while (iterator.hasNext()) {
			int categoryID = iterator.next();
			Stack<Integer> pathToRoot = new Stack<Integer>();
			pathToRoot.push(categoryID);
			while (pathToRoot.peek() != newRoot.getId()) {
				pathToRoot.push(categories.get(pathToRoot.peek()).getParentId());
			}
			pathToRoot.pop(); // remove root
			Category nodeToAdd = newRoot;
			while(!pathToRoot.isEmpty()) {
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
	
	public List<Account> getAccounts(String text) {
		if (!accountSearchText.equals(text)) {
			accountSearchText = text;
			reloadAccounts();
		}
		return accounts.get();
	}
	
	/**
	 * Get list of all locations.
	 * @return list of locations
	 */
	public List<Location> getLocations() {
		return locations.get();
	}
	
	/**
	 * Get locations containing text
	 * @param text which locations should contain
	 * @return list of locations containing text
	 */
	public List<Location> getLocations(String text) {
		ArrayList<Location> filteredLocations = new ArrayList<Location>();
		for (Location location : locations.get()) {
			if (location.toString().toLowerCase().trim().contains(text.toLowerCase().trim())) {
				filteredLocations.add(location);
			}
		}
		return filteredLocations;
	}
	
	/**
	 * Set if a category is selected.
	 * @param id of category
	 * @param selected is true if category should be selected, false otherwise
	 */
	public void setSelectedCategory(int id, boolean selected) {
		if (selected) {
			selectedCategories.add(id);
		} else {
			selectedCategories.remove(id);
		}
		reloadData();
	}
	
	/**
	 * Set if a location is selected.
	 * @param id of location
	 * @param selected is true if location should be selected, false otherwise
	 */
	public void setSelectedLocation(int id, boolean selected) {
		locations.setSelected(id, selected);
		reloadData();
	}
	
	/**
	 * Get list of all accounts.
	 * @return a list of all accounts
	 */
	public List<Account> getSelectedAccounts() {
		return accounts.getSelected();
	}
	
	/**
	 * Get list of selected categories.
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
	 * @return selected locations
	 */
	public List<Location> getSelectedLocations() {
		return locations.getSelected();
	}
	
	/**
	 * Set of an account is selected.
	 * @param id of account
	 * @param selected is true if account should be selected, false otherwise
	 */
	public void setSelectedAccount(int id, boolean selected) {
		accounts.setSelected(id, selected);
		reloadData();
	}
	
	/**
	 * Get data grouped by account.
	 * @return
	 */
	public List<Account> getDataByAccount() {
		return dataByAccount;
	}
	
	/**
	 * Get data grouped by location.
	 * @return
	 */
	public TweetsAndRetweets getDataByLocation() {
		return dataByLocation;
	}
	
	public String getErrorMessage() {
		String message = errorMessage;
		errorMessage = "";
		return message;
	}
	
	/**
	 * Select start and end or one day if start and end date are the same.
	 * Earlier date will automatically be taken as start date.
	 * If one date is null selected date range will be removed.
	 * @param startDate of the date range
	 * @param endDate of the date range
	 */
	public void setDateRange(Date startDate, Date endDate) {
		if (startDate == null || endDate == null) {
			selectedStartDate = null;
			selectedEndDate = null;
		} else {
			if(startDate.before(endDate)) {
				selectedStartDate = startDate;
				selectedEndDate = endDate;
			} else {
				selectedStartDate = endDate;
				selectedEndDate = startDate;
			}
		}
	}
	
	/**
	 * Adds user who's tweets the crawler will be listening.
	 * @param user the twitter user
	 * @param locationID of location from user
	 */
	public void addUserToWatch(User user, int locationID) {
		db.addAccount(user, locationID);
	}
	
	/**
	 * Add a category to an user.
	 * @param accountID of user
	 * @param categoryID of category
	 */
	public void setCategory(int accountID, int categoryID) {
		db.setCategory(accountID, categoryID);
	}
	
	/**
	 * Add a location to an user.
	 * @param accountID of user
	 * @param locationID of location
	 */
	public void setLocation(int accountID, int locationID) {
		db.setLocation(accountID, locationID, true);
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
	 * @param element which will later be notified on update.
	 */
	public void subscribe(GUIElement element) {
		guiElements.add(element);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		new Thread(rnbInitDBConnection).start();
	}
	
}
