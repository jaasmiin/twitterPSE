package gui.selectionOfQuery;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.sun.org.apache.bcel.internal.generic.LLOAD;

import mysql.result.Category;
import mysql.result.Location;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import gui.InputElement;

public class SelectionOfQueryController extends InputElement implements EventHandler<Event>, Initializable {
	
	@FXML
	private TextField txtFilterSearch;
	@FXML
	private TreeView<Category> trvCategory;
	@FXML
	private TreeView<Location> trvLocation;
	@FXML
	private TitledPane tipLocation, tipAccount, tipCategory;	
	@FXML
	private ListView<String> lstSelectedCategories;
	@FXML
	private ListView<String> lstSelectedLocations;
	@FXML
	private ListView<String> lstSelectedAccounts;
	
	private void updateSelected(ListView<String> lstView, ArrayList<String> selected) {
		// TODO: add code
	}
	
	private void updateCategory(ArrayList<Category> categories) {
		TreeItem<Category> rootItem = new TreeItem<Category>(new Category(1, "Alles", null));
		rootItem.setExpanded(true);		
		for (Category category : categories) {
			rootItem.getChildren().add(new TreeItem<Category>(category));
			// TODO: add categories in hierarchy
		}
		trvCategory.setRoot(rootItem);
	}
	
	private void updateLocation(ArrayList<Location> locations) {
		TreeItem<Location> rootItem = new TreeItem<Location>(new Location(0, "Welt", "0000", null));
		rootItem.setExpanded(true);		
		for (Location location : locations) {
			rootItem.getChildren().add(new TreeItem<Location>(location));
			// TODO: add locations in hierarchy
		}
		trvLocation.setRoot(rootItem);
	}
	
	@Override
	public void update(UpdateType type) {
		System.out.println("SelectionOfQuerryController.update()");
		if (type == UpdateType.TWEET) {
			// TODO: load data and update elements
			updateSelected(lstSelectedAccounts, superController.getSelectedAccounts());
			updateSelected(lstSelectedCategories, superController.getSelectedCategories());
			updateSelected(lstSelectedLocations, superController.getSelectedLocations());
		} else if (type == UpdateType.LOCATION) {
			updateLocation(superController.getLocations(txtFilterSearch.getText()));
		} else if (type == UpdateType.CATEGORY) {
			updateCategory(superController.getCategories(txtFilterSearch.getText()));
		} 
	}
	
	@Override
	public void handle(Event e) {
		if (e.getSource().equals(trvCategory)) {
			System.out.println("Kategorie: " + trvCategory.getSelectionModel().getSelectedItem().getValue() +
					" (id=" + trvCategory.getSelectionModel().getSelectedItem().getValue().getId() + ")");
			superController.setCategory(trvCategory.getSelectionModel().getSelectedItem().getValue().getId(), true);
		} else if (e.getSource().equals(trvLocation)) {
			System.out.println("Ort: " + trvLocation.getSelectionModel().getSelectedItem().getValue() +
					" (id=" + trvLocation.getSelectionModel().getSelectedItem().getValue().getId() + ")");
			superController.setLocation(trvLocation.getSelectionModel().getSelectedItem().getValue().getId(), true);
		} else if (e.getSource().equals(txtFilterSearch)) {
			System.out.println("Eingabe: " + txtFilterSearch.getText());
			if (tipCategory.isExpanded()) {
				updateCategory(superController.getCategories(txtFilterSearch.getText()));
			} else if (tipAccount.isExpanded()) {
				// TODO: reload list
			} else if (tipLocation.isExpanded()) {
				updateLocation(superController.getLocations(txtFilterSearch.getText()));
			}
		} else if (e.getSource().equals(null)) {

		} else {
			System.out.println("Something else.");
			// TODO: update selection list & map
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if (trvCategory != null) { // SelectionOfQueryView
			super.initialize(location, resources);
			superController.subscribe(this);
		
			trvCategory.setOnMouseClicked(this);
			trvLocation.setOnMouseClicked(this);
			txtFilterSearch.setOnKeyReleased(this);
		} else { // SelectionOfQuerySelectedView
			lstSelectedAccounts.setOnMouseClicked(this);
			lstSelectedCategories.setOnMouseClicked(this);
			lstSelectedLocations.setOnMouseClicked(this);
			
			// TODO: remove following lines
			lstSelectedAccounts.getItems().add("KIT");
			lstSelectedCategories.getItems().add("Musiker");
			lstSelectedLocations.getItems().add("Deutschland");
			lstSelectedLocations.getItems().add("Frankreich");
		}
	}

}
