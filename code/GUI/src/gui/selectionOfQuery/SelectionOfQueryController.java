package gui.selectionOfQuery;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import mysql.result.Account;
import mysql.result.Category;
import mysql.result.Location;
import mysql.result.Result;
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
	
	private void updateSelectedCategory(List<Category> list) {
		// TODO: add code
	}
	
	private void updateSelectedAccount(List<Account> selectedAccounts) {
		// TODO: add code
	}
	
	private void updateSelectedLocation(List<Location> selectedLocations) {
		// TODO: add code
	}
	
	private void updateCategory(Category rootCategory) {
		TreeItem<Category> rootItem = new TreeItem<Category>(rootCategory);
		rootItem.setExpanded(true);
		updateCategoryRec(rootCategory, rootItem);
		// TODO: implement
		trvCategory.setRoot(rootItem);
	}
	
	private void updateCategoryRec(Category category, TreeItem<Category> item) {
		for (Category childCategory : category.getChilds()) {
			TreeItem<Category> child = new TreeItem<Category>(childCategory);
			child.setExpanded(true);
			updateCategoryRec(childCategory, child);
			item.getChildren().add(child);
		}
	}
	
	private void updateLocation(List<Location> list) {
		TreeItem<Location> rootItem = new TreeItem<Location>(new Location(0, "Welt", "0000", null));
		rootItem.setExpanded(true);		
		for (Location location : list) {
			rootItem.getChildren().add(new TreeItem<Location>(location));
		}
		trvLocation.setRoot(rootItem);
	}
	
	@Override
	public void update(UpdateType type) {
		System.out.println("SelectionOfQuerryController.update()");
		if (type == UpdateType.TWEET) {
			// TODO: load data and update elements
			updateSelectedAccount(superController.getSelectedAccounts());
			updateSelectedCategory(superController.getSelectedCategories());
			updateSelectedLocation(superController.getSelectedLocations());
		} else if (type == UpdateType.LOCATION) {
			updateLocation(superController.getLocations(txtFilterSearch.getText()));
		} else if (type == UpdateType.CATEGORY) {
			updateCategory(superController.getCategoryRoot(txtFilterSearch.getText()));
		} 
	}


	@Override
	public void handle(Event e) {
		if (e.getSource().equals(trvCategory)) {
			System.out.println("Kategorie: " + trvCategory.getSelectionModel().getSelectedItem().getValue() +
					" (id=" + trvCategory.getSelectionModel().getSelectedItem().getValue().getId() + ")");
			superController.setSelectedCategory(trvCategory.getSelectionModel().getSelectedItem().getValue().getId(), true);
		} else if (e.getSource().equals(trvLocation)) {
			System.out.println("Ort: " + trvLocation.getSelectionModel().getSelectedItem().getValue() +
					" (id=" + trvLocation.getSelectionModel().getSelectedItem().getValue().getId() + ")");
			superController.setSelectedLocation(trvLocation.getSelectionModel().getSelectedItem().getValue().getId(), true);
		} else if (e.getSource().equals(txtFilterSearch)) {
			System.out.println("Eingabe: " + txtFilterSearch.getText());
			if (tipCategory.isExpanded()) {
				updateCategory(superController.getCategoryRoot(txtFilterSearch.getText()));
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
