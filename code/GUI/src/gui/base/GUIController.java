package gui.base;
	
import gui.GUIElement;
import gui.GUIElement.UpdateType;
import java.util.ArrayList;
import mysql.DBgui;
import mysql.result.Account;
import mysql.result.Category;
import mysql.result.Location;
import mysql.result.TweetsAndRetweets;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class GUIController extends Application {
	@FXML
	private Pane paSelectionOfQuery;
	@FXML
	private TextField txtSearch;
	
	private static GUIController instance = null;
	private ArrayList<GUIElement> guiElements = new ArrayList<GUIElement>();
	
	private DBgui db;
	private ArrayList<Category> categories = new ArrayList<Category>();
	private ArrayList<Location> locations = new ArrayList<Location>();
	private ArrayList<Account> accounts = new ArrayList<Account>();
	private ArrayList<TweetsAndRetweets> summedData = new ArrayList<TweetsAndRetweets>();
	
	private ArrayList<Integer> selectedCategories = new ArrayList<Integer>();
	private ArrayList<Integer> selectedLocations = new ArrayList<Integer>();
	private ArrayList<Integer> selectedAccounts = new ArrayList<Integer>();
	
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
			initDBConnection();
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
//		db = new DBgui(null, null);
		// TODO: connect to db
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
	
	private void reloadSummedData() {
//		summedData = db.getSumOfData(selectedCategories, selectedLocations);
		// TODO: which data type should be used?
		update(UpdateType.TWEET);
	}
	
	private void reloadAll() {
		reloadAccounts();
		reloadCategories();
		reloadLocation();
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
	 * Get lost of all locations
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
	
	public void setCategory(int id, boolean selected) {
		if (selected) {
			selectedCategories.add(id);
		} else {
			selectedCategories.remove(id);
		}
		reloadSummedData();
	}
	
	public void setLocation(int id, boolean selected) {
		if (selected) {
			selectedCategories.add(id);
		} else {
			selectedCategories.remove(id);
		}
		reloadSummedData();
	}
	
	public void setAccount(int id, boolean selected) {
		if (selected) {
			selectedAccounts.add(id);
		} else {
			selectedAccounts.remove(id);
		}
		reloadSummedData();
	}
	
	private void update(UpdateType type) {
		for (GUIElement element : guiElements) {
			element.update(type);
		}
	}
	
	public void subscribe(GUIElement element) {
		guiElements.add(element);
	}
	
	// TODO: many functions are missing
}
