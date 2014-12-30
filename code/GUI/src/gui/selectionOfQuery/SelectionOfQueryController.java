package gui.selectionOfQuery;

import java.util.ArrayList;

import mysql.result.Category;
import mysql.result.Location;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import gui.InputElement;
import gui.base.GUIController;

public class SelectionOfQueryController implements EventHandler<Event>, InputElement {
	
	@FXML
	private TextField txtFilterSearch;
	@FXML
	private TreeView<Category> trvCategory;
	@FXML
	private TreeView<Location> trvLocation;
	@FXML
	private TitledPane tipLocation, tipAccount, tipCategory;
	
	private GUIController parent;
	
	
	/**
	 * Set super controller.
	 * @param parent is a super controller, which coordinates between GUI elements.
	 */
	public void setParent(GUIController parent) {
		System.out.println("setParent");
		this.parent = parent;
		
		trvCategory.setOnMouseClicked(this);
		trvLocation.setOnMouseClicked(this);
		txtFilterSearch.setOnKeyReleased(this);
		
		updateCategory(parent.getCategories());
		updateLocation(parent.getLocations());
		
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
		if (type == UpdateType.TWEET) {
			// TODO: load data and update elements
		} else if (type == UpdateType.LOCATION) {
			// TODO: load data and update elements
		} else if (type == UpdateType.CATEGORY) {
			// TODO: load data and update elements
		} 
	}
	
	@Override
	public void handle(Event e) {
		if (e.getSource().equals(trvCategory)) {
			System.out.println("Kategorie: " + trvCategory.getSelectionModel().getSelectedItem().getValue() +
					" (id=" + trvCategory.getSelectionModel().getSelectedItem().getValue().getId() + ")");
			// TODO: update selection list & map
		} else if (e.getSource().equals(trvLocation)) {
			System.out.println("Ort: " + trvLocation.getSelectionModel().getSelectedItem().getValue() +
					" (id=" + trvLocation.getSelectionModel().getSelectedItem().getValue().getId() + ")");
			// TODO: update selection list & map
		} else if (e.getSource().equals(txtFilterSearch)) {
			System.out.println("Eingabe: " + txtFilterSearch.getText());
			if (tipCategory.isExpanded()) {
				updateCategory(parent.getCategories(txtFilterSearch.getText()));
			} else if (tipAccount.isExpanded()) {
				// TODO: reload list
			} else if (tipLocation.isExpanded()) {
				updateLocation(parent.getLocations(txtFilterSearch.getText()));
			}
		} else {
			System.out.println("Something else.");
			// TODO: update selection list & map
		}
	}

}
