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
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import gui.InputElement;
import gui.Labels;

public class DatabaseOptController extends InputElement implements
        Initializable {
    
    private static final int DEFAULT_LOCATION = 1;
    // Delay time for success messages
    // private final int DELAY = 5000;
    
	@FXML
	private Menu DBOPT_menu;
    @FXML
    private MenuItem addCat;
    @FXML
    private MenuItem addLoc;
    @FXML
    private MenuItem addAcount;

    // ####################### AddCategory#########################
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
    @FXML
    private Label l_Cat_tab1_selectedAcc;
    @FXML
    private Label l_Cat_tab1_AccDB;
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
    private TreeView<Category> trv_Cat_tab2_oldCats;
    @FXML
    private Label l_Cat_tab2_selectedAccount;
    @FXML
    private Label l_Cat_tab2_success;
    @FXML
    private Label l_Cat_tab2_oldCats;
    @FXML
    private Label l_Cat_tab2_selectedCat;
    @FXML
    private Label l_Cat_tab2_searchCat;
    // ####################### Add/Change Location#########################
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
    private Label l_Loc_tab2_selectedAccount;
    @FXML
    private Label l_Loc_tab2_currentPlace;
    @FXML
    private Label l_Loc_tab2_Account;
    @FXML
    private Label l_Loc_tab2_currentPlaceDescription;
    @FXML
    private Label l_Loc_tab2_currentAccountDescription;
    // ################################# Add Account ########################
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
    @FXML
    private Label l_Acc_searchAccount;
    @FXML
    private Label l_Acc_twitterAccounts;

    private Account account;
    private Stage dialogStage;

    @Override
    public void update(UpdateType type) {
        // TODO Auto-generated method stub
    }

    /**
     * sets labels in menu dialog
     */
    private void setLabelsMenu() {
        addCat.setText(Labels.DBOPT_CATEGORY);
        addLoc.setText(Labels.DBOPT_LOCATION);
        addAcount.setText(Labels.DBOPT_ACCOUNT);
    }

    /**
     * sets labels in add category dialog
     */
    private void setLabelsCat() {
        tab_Cat_tab2.setText(Labels.DBOPT_CAT_SELECTCAT);
        tab_Cat_tab1.setText(Labels.DBOPT_SELECTACCOUNT);
        b_Cat_tab1.setText(Labels.DBOPT_CONTINUE);
        b_Cat_tab2_fertig.setText(Labels.DBOPT_ADD);
        b_Cat_tab2_zurueck.setText(Labels.DBOPT_BACK);
        b_Cat_tab2_entfernen.setText(Labels.DBOPT_REMOVE);
        l_Cat_tab2_selectedAccount.setText(Labels.DBOPT_SELECTEDACCOUNT);
        l_Cat_tab1_selectedAcc.setText(Labels.DBOPT_SELECTACCOUNT);
        l_Cat_tab1_AccDB.setText(Labels.DBOPT_ACCOUNTLIST);
        l_Cat_tab2_oldCats.setText(Labels.DBOPT_CAT_OLDCAT);
        l_Cat_tab2_selectedCat.setText(Labels.DOPT_CAT_SELECTEDCAT);
        l_Cat_tab2_searchCat.setText(Labels.DBOPT_CAT_SEARCHCAT);
    }

    /**
     * sets labels in add loc dialog
     */
    private void setLabelsLoc() {
        tab_Loc_tab1.setText(Labels.DBOPT_SELECTACCOUNT);
        tab_Loc_tab2.setText(Labels.DBOPT_LOC_SELECTLOCATION);
        b_Loc_tab1.setText(Labels.DBOPT_CONTINUE);
        b_Loc_tab2_fertig.setText(Labels.DBOPT_LOC_CHANGE);
        b_Loc_tab2_zurueck.setText(Labels.DBOPT_BACK);
        l_Loc_tab2_selectedAccount.setText(Labels.DBOPT_SELECTACCOUNT);
        l_Loc_tab2_currentPlaceDescription.setText(Labels.DPOPT_CURRENTPLACE);
        l_Loc_tab2_currentAccountDescription
                .setText(Labels.DBOPT_SELECTEDACCOUNT);
    }

    /**
     * sets labels in add account dialog
     */
    public void setLabelsAcc() {
        tab_Acc_tab1.setText(Labels.DBOPT_ACC_ADDACCOUNT);
        b_Acc_tab1_hinzufuegen.setText(Labels.DBOPT_ADD);
        b_Acc_tab1_suchen.setText(Labels.DBOPT_SEARCH);
        l_Acc_searchAccount.setText(Labels.DBOPT_SEARCHACCOUNT);
        l_Acc_twitterAccounts.setText(Labels.DBOPT_ACC_TWITTERACCOUNTS);
        b_Acc_tab1_schliessen.setText(Labels.DBOPT_CLOSE);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        
        if (DBOPT_menu != null) {
        	// set label
            DBOPT_menu.setText(Labels.DBOPT_MENU);
        }
        
        
        if (addCat != null) {
            setLabelsMenu();
            addCat.setOnAction(new MyActionEventHandler());
            addLoc.setOnAction(new MyActionEventHandler());
            addAcount.setOnAction(new MyActionEventHandler());

        }

        // popUp add new category
        if (tabPane_Cat != null) {
            setLabelsCat();
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
            for (Account a : defaultList) {
                list_Cat_tab1.getItems().add(a);
            }
        }

        // popUp add/change location
        if (tabPane_Loc != null) {
            setLabelsLoc();
            tab_Loc_tab2.setDisable(true);
            txtField_Loc_tab1.setOnKeyReleased(new MyLocEventHandler());
            list_Loc_tab1.setOnMouseClicked(new MyLocEventHandler());
            b_Loc_tab1.setOnMouseClicked(new MyLocEventHandler());
            txtField_Loc_tab2.setOnKeyReleased(new MyLocEventHandler());
            trv_Loc_tab2.setOnMouseClicked(new MyLocEventHandler());
            trv_Loc_tab2.setOnMouseClicked(new MyLocEventHandler());
            b_Loc_tab2_fertig.setOnMouseClicked(new MyLocEventHandler());
            b_Loc_tab2_zurueck.setOnMouseClicked(new MyLocEventHandler());

            // list contain default elements
            List<Account> defaultList = superController.getAccounts("");
            list_Loc_tab1.getItems().clear();
            for (Account a : defaultList) {
                list_Loc_tab1.getItems().add(a);
            }
        }
        // popUp add user
        if (tabPane_Acc != null) {
            setLabelsAcc();
            b_Acc_tab1_suchen.setOnMousePressed(new MyAccEventHandler());
            b_Acc_tab1_hinzufuegen.setOnMouseClicked(new MyAccEventHandler());
            b_Acc_tab1_schliessen.setOnMouseClicked(new MyAccEventHandler());
        }

    }

    /**
     * creates a new popUp loading the fxml-file under the given path
     * 
     * @param path
     *            path of the fxml file to load
     * @param title
     *            shown title of the popUp
     * @param selectedAccount
     *            Account that is selected, can also be null;
     */
    private void createPopUp(String path, String title, Account selectedAccount) {
        // Load the fxml file and create a new stage for the popup
        FXMLLoader loader = new FXMLLoader(
                DatabaseOptController.class.getResource(path));

        TabPane page;

        try {
           
            page = (TabPane) loader.load();
            dialogStage = new Stage();
            
            // give the controller in the newly created thread a reference to
            // the current stage;
            ((DatabaseOptController) loader.getController())
                    .setCurrentStage(dialogStage);
            ((DatabaseOptController) loader.getController())
                    .setAccount(selectedAccount);
            dialogStage.setTitle(title);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            // dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            dialogStage.show();
        } catch (IOException e1) {

            //System.out.println("Unable to construct popUp");
            e1.printStackTrace();
        }
    }

    /**
     * Sets the current stage
     * 
     * @param stage
     *            current stage
     */
    public void setCurrentStage(Stage stage) {
        dialogStage = stage;
    }
    /**
     * Setter for account attribute
     * @param account account to be set
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Inner class that manages all ActionEvent activities
     * 
     * @author Matthias
     * 
     */
    private class MyActionEventHandler implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            // select right handling for event
            if (event.getSource().equals(addCat)) {
                // popUp for input-dialog add category
                createPopUp("AddCategory.fxml", "Kategorie hinzufügen", null);

            }
            if (event.getSource().equals(addLoc)) {
                // popUp for input-dialog change/add location
                createPopUp("AddLocation.fxml", "Location ändern", null);
            }
            if (event.getSource().equals(addAcount)) {
                // popUp for input dialog add account
                createPopUp("AddAccount.fxml", "Account hinzufügen", null);
            }
        }
    }

    /**
     * contains handle-method for all actions contained in the add/change
     * location category
     * 
     * @author Matthias
     * 
     */
    private class MyLocEventHandler implements EventHandler<Event> {

        @Override
        public void handle(Event event) {
            // ################################### tab1 ######################
            

            if (event.getSource().equals(txtField_Loc_tab1)) {
                // update listView of account

                if ((event instanceof KeyEvent)) {

                    KeyEvent k = (KeyEvent) event;

                    if (!k.getText().isEmpty()
                            || k.getCode().equals(KeyCode.DELETE)
                            || k.getCode().equals(KeyCode.BACK_SPACE)) {

                        String input = txtField_Loc_tab1.getText();

                        // update list
                        List<Account> list = list_Loc_tab1.getItems();

                        list_Loc_tab1.getItems().clear();
                        list = list_Loc_tab1.getItems();

                        for (Account a : superController.getAccounts(input)) {
                            list_Loc_tab1.getItems().add(a);
                        }

                    }
                }
            }

            if (event.getSource().equals(b_Loc_tab1)) {
                // save selected account close tab

                Account selectedAccount = list_Loc_tab1.getSelectionModel()
                        .getSelectedItem();
                if (selectedAccount == null) {
                    return;
                }
                account = selectedAccount;
               
                // change tab
                tab_Loc_tab1.setDisable(true);
                tab_Loc_tab2.setDisable(false);
                tabPane_Loc.getSelectionModel().select(tab_Loc_tab2);
                l_Loc_tab2_currentPlace.setText(account.getLocationCode());
                l_Loc_tab2_selectedAccount.setText(account.getName());
                updateLocation(superController.getLocations(""));
            }

            // ####################### tab2 ####################################
            

            if (event.getSource().equals(txtField_Loc_tab2)) {
                // update list of countries

                if ((event instanceof KeyEvent)) {

                    KeyEvent k = (KeyEvent) event;

                    if (!k.getText().isEmpty()
                            || k.getCode().equals(KeyCode.DELETE)
                            || k.getCode().equals(KeyCode.BACK_SPACE)) {

                        String input = txtField_Loc_tab2.getText();
                        
                        updateLocation(superController.getLocations(input));
                    }
                }
            }

            if (event.getSource().equals(b_Loc_tab2_fertig)) {
                // add selected Location to database

                TreeItem<Location> selectedItem = trv_Loc_tab2
                        .getSelectionModel().getSelectedItem();
                if (selectedItem == null) {
                    // no item selected;
                    return;
                }
                if (account == null) {
                    System.out.println("an error occured!");
                    return;
                }
                // print message
                superController.setInfo(Labels.DBOPT_INPROCESS);
                superController.setLocation(account.getId(), selectedItem
                        .getValue().getId());
                // print message
                superController.setInfo(Labels.DBOPT_UPDATED,
                        Labels.DBOPT_INPROCESS);
                dialogStage.close();
            }

            if (event.getSource().equals(b_Loc_tab2_zurueck)) {
                // change tab
                tab_Loc_tab2.setDisable(true);
                tab_Loc_tab1.setDisable(false);
                tabPane_Loc.getSelectionModel().select(tab_Loc_tab1);
            }

        }

        private void updateLocation(List<Location> list) {
            TreeItem<Location> rootItem = new TreeItem<Location>(new Location(
                    0, "World", "0000", null));
            rootItem.setExpanded(false);
            for (Location location : list) {
                rootItem.getChildren().add(new TreeItem<Location>(location));
            }
            trv_Loc_tab2.setRoot(rootItem);
        }
    }

    /**
     * contains handle-method for all actions contained in the add category
     * window
     * 
     * @author Matthias
     * 
     */
    private class MyCatEventHandler implements EventHandler<Event> {

        @Override
        public void handle(Event event) {

            // ########################################## tab1
            // ##############################

            if (event.getSource().equals(txtField_Cat_tab1)) {
                // update listView of account

                if ((event instanceof KeyEvent)) {

                    KeyEvent k = (KeyEvent) event;

                    if (!k.getText().isEmpty()
                            || k.getCode().equals(KeyCode.DELETE)
                            || k.getCode().equals(KeyCode.BACK_SPACE)) {

                        String input = txtField_Cat_tab1.getText();
                        // update list
                        list_Cat_tab1.getItems().clear();
                        for (Account a : superController.getAccounts(input)) {
                            list_Cat_tab1.getItems().add(a);
                        }

                    }
                }
            }

            if (event.getSource().equals(b_Cat_tab1)) {
                // save selected account inactivate tab

                Account selectedAccount = list_Cat_tab1.getSelectionModel()
                        .getSelectedItem();
                if (selectedAccount == null) {
                    return;
                }
                account = selectedAccount;

                // change tab
                tab_Cat_tab1.setDisable(true);
                tab_Cat_tab2.setDisable(false);
                tabPane_Cat.getSelectionModel().select(tab_Cat_tab2);

                // get and view all categories already belonging to the selected
                // account

                List<Integer> categoryList = account.getCategoryIds();
                int[] categoryArr = new int[categoryList.size()];

                for (int i = 0; i < categoryArr.length; i++) {
                    categoryArr[i] = categoryList.get(i);
                }
                // System.out.println("ausgewählter account: " + account.getName()
                //        + " kategorien: " + account.getCategoryIds().size());
                Category rootCat = superController.getCategoryRoot(categoryArr);
                // set new root item in treeView
                if (rootCat != null) {
                    TreeItem<Category> rootItem = updateCategory(rootCat);
                    trv_Cat_tab2_oldCats.setRoot(rootItem);
                } else {
                    trv_Cat_tab2_oldCats.setRoot(null);
                }
                // set label to show the selected account and expand first item
                // of treeView
                l_Cat_tab2_selectedAccount.setText(account.getName());
                trv_Cat_tab2.setRoot(updateCategory(superController
                        .getCategoryRoot("")));

            }

            // ########################### tab2  ###########################
            

            if (event.getSource().equals(txtField_Cat_tab2)) {
                // update TreeView of categories

                if ((event instanceof KeyEvent)) {

                    KeyEvent k = (KeyEvent) event;

                    if (!k.getText().isEmpty()
                            || k.getCode().equals(KeyCode.DELETE)
                            || k.getCode().equals(KeyCode.BACK_SPACE)) {

                        String input = txtField_Cat_tab2.getText();
                        //System.out.println("zweites suchfeld: " + input);
                        trv_Cat_tab2.setRoot(updateCategory(superController
                                .getCategoryRoot(input)));
                    }
                }
            }

            if (event.getSource().equals(trv_Cat_tab2)) {
                // add a category to list of selected categories

                // only select on double click
                if (event instanceof MouseEvent) {
                    MouseEvent mouse = (MouseEvent) event;
                    if (mouse.getClickCount() == 2) {

                        if (trv_Cat_tab2.getSelectionModel().getSelectedItem() == null) {
                            // no real item selected, just click event
                            return;
                        }
                        Category cat = trv_Cat_tab2.getSelectionModel()
                                .getSelectedItem().getValue();
                        trv_Cat_tab2.getSelectionModel().clearSelection();

                        if (!list_Cat_tab2.getItems().contains(cat)) {
                            // add a category only once
                            list_Cat_tab2.getItems().add(cat);

                        }
                    }
                }

            }

            if (event.getSource().equals(b_Cat_tab2_entfernen)) {
                // remove a category from list of selected categories

                int index = list_Cat_tab2.getSelectionModel()
                        .getSelectedIndex();

                if (index >= 0) {
                    list_Cat_tab2.getItems().remove(index);
                    list_Cat_tab2.setItems(list_Cat_tab2.getItems());
                }
            }

            if (event.getSource().equals(b_Cat_tab2_fertig)) {
                // add selected Categories to database

                // print message
                superController.setInfo(Labels.DBOPT_INPROCESS);

                // add categories
                for (Category cat : list_Cat_tab2.getItems()) {
                    superController.setCategory(account.getId(), cat.getId());
                    System.out.println("2");
                }

                // print message
                superController.setInfo(Labels.DBOPT_UPDATED,
                        Labels.DBOPT_INPROCESS);

            }

            if (event.getSource().equals(b_Cat_tab2_zurueck)) {
                // change tab
                tab_Cat_tab2.setDisable(true);
                tab_Cat_tab1.setDisable(false);
                tabPane_Cat.getSelectionModel().select(tab_Cat_tab1);
            }

        }

        /**
         * creates tree for treeView
         * 
         * @param rootCategory
         * @return root-item
         */
        private TreeItem<Category> updateCategory(Category rootCategory) {

            TreeItem<Category> rootItem = new TreeItem<Category>(rootCategory);
            rootItem.setExpanded(true);
            updateCategoryRec(rootCategory, rootItem);
            return rootItem;

        }

        private void updateCategoryRec(Category category,
                TreeItem<Category> item) {
            for (Category childCategory : category.getChilds()) {
                TreeItem<Category> child = new TreeItem<Category>(childCategory);
                child.setExpanded(false);
                updateCategoryRec(childCategory, child);
                item.getChildren().add(child);
            }
        }

    }

    /**
     * contains handle-method for all actions contained in the add account
     * window
     * 
     * @author Matthias
     * 
     */
    private class MyAccEventHandler implements EventHandler<Event> {
        @Override
        public void handle(Event event) {
            if (event.getSource().equals(b_Acc_tab1_suchen)) {
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

                // fill found users/accounts in listView
                Iterator<User> it = list.iterator();
                while (it.hasNext()) {
                    UserContainer userW = new UserContainer(it.next());
                    list_Acc_tab1.getItems().add(userW);

                }

            }
            if (event.getSource().equals(b_Acc_tab1_hinzufuegen)) {
                // add account/user to database

                UserContainer userC = list_Acc_tab1.getSelectionModel()
                        .getSelectedItem();
                //System.out.println("hinzugefuegen:   "
                      //  + userC.getUser().getURL());

                // print message
                superController.setInfo(Labels.DBOPT_INPROCESS);

                superController.addUserToWatch(userC.getUser(),
                        DEFAULT_LOCATION);
                //System.out.println(userC.getUser().getName() + "    "
                    //    + userC.getUser().getScreenName());

                // print message
                superController.setInfo(Labels.DBOPT_UPDATED,
                        Labels.DBOPT_INPROCESS);

            }
            if (event.getSource().equals(b_Acc_tab1_schliessen)) {
                dialogStage.close();
            }
        }

    }
}