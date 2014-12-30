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
	
	// TODO: many functions are missing
}
