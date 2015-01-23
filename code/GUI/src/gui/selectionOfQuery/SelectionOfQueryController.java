package gui.selectionOfQuery;

import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.ResourceBundle;

import sun.rmi.transport.LiveRef;
import mysql.result.Account;
import mysql.result.Category;
import mysql.result.Location;
import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import gui.InputElement;

public class SelectionOfQueryController extends InputElement implements EventHandler<Event>, Initializable {
	
	@FXML
	private TextField txtFilterSearch;
	@FXML
	private TreeView<Category> trvCategory;
	@FXML
	private TreeView<Location> trvLocation;
	@FXML
	private ListView<Account> lstAccount;
	@FXML
	private TitledPane tipLocation;
	@FXML
	private TitledPane tipAccount;
	@FXML
	private TitledPane tipCategory;
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
	
	private void updateAccounts(List<Account> accounts) {
		lstAccount.getItems().clear();
		for (Account a : accounts) {
			lstAccount.getItems().add(a);
		}
	}
	
	private void updateCategory(Category rootCategory) {
		TreeItem<Category> rootItem = new TreeItem<Category>(rootCategory);
		rootItem.setExpanded(true);
		updateCategoryRec(rootCategory, rootItem);
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
		} else if (type == UpdateType.ACCOUNT) {
			updateAccounts(superController.getAccounts(txtFilterSearch.getText()));
		}
	}

	@FXML
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
		} else if(e.getSource().equals(lstAccount)) {
			System.out.println("Account: " + lstAccount.getSelectionModel().getSelectedItem() +
					" (id=" + lstAccount.getSelectionModel().getSelectedItem().getId() + ")");
			superController.setSelectedAccount(lstAccount.getSelectionModel().getSelectedItem().getId(), true);
		} else if (e.getSource().equals(txtFilterSearch)) {
			if (e instanceof KeyEvent) {
				KeyEvent k = (KeyEvent) e;
				if (!k.getText().isEmpty() || k.getCode().equals(KeyCode.DELETE) || k.getCode().equals(KeyCode.BACK_SPACE)) {
					if (tipCategory.isExpanded()) {
						updateCategory(superController.getCategoryRoot(txtFilterSearch.getText()));
					} else if (tipAccount.isExpanded()) {
						updateAccounts(superController.getAccounts(txtFilterSearch.getText()));
					} else if (tipLocation.isExpanded()) {
						updateLocation(superController.getLocations(txtFilterSearch.getText()));
					}
				}
			}
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if (trvCategory != null) { // only one time in SelectionOfQueryView
			super.initialize(location, resources);
			superController.subscribe(this);
		}
	}

}
