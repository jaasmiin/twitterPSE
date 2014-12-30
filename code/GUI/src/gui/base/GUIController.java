package gui.base;
	
import gui.selectionOfQuery.SelectionOfQueryController;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import mysql.result.Category;
import mysql.result.Location;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class GUIController extends Application implements Initializable {
	@FXML
	private Pane paSelectionOfQuery;
	@FXML
	private TextField txtSearch;
	@FXML
	private SelectionOfQueryController selectionOfQueryController;
	
	private ArrayList<Category> categories = new ArrayList<Category>();
	private ArrayList<Location> locations = new ArrayList<Location>();
	
	@Override
	public void start(Stage primaryStage) {
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
	}
	
	public static void main(String[] args) {
		launch(args);
		
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO: code below added for testing
		categories.add(new Category(31, "Musiker", null));
		categories.add(new Category(22, "Schriftsteller", null));
		categories.add(new Category(13, "Politiker", null));
		locations.add(new Location(21, "Deutschland", "1234", null));
		locations.add(new Location(82, "Frankreich", "1234", null));
		locations.add(new Location(43, "Finnland", "1234", null));
		locations.add(new Location(14, "Ungarn", "1234", null));
		// TODO: code before add for testing
		selectionOfQueryController.setParent(this);
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
			if (category.getCategory().contains(text)) {
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
			if (location.getName().contains(text)) {
				filteredLocations.add(location);
			}
		}
		return filteredLocations;
	}
	
	
	// TODO: many functions are missing
}
