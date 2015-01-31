package gui.databaseOptions;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import twitter4j.User;
import mysql.result.Account;
import mysql.result.Category;
import mysql.result.Location;
import javafx.application.Platform;
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
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
	
	//####################### AddCategory#########################
	@FXML 
	private TabPane tabPane_Cat;
	@FXML
	private Tab tab_Cat_tab1;
	@FXML 
	private Tab tab_Cat_tab2;
	// tab1
	@FXML
	private TextField txtField_Cat_tab1;
	@FXML 
	private ListView<Account> list_Cat_tab1;
	@FXML 
	private Button b_Cat_tab1;
	// tab2
	@FXML
	private TextField txtField_Cat_tab2;
	@FXML
	private ListView<Category> list_Cat_tab2;
	@FXML
	private TreeView<Category> trv_Cat_tab2;
	@FXML 
	private Button b_Cat_tab2_fertig;
	@FXML 
	private Button b_Cat_tab2_zurueck;
	@FXML
	private Button b_Cat_tab2_entfernen;
	@FXML
	private TreeView trv_Cat_tab2_oldCats;
	@FXML
	private Label l_Cat_tab2_selectedAccount;
	@FXML 
	private Label l_Cat_tab2_success;
	//####################### Add/Change Location#########################
	@FXML
	private TabPane tabPane_Loc;
	@FXML
	private Tab tab_Loc_tab1;
	@FXML
	private Tab tab_Loc_tab2;
	// tab1
	@FXML
	private TextField txtField_Loc_tab1;
	@FXML 
	private ListView<Account> list_Loc_tab1;
	@FXML 
	private Button b_Loc_tab1;
	// tab2
	@FXML
	private TextField txtField_Loc_tab2;
	@FXML
	private TreeView<Location> trv_Loc_tab2;
	@FXML 
	private Button b_Loc_tab2_fertig;
	@FXML 
	private Button b_Loc_tab2_zurueck;
	@FXML
	private Button b_Loc_tab2_entfernen;
	
	//################################# Add Account ########################
	@FXML
	private TabPane tabPane_Acc;
	@FXML
	private Tab tab_Acc_tab1;
	@FXML
	private TextField txtField_Acc_tab1;
	@FXML
	private ListView<UserContainer> list_Acc_tab1;
	@FXML 
	private Button b_Acc_tab1_schliessen;
	@FXML
	private Button b_Acc_tab1_hinzufuegen;
	@FXML
	private Button b_Acc_tab1_suchen;
	
	
	private Account account;
	private Stage dialogStage;
	private final int DEFAULT_LOCATION = 1;
	// Delay time for success messages
	private final int DELAY = 5000;
	
	

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
		
		//popUp add new category
		if(tabPane_Cat != null) {
			tab_Cat_tab2.setDisable(true);
			txtField_Cat_tab1.setOnKeyReleased(new MyCatEventHandler());
			list_Cat_tab1.setOnMouseClicked(new MyCatEventHandler());
			b_Cat_tab1.setOnMouseClicked(new MyCatEventHandler());
			txtField_Cat_tab2.setOnKeyReleased(new MyCatEventHandler());
			list_Cat_tab2.setOnMouseClicked(new MyCatEventHandler());
			trv_Cat_tab2.setOnMouseClicked(new MyCatEventHandler());
			b_Cat_tab2_fertig.setOnMouseClicked(new MyCatEventHandler());
			b_Cat_tab2_zurueck.setOnMouseClicked(new MyCatEventHandler());	
			b_Cat_tab2_entfernen.setOnMouseClicked(new MyCatEventHandler());
			
			// list contain default elements
			List<Account> defaultList = superController.getAccounts("");
			list_Cat_tab1.getItems().clear();
			for(Account a : defaultList) {
				list_Cat_tab1.getItems().add(a);
			}
		}
		
		//popUp add/change location
		if(tabPane_Loc != null) {
			tab_Loc_tab2.setDisable(true);
			txtField_Loc_tab1.setOnKeyReleased(new MyLocEventHandler());
			list_Loc_tab1.setOnMouseClicked(new MyLocEventHandler());
			b_Loc_tab1.setOnMouseClicked(new MyLocEventHandler());
			txtField_Loc_tab2.setOnKeyReleased(new MyLocEventHandler());
			trv_Loc_tab2.setOnMouseClicked(new MyLocEventHandler());
			trv_Loc_tab2.setOnMouseClicked(new MyLocEventHandler());
			b_Loc_tab2_fertig.setOnMouseClicked(new MyLocEventHandler());
			b_Loc_tab2_zurueck.setOnMouseClicked(new MyLocEventHandler());	
		}
		// popUp add user
		if (tabPane_Acc != null) {
			b_Acc_tab1_suchen.setOnMousePressed(new MyAccEventHandler());
			b_Acc_tab1_hinzufuegen.setOnMouseClicked(new MyAccEventHandler());
			b_Acc_tab1_schliessen.setOnMouseClicked(new MyAccEventHandler());
		}
		

		
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
	    
	    TabPane page;
	    
		try {
			System.out.println(DatabaseOptController.this);
			page = (TabPane) loader.load();
	        dialogStage = new Stage();
	        System.out.println(loader.getController());
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
				createPopUp("AddCategory.fxml", "Kategorie hinzufügen",null);
			    
			}
			if (event.getSource().equals(addLoc)) {
				//popUp for input-dialog change/add location
				createPopUp("AddLocation.fxml","Location ändern", null);
			}
			if (event.getSource().equals(addAcount)) {
				//popUp for input dialog add account
				createPopUp("AddAccount.fxml","Account hinzufügen",null);
			}
		}	
	}

	/**
	 * contains handle-method for all actions contained in the add/change location category
	 * @author Matthias
	 *
	 */
	private class MyLocEventHandler implements EventHandler<Event> {

		@Override
		public void handle(Event event) {
//########################################## tab1 ##############################
			
			if(event.getSource().equals(txtField_Loc_tab1)) {
				// update listView of account
				
				if ( (event instanceof KeyEvent)) {
					
					KeyEvent k = (KeyEvent) event;
					
					if(!k.getText().isEmpty() || k.getCode().equals(KeyCode.DELETE) || k.getCode().equals(KeyCode.BACK_SPACE)) {
						 
						String input = txtField_Loc_tab1.getText();
						
						    // update list
							List<Account>  list = list_Loc_tab1.getItems();
							
							list_Loc_tab1.getItems().clear();
							list = list_Loc_tab1.getItems();
							
							for(Account a : superController.getAccounts(input)) {
								list_Loc_tab1.getItems().add(a);
							} 
						
					} 
				}
			}
			
			if(event.getSource().equals(b_Loc_tab1)) {
				// save selected account close tab
			
				Account selectedAccount = list_Loc_tab1.getSelectionModel().getSelectedItem(); 
				if (selectedAccount == null) {
					return;
				} 
				account = selectedAccount;
				System.out.println(selectedAccount.getName());
				// change tab
				tab_Loc_tab1.setDisable(true);
				tab_Loc_tab2.setDisable(false);
				tabPane_Loc.getSelectionModel().select(tab_Loc_tab2);
			}
			
			// ################################# tab2 ####################################
			
		
			if(event.getSource().equals(txtField_Loc_tab2)) {
				// update list of countries
					
				if ( (event instanceof KeyEvent)) {
						
					KeyEvent k = (KeyEvent) event;
					
					if(!k.getText().isEmpty() || k.getCode().equals(KeyCode.DELETE) || k.getCode().equals(KeyCode.BACK_SPACE)) {
							 
						String input = txtField_Loc_tab2.getText();
						System.out.println("zweites suchfeld: "+input);
						updateLocation(superController.getLocations(input));
					}
				}
			}				
					
			if(event.getSource().equals(b_Loc_tab2_fertig)) {
				// add selected Location to database
				
				TreeItem<Location> selectedItem = trv_Loc_tab2.getSelectionModel().getSelectedItem();
				if (selectedItem == null) {
					// no item selected;
					return;
				}
				if (account == null) {
					System.out.println("an error occured!");
					return;
				}
				
				superController.setLocation(account.getId(), selectedItem.getValue().getId());
			
			}
			
			if(event.getSource().equals(b_Loc_tab2_zurueck)) {
				//change tab
				tab_Loc_tab2.setDisable(true);
				tab_Loc_tab1.setDisable(false);
				tabPane_Loc.getSelectionModel().select(tab_Loc_tab1);		
			}
		
		}
			
		
		private void updateLocation(List<Location> list) {
			TreeItem<Location> rootItem = new TreeItem<Location>(new Location(0, "Welt", "0000", null));
			rootItem.setExpanded(false);		
			for (Location location : list) {
				rootItem.getChildren().add(new TreeItem<Location>(location));
			}
			trv_Loc_tab2.setRoot(rootItem);
		}
	}	
	
	/**
	 * contains handle-method for all actions contained in the add category window
	 * @author Matthias
	 *
	 */
	private class MyCatEventHandler implements EventHandler<Event> {

		@Override
		public void handle(Event event) {
			
			//########################################## tab1 ##############################
			
			if(event.getSource().equals(txtField_Cat_tab1)) {
				// update listView of account
				
				if ( (event instanceof KeyEvent)) {
					
					KeyEvent k = (KeyEvent) event;
					
					if(!k.getText().isEmpty() || k.getCode().equals(KeyCode.DELETE) || k.getCode().equals(KeyCode.BACK_SPACE)) {
						 
						String input = txtField_Cat_tab1.getText();
							System.out.println(input);
						 // update list
							System.out.println(list_Cat_tab1.getItems().size());
							list_Cat_tab1.getItems().clear();
							System.out.println(list_Cat_tab1.getItems().size());
							for(Account a : superController.getAccounts(input)) {
								list_Cat_tab1.getItems().add(a);
							}
						
					}
				}
			}
			
			if(event.getSource().equals(b_Cat_tab1)) {
				// save selected account close tab
			
				Account selectedAccount = list_Cat_tab1.getSelectionModel().getSelectedItem(); 
				if (selectedAccount == null) {
					return;
				} 
				account = selectedAccount;
				
				// change tab
				tab_Cat_tab1.setDisable(true);
				tab_Cat_tab2.setDisable(false);
				tabPane_Cat.getSelectionModel().select(tab_Cat_tab2);
				
				//get and view all categories already belonging to the selected account
				
				List<Integer> categoryList = account.getCategoryIds();
				int[] categoryArr = new int[categoryList.size()];
				
				for (int i = 0; i < categoryArr.length; i++) {
					categoryArr[i] = categoryList.get(i);
				}
				System.out.println("ausgewählter account: " + account.getName() + " kategorien: "+ account.getCategoryIds().size());
				Category rootCat = superController.getCategoryRoot(categoryArr);
				// set new root item in treeView
				if (rootCat != null) {
					TreeItem<Category> rootItem = updateCategory(rootCat);
					trv_Cat_tab2_oldCats.setRoot(rootItem);
				}
				else {
					trv_Cat_tab2_oldCats.setRoot(null);
				}
				// set label to show the selected account
				l_Cat_tab2_selectedAccount.setText(account.getName());
				
				
			
			}
			
			// ################################# tab2 ####################################
			
			
			if(event.getSource().equals(txtField_Cat_tab2)) {
				// update TreeView of categories
					
				if ( (event instanceof KeyEvent)) {
						
					KeyEvent k = (KeyEvent) event;
					
					if(!k.getText().isEmpty() || k.getCode().equals(KeyCode.DELETE) || k.getCode().equals(KeyCode.BACK_SPACE)) {
							 
						String input = txtField_Cat_tab2.getText();
						System.out.println("zweites suchfeld: "+input);
					    trv_Cat_tab2.setRoot(updateCategory(superController.getCategoryRoot(input)));
					}
				}
			}				
			
			if(event.getSource().equals(trv_Cat_tab2)) {
				// add a category to list of selected categories
				
				Category cat = trv_Cat_tab2.getSelectionModel().getSelectedItem().getValue();
				
				if(!list_Cat_tab2.getItems().contains(cat)) {
					//  add a category only once
					list_Cat_tab2.getItems().add(cat);
				}	
			}
			
			if(event.getSource().equals(b_Cat_tab2_entfernen)) {
				// remove a category from list of selected categories
				
				int index =list_Cat_tab2.getSelectionModel().getSelectedIndex();
				
				if (index >= 0) {
					list_Cat_tab2.getItems().remove(index);
					list_Cat_tab2.setItems(list_Cat_tab2.getItems());
				}
			}
			
			if(event.getSource().equals(b_Cat_tab2_fertig)) {
				// add selected Categories to database
			
				
				// show  message for DELAY time
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						l_Cat_tab2_success.setText("Füge hinzu ... ");
					}
				});
				for(Category cat : list_Cat_tab2.getItems()) {
					superController.setCategory(account.getId(), cat.getId());
				}	
				// show success message for DELAY time
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						l_Cat_tab2_success.setText("Kategorie hinzugefügt");
						try {
							Thread.sleep(DELAY);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						l_Cat_tab2_success.setText("");
					}
				});
				
			}
			
			if(event.getSource().equals(b_Cat_tab2_zurueck)) {
				//change tab
				tab_Cat_tab2.setDisable(true);
				tab_Cat_tab1.setDisable(false);
				tabPane_Cat.getSelectionModel().select(tab_Cat_tab1);		
			}
		
			
			
		}
		/**
		 * creates tree for treeView
		 * @param rootCategory
		 * @return root-item
		 */
		private TreeItem<Category> updateCategory(Category rootCategory) {
			
			TreeItem<Category> rootItem = new TreeItem<Category>(rootCategory);
			rootItem.setExpanded(false);
			updateCategoryRec(rootCategory, rootItem);
			return rootItem;
			
		}
		private void updateCategoryRec(Category category, TreeItem<Category> item) {
			for (Category childCategory : category.getChilds()) {
				TreeItem<Category> child = new TreeItem<Category>(childCategory);
				child.setExpanded(false);
				updateCategoryRec(childCategory, child);
				item.getChildren().add(child);
			}
		}
		
	}
	/**
	 * contains handle-method for all actions contained in the add account window
	 * @author Matthias
	 *
	 */
	private class MyAccEventHandler implements EventHandler<Event> {
		@Override
		public void handle(Event event) {
			if(event.getSource().equals(b_Acc_tab1_suchen)) {
				// search for account matching the query
				
				String input = txtField_Acc_tab1.getText();
				if (input == null || input.equals("")) {
					return;
				}
				// clear old list
				if (list_Acc_tab1.getItems() != null) {
					list_Acc_tab1.getItems().clear();
				}
				List<User> list = TwitterAccess.getUser(input);
				System.out.println("hello");
				// fill found users/accounts in listView
				Iterator<User> it = list.iterator();
				while(it.hasNext()) {
					UserContainer userW = new UserContainer(it.next());
					list_Acc_tab1.getItems().add(userW);
					
				}
				
			}
			if(event.getSource().equals(b_Acc_tab1_hinzufuegen)) {
				// add account/user to database
				UserContainer userC = list_Acc_tab1.getSelectionModel().getSelectedItem();
				System.out.println("hinzugefuegen:   " + userC.getUser().getURL());
				
				superController.addUserToWatch(userC.getUser(), DEFAULT_LOCATION);
				System.out.println(userC.getUser().getName() + "    " + userC.getUser().getScreenName());
				System.out.println("hinzugefuegt");
				
				
			}
			if(event.getSource().equals(b_Acc_tab1_schliessen)) {
				dialogStage.close();
			}
		}
			
		
		
	}

}



/**
 * Inner class that manages all Event activities
 * @author Matthias
 *
 */
/*
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
	/*	if(event.getSource().equals(bZurueckLocation)) {
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
	
} */

//popUp add account
 /*
if (tabPane_Acc != null) {
	b_Acc_tab1_suchen.setOnMouseClicked(new MyAccEventHandler());
	b_Acc_tab1_hinzufuegen.setOnMouseClicked(new MyAccEventHandler());
	b_Acc_tab1_schliessen.setOnMouseClicked(new MyAccEventHandler());
}
//popUp select account category
if(bWeiter != null) {
	txtAccountSearch.setOnKeyReleased(new MyEventHandler());
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
Tab tab = new Tab(); */
/*

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
@FXML private TabPane pane;

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


*/
