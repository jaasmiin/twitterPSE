package gui.selectionOfQuery;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mysql.result.Account;
import mysql.result.Category;
import mysql.result.Location;
import javafx.application.Platform;
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
import gui.PRunnable;
import gui.Util;
/**
 * Class where categories, locations and accounts can be selected out of a list.
 * @author Maximilian Awiszus
 *
 */
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

    private ExecutorService threadPool = Executors.newCachedThreadPool();
    
    private void updateAccounts(List<Account> accounts) {
    	Platform.runLater(new PRunnable<List<Account>>(accounts) {
			@Override
			public void run(List<Account> accounts) {
				lstAccount.getItems().clear();
		        for (Account a : accounts) {
		            lstAccount.getItems().add(a);
		        }
		        tipAccount.setDisable(false);
			}
		});
        
    }

    private void updateCategory(Category rootCategory) {
        Platform.runLater(new PRunnable<Category>(rootCategory) {
			@Override
			public void run(Category rootCategory) {
		        TreeItem<Category> rootItem = new TreeItem<Category>(rootCategory);
		        rootItem.setExpanded(true);
		        updateCategoryRec(rootCategory, rootItem);
		        trvCategory.setRoot(rootItem);
		        tipCategory.setDisable(false);
			}
        });
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
        Platform.runLater(new PRunnable<List<Location>>(list) {
			@Override
			public void run(List<Location> locations) {
				TreeItem<Location> rootItem = new TreeItem<Location>(new Location(0,
		                Util.getUppercaseStart(Labels.WORLD), "0000"));
		        rootItem.setExpanded(true);
				for (Location location : locations) {
		            rootItem.getChildren().add(new TreeItem<Location>(location));
		        }
		        trvLocation.setRoot(rootItem);
		        tipLocation.setDisable(false);
			}
		});
        
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

        } else if (type == UpdateType.DONT_LOAD) {
        	Platform.runLater(new Runnable() {
				@Override
				public void run() {
		        	setDisable(superController.isDontLoad());	
				}
			});
        }
    }
    
    private void setDisable(boolean disabled) {
    	txtFilterSearch.setDisable(disabled);
    	tipLocation.setDisable(disabled);
    	tipCategory.setDisable(disabled);
    	tipAccount.setDisable(disabled);
    }

    @FXML
    @Override
    public void handle(Event e) {
        if (e instanceof MouseEvent) {
        	MouseEvent me = (MouseEvent) e;
        	if (me.getClickCount() == 2) {
	            if (e.getSource().equals(trvCategory)) {
	                if (trvCategory.getSelectionModel().getSelectedItem() != null) {
	                    reloadCoategories();
	                }
	            } else if (e.getSource().equals(trvLocation)) {
	                if (trvLocation.getSelectionModel().getSelectedItem() != null) {
	                   reloadLocations();
	                }
	            } else if (e.getSource().equals(lstAccount)) {
	                if (lstAccount.getSelectionModel().getSelectedItem() != null) {
	                    reloadAccounts();
	                }
	            }
        	} else if (me.getClickCount() == 1) { // TODO: source?
        		if (me.getSource().equals(tipAccount) 
        				|| me.getSource().equals(tipCategory)
        				|| me.getSource().equals(tipLocation)) {
        			reloadActiveList();
        		}
        	}
        } else if (e.getSource().equals(txtFilterSearch)) {
            if (e instanceof KeyEvent) {
                KeyEvent k = (KeyEvent) e;
                if (!k.getText().isEmpty()
                        || k.getCode().equals(KeyCode.DELETE)
                        || k.getCode().equals(KeyCode.BACK_SPACE)) {
                    reloadActiveList();
                }
            }
        }
    }

    private void reloadAccounts() {
    	threadPool.execute(new PRunnable<Integer>(lstAccount
                .getSelectionModel().getSelectedItem().getId()) {
            @Override
            public void run(Integer accountID) {
                superController.setSelectedAccount(accountID, true);
            }
        });
    }
    
    private void reloadActiveList() {
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
    
    private void reloadCoategories() {
    	threadPool.execute(new PRunnable<Integer>(trvCategory
                .getSelectionModel().getSelectedItem().getValue()
                .getId()) {
            @Override
            public void run(Integer categoryID) {
                superController
                        .setSelectedCategory(categoryID, true);
            }
        });
    }
    
    private void reloadLocations() {
    	threadPool.execute(new PRunnable<Integer>(trvLocation
                 .getSelectionModel().getSelectedItem().getValue()
                 .getId()) {
             @Override
             public void run(Integer locationID) {
                 superController.setSelectedLocation(locationID, true);
             }
         });
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
