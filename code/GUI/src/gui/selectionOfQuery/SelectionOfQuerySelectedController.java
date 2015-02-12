package gui.selectionOfQuery;

import gui.Labels;
import gui.OutputElement;
import gui.RunnableParameter;
import gui.Util;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import mysql.result.Account;
import mysql.result.Category;
import mysql.result.Location;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

/**
 * This class is used to display the tuple of categories, locations and accounts
 * the user has selected for his query.
 * 
 * @author Philipp
 * @version 1.0
 */
public class SelectionOfQuerySelectedController extends OutputElement implements EventHandler<Event>, Initializable {

	/**
	 * This listView displays the names of the selected categories.
	 */
	@FXML
	private ListView<Category> lstSelectedCategories;
	
	/**
	 * This listView displays the names of the selected locations.
	 */
	@FXML
	private ListView<Location> lstSelectedLocations;
	
	/**
	 * This listView displays the names of the selected Accounts.
	 */
	@FXML
	private ListView<Account> lstSelectedAccounts;
	
	/**
	 * This label describes ListView lstSelectedAccounts.
	 */
    @FXML
    private Label lblAccounts;

    /**
     * This label describes ListView lstSelectedLocations.
     */
    @FXML
    private Label lblLocations;

    /**
     * This label describes ListView lstSelectedCategories.
     */
    @FXML
    private Label lblCategories;
	
	
	/**
	 * Sets the list to display in lstSelectedCategory.
	 * 
	 * @param selectedCategories the list to display
	 */
	private void updateSelectedCategory(List<Category> selectedCategories) {
		Platform.runLater(new RunnableParameter<List<Category>>(selectedCategories) {
			@Override
			public void run() {
				lstSelectedCategories.getItems().clear();
				for (Category c : parameter) {
					lstSelectedCategories.getItems().add(c);
				}
			}
		});
		
	}
	
	/**
	 * Sets the list to display in lstSelectedAccounts.
	 * 
	 * @param selectedAccounts the list to display
	 */
	private void updateSelectedAccount(List<Account> selectedAccounts) {
		Platform.runLater(new RunnableParameter<List<Account>>(selectedAccounts) {
			@Override
			public void run() {
				lstSelectedAccounts.getItems().clear();
				for (Account a : parameter) {
					lstSelectedAccounts.getItems().add(a);
				}
			}
		});
		
	}
	
	/**
	 * Sets the list to display in lstSelectedLocations
	 * 
	 * @param selectedLocations the list to display
	 */
	private void updateSelectedLocation(List<Location> selectedLocations) {
		Platform.runLater(new RunnableParameter<List<Location>>(selectedLocations) {
			@Override
			public void run() {
				lstSelectedLocations.getItems().clear();
				for (Location l : parameter) {
					lstSelectedLocations.getItems().add(l);
				}				
			}
		});
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.initialize(location, resources);
		superController.subscribe(this);
		
		lblAccounts.setText(Util.getUppercaseStartAndRestLowerCase(Labels.ACCOUNTS));
		lblLocations.setText(Util.getUppercaseStartAndRestLowerCase(Labels.LOCATIONS));
		lblCategories.setText(Util.getUppercaseStartAndRestLowerCase(Labels.CATEGORIES));
	}

	@Override
	public void update(UpdateType type) {
		switch (type) {
			case ACCOUNT_SELECTION:
				updateSelectedAccount(superController.getSelectedAccounts());
				break;
			case CATEGORY_SELECTION:
				updateSelectedCategory(superController.getSelectedCategories());
				break;
			case LOCATION_SELECTION:
				updateSelectedLocation(superController.getSelectedLocations());
				break;
			default : 
				// do nothing
		}					
	}

	@FXML
	@Override
	public void handle(Event event) {
		// TODO: in thread öffnen
		if (event instanceof MouseEvent) {
			MouseEvent mouse = (MouseEvent) event;
			if (event.getSource().equals(lstSelectedAccounts)) {				
				handleUnselectAccount(mouse);
			} else if (event.getSource().equals(lstSelectedCategories)) {
				handleUnselectCategory(mouse);
			} else if (event.getSource().equals(lstSelectedLocations)) {
				handleUnselectLocation(mouse);
			}			
		}
	}
	
	/**
	 * Unselects the clicked account.
	 * 
	 * This method sets the account to not selected in the guiController
	 * and removes it's name from the display.
	 * 
	 * @param mouse a mouse Event originated from lstSelectedAccounts
	 */
	private void handleUnselectAccount(MouseEvent mouse) {
		if (mouse.getClickCount() == 2) {
			Account a = lstSelectedAccounts.getSelectionModel().getSelectedItem();
			superController.setSelectedAccount(a.getId(), false);
			int index = lstSelectedAccounts.getSelectionModel().getSelectedIndex();
			lstSelectedAccounts.getItems().remove(index);
		}	
	}
	
	/**
	 * Unselects the clicked category.
	 * 
	 * This method sets the category to not selected in the guiController
	 * and removes it's name from the display.
	 * 
	 * @param mouse a mouse Event originated from lstSelectedAccounts
	 */
	private void handleUnselectCategory(MouseEvent mouse) {
		if (mouse.getClickCount() == 2) {
			Category c = lstSelectedCategories.getSelectionModel().getSelectedItem();
			superController.setSelectedCategory(c.getId(), false);
			int index = lstSelectedCategories.getSelectionModel().getSelectedIndex();
			lstSelectedCategories.getItems().remove(index);
		}	
	}
	
	/**
	 * Unselects the clicked location.
	 * 
	 * This method sets the location to not selected in the guiController
	 * and removes it's name from the display.
	 * 
	 * @param mouse a mouse Event originated from lstSelectedAccounts
	 */
	private void handleUnselectLocation(MouseEvent mouse) {
		if (mouse.getClickCount() == 2) {
			Location l = lstSelectedLocations.getSelectionModel().getSelectedItem();
			superController.setSelectedLocation(l.getId(), false);
			int index = lstSelectedLocations.getSelectionModel().getSelectedIndex();
			lstSelectedLocations.getItems().remove(index);
		}	
	}

}
