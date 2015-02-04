package gui.selectionOfQuery;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import mysql.result.Account;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import gui.InputElement;
import gui.Labels;
import gui.RunnableParameter;
import gui.Util;

public class SelectionOfQueryController extends InputElement implements
        EventHandler<Event>, Initializable {

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

    private void updateAccounts(List<Account> accounts) {
        lstAccount.getItems().clear();
        for (Account a : accounts) {
            lstAccount.getItems().add(a);
        }
        tipAccount.setDisable(false);
    }

    private void updateCategory(Category rootCategory) {
        TreeItem<Category> rootItem = new TreeItem<Category>(rootCategory);
        rootItem.setExpanded(true);
        updateCategoryRec(rootCategory, rootItem);
        trvCategory.setRoot(rootItem);
        tipCategory.setDisable(false);
    }

    private void updateCategoryRec(Category category, TreeItem<Category> item) {
        if (category.isUsed()) {
            for (Category childCategory : category.getChilds()) {
                TreeItem<Category> child = new TreeItem<Category>(childCategory);
                updateCategoryRec(childCategory, child);
                item.getChildren().add(child);
            }
        }
    }

    private void updateLocation(List<Location> list) {
        TreeItem<Location> rootItem = new TreeItem<Location>(new Location(0,
                Util.getUppercaseStart(Labels.WORLD), "0000", null));
        rootItem.setExpanded(true);
        for (Location location : list) {
            rootItem.getChildren().add(new TreeItem<Location>(location));
        }
        trvLocation.setRoot(rootItem);
        tipLocation.setDisable(false);
    }

    @Override
    public void update(UpdateType type) {
        if (type == UpdateType.LOCATION) {
            updateLocation(superController.getLocations(txtFilterSearch
                    .getText()));
        } else if (type == UpdateType.CATEGORY) {
            updateCategory(superController.getCategoryRoot(txtFilterSearch
                    .getText()));
        } else if (type == UpdateType.ACCOUNT) {
            updateAccounts(superController.getAccounts(txtFilterSearch
                    .getText()));
        } else if (type == UpdateType.ERROR) {

        }
    }

    @FXML
    @Override
    public void handle(Event e) {
        if (e instanceof MouseEvent && ((MouseEvent) e).getClickCount() == 2) {
            if (e.getSource().equals(trvCategory)) {
                if (trvCategory.getSelectionModel().getSelectedItem() != null) {
                    new Thread(new RunnableParameter<Integer>(trvCategory
                            .getSelectionModel().getSelectedItem().getValue()
                            .getId()) {
                        @Override
                        public void run() {
                            superController
                                    .setSelectedCategory(parameter, true);
                        }
                    }).start();
                }
            } else if (e.getSource().equals(trvLocation)) {
                if (trvLocation.getSelectionModel().getSelectedItem() != null) {
                    new Thread(new RunnableParameter<Integer>(trvLocation
                            .getSelectionModel().getSelectedItem().getValue()
                            .getId()) {
                        @Override
                        public void run() {
                            superController
                                    .setSelectedLocation(parameter, true);
                        }
                    }).start();

                }
            } else if (e.getSource().equals(lstAccount)) {
                if (lstAccount.getSelectionModel().getSelectedItem() != null) {
                    new Thread(new RunnableParameter<Integer>(lstAccount
                            .getSelectionModel().getSelectedItem().getId()) {
                        @Override
                        public void run() {
                            superController.setSelectedAccount(parameter, true);
                        }
                    }).start();
                }
            }
        } else if (e.getSource().equals(txtFilterSearch)) {
            if (e instanceof KeyEvent) {
                KeyEvent k = (KeyEvent) e;
                if (!k.getText().isEmpty()
                        || k.getCode().equals(KeyCode.DELETE)
                        || k.getCode().equals(KeyCode.BACK_SPACE)) {
                    if (tipCategory.isExpanded()) {
                        updateCategory(superController
                                .getCategoryRoot(txtFilterSearch.getText()));
                    } else if (tipAccount.isExpanded()) {
                        updateAccounts(superController
                                .getAccounts(txtFilterSearch.getText()));
                    } else if (tipLocation.isExpanded()) {
                        updateLocation(superController
                                .getLocations(txtFilterSearch.getText()));
                    }
                }
            }
        }
    }

    private void setLabels() {
        tipAccount.setText(Util.getUppercaseStart(Labels.ACCOUNT));
        tipCategory.setText(Util.getUppercaseStart(Labels.CATEGORY));
        tipLocation.setText(Util.getUppercaseStart(Labels.LOCATION));

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        superController.subscribe(this);
        setLabels();
    }

}
