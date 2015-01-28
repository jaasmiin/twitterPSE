package gui.databaseOptions;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import twitter4j.User;
import mysql.result.Account;
import mysql.result.Category;
import mysql.result.Location;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import gui.InputElement;

public class DatabaseOptController extends InputElement implements Initializable {
	@FXML
	private MenuItem addCat;
	@FXML
	private MenuItem addLoc;
	@FXML
	private MenuItem addAcount;
	@FXML 
	private Button bFertig;
	@FXML
	private Button bEntf;
	@FXML
	private Button bZurueck;
	@FXML
	private TreeView<Category> trvCat;
	@FXML
	private TextField txtCatSearch;
	@FXML
	private ListView<Category> listCat;
	@FXML
	private TextField txtAccountSearch;
	@FXML
	private Button bWeiter;
	@FXML
	private ListView<Account> listAccount;
	@FXML 
	private Label lbInfo;
	@FXML
	private TreeView<Location> trvLocation;
	@FXML
	private TextField txtLocSearch;
	@FXML
	private Button bWeiterLocation;
	@FXML 
	private Button bFertigLocation;
	@FXML
	private Button bZurueckLocation;
	
	//AccountADD
	@FXML
	private TextField txtAccountAdd;
	@FXML
	private Button bSearchAccount;
	@FXML
	private ListView<Account> listAccountAdd;
	@FXML
	private Button bAccountAdd;
	@FXML 
	private Button bAddAccountClose;
	
	private Account account;
	private Stage dialogStage;
	
