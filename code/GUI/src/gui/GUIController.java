package gui;
	
import gui.GUIElement.UpdateType;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;

import mysql.AccessData;
import mysql.DBgui;
import mysql.result.Account;
import mysql.result.Category;
import mysql.result.Location;
import mysql.result.TweetsAndRetweets;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class GUIController extends Application implements Initializable {
	@FXML
	private Pane paSelectionOfQuery;
	@FXML
	private TextField txtSearch;
	@FXML
	private Label lblInfo;
	
	private static GUIController instance = null;
	private ArrayList<GUIElement> guiElements = new ArrayList<GUIElement>();
	
	private DBgui db;
	private List<Category> categories = new ArrayList<Category>();
	private List<Location> locations = new ArrayList<Location>();
	private List<Account> accounts = new ArrayList<Account>();
	private TweetsAndRetweets dataByLocation = new TweetsAndRetweets();
	private List<Account> dataByAccount = new ArrayList<Account>();
	
	private HashSet<Integer> selectedCategories = new HashSet<Integer>();
	private HashSet<Integer> selectedLocations = new HashSet<Integer>();
	private HashSet<Integer> selectedAccounts = new HashSet<Integer>();
	private Date selectedStartDate, selectedEndDate;
	public static GUIController getInstance() {
		if (instance == null) {
			launch("");
		}
		return instance;
	}
	
	@Override
	public void start(Stage primaryStage) {
		if (instance == null) {
			instance = this;
			try {
				Parent parent = FXMLLoader.load(GUIController.class.getResource("GUIView.fxml"));
				Scene scene = new Scene(parent, 800, 600);
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				primaryStage.setTitle("PSE-Twitter");
				primaryStage.setScene(scene);
				primaryStage.show();
			} catch(Exception e) {
				e.printStackTrace();
			}
			reloadAll();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	private void initDBConnection() {
		boolean success = true;
		lblInfo.setText("Verbindung mit DB wird aufgebaut...");
		try {
			db = new DBgui(new AccessData("hostName", "port", "dbName", "userName", "password"), null);
			// TODO: connect to db
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			setInfo("Fehler, " + e.getLocalizedMessage());
			success = false;
		}
		if (success) {
			setInfo("Erfolreich mit DB verbunden.");
		}
	}
	private void reloadLocation() {
//		locations = db.getLocations();
		// TODO: remove test code:
		locations.clear();
		locations.add(new Location(21, "Deutschland", "1234", null));
		locations.add(new Location(82, "Frankreich", "1234", null));
		locations.add(new Location(43, "Finnland", "1234", null));
		locations.add(new Location(14, "Ungarn", "1234", null));
		
		update(UpdateType.LOCATION);
	}
	
	private void reloadAccounts() {
//		accounts = db.getAccounts();
//		TODO: remove test code:
		accounts.clear();
		accounts.add(new Account(1, 243, "Max Mustermann", true, null, 1, 0, null, null, null));
		accounts.add(new Account(2, 122, "Max Mustermann", true, null, 2, 0, null, null, null));
		accounts.add(new Account(5, 432, "Max Mustermann", true, null, 3, 0, null, null, null));
		update(UpdateType.ACCOUNT);
	}
	
	private void reloadCategories() {
//		categories = db.getCategories();
//		TODO: remove test code:
		categories.clear();
		categories.add(new Category(31, "Musiker", null));
		categories.add(new Category(22, "Schriftsteller", null));
		categories.add(new Category(13, "Politiker", null));
		update(UpdateType.CATEGORY);
	}
	
	private void reloadData() {
		final Integer[] selectedCategoriesArray = selectedLocations.toArray(new Integer[selectedCategories.size()]);
		final Integer[] selectedLocationsArray = selectedLocations.toArray(new Integer[selectedLocations.size()]);
		final Integer[] selectedAccountsArray = selectedAccounts.toArray(new Integer[selectedAccounts.size()]);
		final boolean dateSelected = selectedStartDate != null && selectedEndDate != null;
		boolean success = true;
		Thread t1 = new Thread(new Runnable() {
			@Override
			public void run() {
				dataByLocation = db.getSumOfData(selectedCategoriesArray, selectedLocationsArray, selectedAccountsArray, dateSelected);				
			}
		});
		Thread t2 = new Thread(new Runnable() {
			@Override
			public void run() {
				dataByAccount = db.getAllData(selectedCategoriesArray, selectedLocationsArray, selectedAccountsArray, dateSelected);				
			}
		});
		t1.start();
		t2.start();
		try {
			t1.join();
		} catch (InterruptedException e) {
			success = false;
			setInfo(e.getLocalizedMessage());
		}
		try {
			t2.join();
		} catch (InterruptedException e) {
			success = false;
			setInfo(e.getLocalizedMessage());
		}
		if (success) {
			update(UpdateType.TWEET);
		}
	}
	
	private void reloadAll() {
		reloadAccounts();
		reloadCategories();
		reloadLocation();
	}
	
	private void setInfo(String text) {
		if (lblInfo != null) {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					lblInfo.setText(text);
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							lblInfo.setText("");
						}
					});
				}
			});
		}
	}
	
	/**
	 * Get list of all categories
	 * @return list of categories
	 */
	public ArrayList<Category> getCategories() {
		return categories;
	}
	
	/**
	 * Get categories containing text
	 * @param text which categories should contain
	 * @return list of categories containing text
	 */
	public ArrayList<Category> getCategories(String text) {
		ArrayList<Category> filteredCategories = new ArrayList<Category>();
		for (Category category : categories) {
			if (category.toString().contains(text)) {
				filteredCategories.add(category);
			}
		}
		return filteredCategories;
	}
	
	/**
	 * Get list of all locations.
	 * @return list of locations
	 */
	public ArrayList<Location> getLocations() {
		return locations;
	}
	
	/**
	 * Get locations containing text
	 * @param text which locations should contain
	 * @return list of locations containing text
	 */
	public ArrayList<Location> getLocations(String text) {
		ArrayList<Location> filteredLocations = new ArrayList<Location>();
		for (Location location : locations) {
			if (location.toString().contains(text)) {
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
	public void setCategory(int id, boolean selected) {
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
	public void setLocation(int id, boolean selected) {
		if (selected) {
			selectedCategories.add(id);
		} else {
			selectedCategories.remove(id);
		}
		reloadData();
	}
	
	/**
	 * Get list of all accounts.
	 * @return a list of all accounts
	 */
	public ArrayList<String> getSelectedAccounts() {
		// TODO: add correct code
		ArrayList<String> a = new ArrayList<String>();
		a.add("ZDF");
		return a;
	}
	
	/**
	 * Get list of selected categories.
	 * @return selected categories
	 */
	public ArrayList<String> getSelectedCategories() {
		// TODO: add correct code
		ArrayList<String> a = new ArrayList<String>();
		a.add("Musiker");
		a.add("Schornsteinfeger");
		return a;
	}
	/**
	 * Get list of selected locations.
	 * @return selected locations
	 */
	public ArrayList<String> getSelectedLocations() {
		// TODO: add correct code
		ArrayList<String> a = new ArrayList<String>();
		a.add("Deutschland");
		a.add("Frankreich");
		a.add("�sterreich");
		return a;
	}
	
	/**
	 * Set of an account is selected.
	 * @param id of account
	 * @param selected is true if account should be selected, false otherwise
	 */
	public void setAccount(int id, boolean selected) {
		if (selected) {
			selectedAccounts.add(id);
		} else {
			selectedAccounts.remove(id);
		}
		reloadData();
	}
	
	/**
	 * Get data grouped by account.
	 * @return
	 */
	public ArrayList<Account> getDataByAccount() {
		return dataByAccount;
	}
	
	/**
	 * Get data grouped by location.
	 * @return
	 */
	public TweetsAndRetweets getDataByLocation() {
		return dataByLocation;
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
	
	private void update(UpdateType type) {
		for (GUIElement element : guiElements) {
			element.update(type);
		}
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
		initDBConnection();
	}
	
	// TODO: many functions are missing
}
