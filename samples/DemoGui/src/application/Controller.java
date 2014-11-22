package application;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebView;

public class Controller implements Initializable {

    @FXML
    private TreeView<String> treeViewCategory;

    @FXML
    private ListView<String> listViewCountry;
    private ObservableList<String> countryData;
    
    @FXML
    private ListView<String> listViewCategory;
    private ObservableList<String> categoryData;

    @FXML
    private WebView webView;

    @FXML
    private TreeView<String> treeViewCountry;
    
    @FXML
    private MenuItem menuItemAddAccount;

    @FXML
    private TableView<TableEntry> tableViewData;
    
    @FXML
    private TableColumn<TableEntry, String> tableColumnValue;
    
    @FXML
    private TableColumn<TableEntry, String> tableColumnAspect;
    private ObservableList<TableEntry> entries = FXCollections.observableArrayList(
			new TableEntry("Invalid Accounts", "100"),
			new TableEntry("USA", "1000"),
			new TableEntry("GER", "2000"));
    
    private DemoProvider provider;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		provider = new DemoProvider();
		
		initWebView();
		initTreeViewCountry();
		initListViewCountry();
		initTreeViewCategory();
		initListViewCategory();
		initTableViewData();
	}
	
	private void initTableViewData() {
		tableViewData.setEditable(false);			
		tableColumnAspect.setCellValueFactory(new PropertyValueFactory<TableEntry, String>("aspect"));
		tableColumnValue.setCellValueFactory(new PropertyValueFactory<TableEntry, String>("value"));				
	}
	
	
	/**
	 * Loads world map into webView.
	 */
	private void initWebView() {
		//loading worldmap to webView
		//html document you want to load, if outside of classpath, give full address
		String html = "worldMapEmpty.html";
		java.net.URI uri = java.nio.file.Paths.get(html).toAbsolutePath().toUri();
		webView.getEngine().load(uri.toString());
	}
	
	/**
	 * Inits TreeViewCountry with hierarchy of countries and adds selectionHandler.
	 */
	private void initTreeViewCountry() {
		TreeItem<String> rootItem = new TreeItem<String>("World");
		rootItem.setExpanded(true);
		for (String country : provider.getCountries()) {
			rootItem.getChildren().add(new TreeItem<String>(country));			
		}
		treeViewCountry.setRoot(rootItem);
		addSelectionHandlerCountry();
	}
	
	/**
	 * Inits listViewCountry with no elements and adds discardHandler
	 */
	private void initListViewCountry() {
		listViewCountry.setOrientation(Orientation.HORIZONTAL);
		countryData = FXCollections.observableList(provider.getSelectedCountries());
		listViewCountry.setItems(countryData);
		addDiscardHandlerCountry();
	}
	
	/**
	 * Add items from treeViewCountry to listViewCountry by double-click.
	 */
	private void addSelectionHandlerCountry() {
		treeViewCountry.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				if (event.getClickCount() == 2) {
					TreeItem<String> item = treeViewCountry.getSelectionModel().getSelectedItem();
					//actualize ListViewCountry
					countryData.add(item.getValue());
				}
			}
		});
	}
	
	/**
	 * Remove items from listViewCountry by double-clicking them.
	 */
	private void addDiscardHandlerCountry() {
		listViewCountry.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				if (event.getClickCount() == 2) {
					int index = listViewCountry.getSelectionModel().getSelectedIndex();
					countryData.remove(index);	
				}
			}
		});
	}
	
	private void initTreeViewCategory() {
		TreeItem<String> rootItem = new TreeItem<String>("All");
		rootItem.setExpanded(true);
		for (String category : provider.getCategories()) {
			rootItem.getChildren().add(new TreeItem<String>(category));			
		}
		treeViewCategory.setRoot(rootItem);
		addSelectionHandlerCategory();
	}
	
	private void initListViewCategory() {
		listViewCategory.setOrientation(Orientation.HORIZONTAL);
		categoryData = FXCollections.observableList(provider.getSelectedCategories());
		listViewCategory.setItems(categoryData);
		addDiscardHandlerCategory();
	}
	
	private void addSelectionHandlerCategory() {
		treeViewCategory.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				if (event.getClickCount() == 2) {
					TreeItem<String> item = treeViewCategory.getSelectionModel().getSelectedItem();
					//actualize ListViewCategory
					categoryData.add(item.getValue());
				}
			}
		});
	}
	
	/**
	 * Remove items from listViewCategory by double-clicking them.
	 */
	private void addDiscardHandlerCategory() {
		listViewCategory.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				if (event.getClickCount() == 2) {
					int index = listViewCategory.getSelectionModel().getSelectedIndex();
					categoryData.remove(index);
				}				
			}
		});
	}
	

}