	@Override
	public void update(UpdateType type) {
		// TODO Auto-generated method stub
	}

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.initialize(location, resources);
		if(addCat != null) {
			addCat.setOnAction(new MyActionEventHandler());
			addLoc.setOnAction(new MyActionEventHandler());
			addAcount.setOnAction(new MyActionEventHandler());
		}
		//popUp select account category
		if(bWeiter != null) {
			txtAccountSearch.setOnKeyPressed(new MyEventHandler());
			listAccount.setOnMouseClicked(new MyEventHandler());
			bWeiter.setOnMouseClicked(new MyEventHandler());
		}
		//popUp add Category
		if(txtCatSearch != null) {
			System.out.println("eingabe");
			txtCatSearch.setOnKeyPressed(new MyEventHandler());
			trvCat.setOnMouseClicked(new MyEventHandler());
			bEntf.setOnMouseClicked(new MyEventHandler());
			bFertig.setOnMouseClicked(new MyEventHandler());
			bZurueck.setOnMouseClicked(new MyEventHandler());
		}
		//popUp select account change location
		if(bWeiterLocation != null) {
			bWeiterLocation.setOnMouseClicked(new MyEventHandler());
			txtAccountSearch.setOnKeyPressed(new MyEventHandler());
			listAccount.setOnMouseClicked(new MyEventHandler());
		}
		//popUp change location
		if(txtLocSearch != null) {
			txtLocSearch.setOnKeyPressed(new MyEventHandler());
			trvLocation.setOnMouseClicked(new MyEventHandler());
			bFertigLocation.setOnMouseClicked(new MyEventHandler());
			bZurueckLocation.setOnMouseClicked(new MyEventHandler());
		}
		//popUp add account
		if(txtAccountAdd != null) {
			bSearchAccount.setOnMouseClicked(new MyEventHandler());
			bAccountAdd.setOnMouseClicked(new MyEventHandler());
			bAddAccountClose.setOnMouseClicked(new MyEventHandler());
		}
		
	}
	/**
	 * creates tree for treeView
	 * @param rootCategory
	 */
	private void updateCategory(Category rootCategory) {
		TreeItem<Category> rootItem = new TreeItem<Category>(rootCategory);
		rootItem.setExpanded(true);
		updateCategoryRec(rootCategory, rootItem);
		trvCat.setRoot(rootItem);
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
	/**
	 * creates a new popUp loading the fxml-file under the given path
	 * @param path path of the fxml file to load
	 * @param title shown title of the popUp
	 * @param selectedAccount Account that is selected, can also be null;
	 */
	private void createPopUp(String path, String title, Account selectedAccount) {
		// Load the fxml file and create a new stage for the popup
	    FXMLLoader loader = new FXMLLoader(DatabaseOptController.class.getResource(path));
	    
	    AnchorPane page;
	    
		try {
			System.out.println(DatabaseOptController.this);
			page = (AnchorPane) loader.load();
	        dialogStage = new Stage();
	        
	        // give the controller in the newly created thread a reference to the current stage;
	        ((DatabaseOptController)loader.getController()).setCurrentStage(dialogStage);
	        ((DatabaseOptController)loader.getController()).setAccount(selectedAccount);
	        dialogStage.setTitle(title);
		    dialogStage.initModality(Modality.WINDOW_MODAL);
		    //dialogStage.initOwner(primaryStage);
		    Scene scene = new Scene(page);
		    dialogStage.setScene(scene);
		    dialogStage.show();
		} catch (IOException e1) {
			
			System.out.println("Unable to construct popUp");
			e1.printStackTrace();
		}
	}
	/**
	 * Sets the current stage
	 * @param stage current stage
	 */
	public void setCurrentStage(Stage stage) {
		dialogStage = stage;
	}
	public void setAccount(Account account) {
		this.account = account;
	}
	/**
	 * Inner class that manages all ActionEvent activities
	 * @author Matthias
	 *
	 */
	private class MyActionEventHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			// TODO Auto-generated method stub
			// select right handling for event
			if (event.getSource().equals(addCat)) {
				// popUp for input-dialog add category
				createPopUp("AccountSelect.fxml", "Account auswählen",null);
			    
			}
			if (event.getSource().equals(addLoc)) {
				//popUp for input-dialog change/add location
				createPopUp("AccountSelectLocation.fxml","Account auswählen", null);
			}
			if (event.getSource().equals(addAcount)) {
				//popUp for input dialog add account
				createPopUp("AccountAdd.fxml","Account hinzufügen",null);
			}
		}	
	}
	/**
	 * Inner class that manages all Event activities
	 * @author Matthias
	 *
	 */
	private class MyEventHandler implements EventHandler<Event>{

		

		@Override
		public void handle(Event event) {
			System.out.println("kontroller:" + event.getSource());
		
	    //######################################################	
		//#PopUp Select Account both versions
		//########################################################
			if(event.getSource().equals(txtAccountSearch)) {
				// update ListView of accounts
				
				String input = txtAccountSearch.getText();
				listAccount.getItems().clear();
				for(Account a : superController.getAccounts(input)) {
					listAccount.getItems().add(a);
				}
			}
			if(event.getSource().equals(bWeiterLocation)) {
				// save selected account, close select-account-popUp
				
				Account selectedAccount = listAccount.getSelectionModel().getSelectedItem();
				if (selectedAccount == null) {
					lbInfo.setText("Kein Account ausgewählt");
					return;
				}
				System.out.println(selectedAccount.getName());
				dialogStage.close();
				// popUp for InputDialog
				// Load the fxml file and create a new stage for the popup
				createPopUp("LocationSelect.fxml","Location auswählen",selectedAccount);
				
			}
			if(event.getSource().equals(bWeiter)) {
				// save selected account, close select-account-popUp
				
				Account selectedAccount = listAccount.getSelectionModel().getSelectedItem();
				if (selectedAccount == null) {
					lbInfo.setText("Kein Account ausgewählt");
					return;
				}
				System.out.println(selectedAccount.getName());
				dialogStage.close();
				
				// popUp for InputDialog
				// Load the fxml file and create a new stage for the popup
				createPopUp("CatSelect.fxml","Kategorie auswählen", selectedAccount);
			 
			}
			
			
		//####################################################	
		//#PopUp select category
		//####################################################
			if(event.getSource().equals(txtCatSearch)) {
				// update TreeView of categories
				String input = txtCatSearch.getText();
				System.out.println(input);
				updateCategory(superController.getCategoryRoot(input));
			}
			if(event.getSource().equals(trvCat)) {
				// add a category to list of selected categories
				Category cat = trvCat.getSelectionModel().getSelectedItem().getValue();
				if(!listCat.getItems().contains(cat)) {
					//  add a category only once
					listCat.getItems().add(cat);
				}
				
			}
			if(event.getSource().equals(bEntf)) {
				// remove a category from list of selected categories
				int index =listCat.getSelectionModel().getSelectedIndex();
				System.out.println(index);
				if (index >= 0) {
					listCat.getItems().remove(index);
					listCat.setItems(listCat.getItems());
				}
			}
			if(event.getSource().equals(bFertig)) {
				// add selected Categories to database
				System.out.println("bFertig xxxx");
				for(Category cat : listCat.getItems()) {
					superController.setCategory(account.getId(), cat.getId());
				}	
				//System.out.println(DatabaseOptController.this);
				dialogStage.close();
			}
			if(event.getSource().equals(bZurueck)) {
				//close select-category-popUp and open select-account-popUp again
				dialogStage.close();
				createPopUp("AccountSelect.fxml","Account auswählen", null);
				
			}
			
			//####################################################	
			//#PopUp select location
			//####################################################
			if(event.getSource().equals(bZurueckLocation)) {
				//close select-location-popUp and open select-account-(location)-popUp again
				dialogStage.close();
				createPopUp("AccountSelectLocation.fxml","Account auswählen", null);
			}
			if(event.getSource().equals(txtLocSearch)) {
				// update TreeView of locations
				String input = txtLocSearch.getText();
				System.out.println(input);
				updateLocation(superController.getLocations(input));
			}
			if(event.getSource().equals(bFertigLocation)) {
				// add selected location to database
				
				TreeItem<Location> selectedItem = trvLocation.getSelectionModel().getSelectedItem();
				if (selectedItem == null) {
					// no item selected;
					return;
				}
				if (account == null) {
					System.out.println("an error occured!");
					return;
				}
				
				superController.setLocation(account.getId(), selectedItem.getValue().getId());
				dialogStage.close();
				
				
			}
			
			//####################################################	
			//#PopUp add account
			//####################################################
			
			if(event.getSource().equals(bSearchAccount)) {
				// search for account matching the query
				
				String input = txtAccountAdd.getText();
				if (input == null || input.equals("")) {
					return;
				}
				// clear old list
				listAccountAdd.getItems().clear();
				List<User> list = TwitterAccess.getUser(input);
					
				// fill found users/accounts in listView
				Iterator<User> it = list.iterator();
				while(it.hasNext()) {
					Account account = new Account(0, it.next().getName(), "");
					listAccountAdd.getItems().add(account);
				}
				
			}
			if(event.getSource().equals(bAccountAdd)) {
				// add account/user to database
				listAccountAdd.getSelectionModel().getSelectedItem();
				//Account account = new Account()
				// TODO add superController.addAccount();
				
			}
			if(event.getSource().equals(bAddAccountClose)) {
				dialogStage.close();
			}
		}
		
	}
}
