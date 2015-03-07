package gui.table;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import mysql.result.Account;
import mysql.result.Location;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import gui.Labels;
import gui.OutputElement;
import gui.PRunnable;

/**
 * This class presents raw-data in a table.
 * 
 * The format looks like this:
 * 
 * Account | Follower |               Retweets
 *         |          | Total | ... | Germany | ... other locations
 * ----------------------------------------------------------------
 *  KIT    |   20     |  60   | ... |  51     | ...
 * 
 * 
 * @author Philipp
 *
 */
public class ContentTableController extends OutputElement implements Initializable {
	
	/**
	 * The tab in application of this view.
	 */
	@FXML
	private Tab tabTable;
	
	/**
	 * The table that is displayed on screen.
	 */
	@FXML
    private TableView<InternAccount> table;
	
	/**
	 * The column containing the numbers of retweets.
	 */
	private TableColumn<InternAccount, Integer> retweetColumn;
	
	/**
	 * The List containing the data to be displayed in the table.
	 */
	private ObservableList<InternAccount> data;
	
	@Override
	public void update(UpdateType type) {
		Platform.runLater(new PRunnable<UpdateType>(type) {
			@Override
			public void run(UpdateType updateType) {
				if (updateType == UpdateType.TWEET_BY_ACCOUNT) {
					fillData(superController.getDataByAccount());			
					table.setItems(data);
				} else if (updateType == UpdateType.LOCATION) {
					addLocationColumns();
				}
			}
		});
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.initialize(location, resources);
		superController.subscribe(this);
		
		data = FXCollections.observableArrayList();
		
		tabTable.setText(Labels.TABLE);
		
		addAccountsColumn();
		addFollowerColumn();
		addRetweetsColumn();		
	}
	
	/**
	 * This method clears the data list and inserts new data.
	 * 
	 * @param accounts a list of accounts
	 */
	private void fillData(List<Account> accounts) {
		// clear list before inserting new data
		data.removeAll(data);
		for (Account a : accounts) {
			data.add(new InternAccount(a.getName(), a.getFollower(), a.getRetweets()));
		}
	}
	
	/**
	 * Adds a column containing the names of the accounts to the table.
	 */
	private void addAccountsColumn() {
		TableColumn<InternAccount, String> accountsColumn = new TableColumn<InternAccount, String>(Labels.ACCOUNT);		
		accountsColumn.setCellValueFactory(new PropertyValueFactory<InternAccount, String>("accountName"));		
		
		accountsColumn.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<InternAccount, String>, ObservableValue<String>>() {

			@Override
			public ObservableValue<String> call(CellDataFeatures<InternAccount, String> account) {
				String name = null;				
				if (account.getValue() != null) {
					name = account.getValue().getAccountName();					
				} 				
				return new SimpleStringProperty(name);
			}
			
		});
				
		table.getColumns().add(accountsColumn);
	}
	
	
	
	/**
	 * Adds columns containing the number of retweets per country
	 * a certain account received to the table.
	 */
	private void addRetweetsColumn() {
		retweetColumn = new TableColumn<>(Labels.RETWEETS);
		table.getColumns().add(retweetColumn);
	}
	
	/**
	 * This method adds a column displaying the total number of retweets to tweets of a certain account 
	 * to the retweetsColumn.
	 */
	private void addTotalRetweetsColumn() {		
		TableColumn<InternAccount, Integer> sumColumn = new TableColumn<>(Labels.TOTAL);		
		sumColumn.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<InternAccount, Integer>, ObservableValue<Integer>>() {

			@Override
			public ObservableValue<Integer> call(CellDataFeatures<InternAccount, Integer> account) {
				int retweetNumber = 0;				
				if (account.getValue() != null) {
					retweetNumber = account.getValue().getTotalRetweets();					
				} 				
				return new SimpleIntegerProperty(retweetNumber).asObject();
			}
			
		});
		
		retweetColumn.getColumns().add(sumColumn);	
	}
	
	/**
	 * This method adds country subcolumns to retweetColumn.
	 */
	private void addLocationColumns() {		
		addTotalRetweetsColumn();
		
		for (Location currentLocation : superController.getLocations()) {
			final Location tempLocation = currentLocation;
			TableColumn<InternAccount, Integer> countryColumn = new TableColumn<>(tempLocation.toString());
			countryColumn.setCellValueFactory(
					new Callback<TableColumn.CellDataFeatures<InternAccount, Integer>, ObservableValue<Integer>>() {

						@Override
						public ObservableValue<Integer> call(
								CellDataFeatures<InternAccount, Integer> account) {
							int retweetsPerCountry = 0;
							if (account.getValue() != null) {
								retweetsPerCountry = account.getValue().getRetweetNumber(
													tempLocation.getLocationCode());
							} 
							return new SimpleIntegerProperty(retweetsPerCountry).asObject();
						}
						
					});
			retweetColumn.getColumns().add(countryColumn);
		}	
	}
	
	/**
	 * This method adds a column displaying the number of followers of an account to the table.
	 */
	private void addFollowerColumn() {
		TableColumn<InternAccount, Integer> followerColumn = new TableColumn<>(Labels.FOLLOWER);
		
		followerColumn.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<InternAccount, Integer>, ObservableValue<Integer>>() {

			@Override
			public ObservableValue<Integer> call(CellDataFeatures<InternAccount, Integer> account) {
				int followerNumber = 0;				
				if (account.getValue() != null) {
					followerNumber = account.getValue().getFollower();					
				} 				
				return new SimpleIntegerProperty(followerNumber).asObject();
			}
			
		});
		
		table.getColumns().add(followerColumn);
	}
	
}
