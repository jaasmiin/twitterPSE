package gui.table;

import java.net.URL;
import java.util.ResourceBundle;

import mysql.result.Account;
import mysql.result.Location;
import mysql.result.Retweets;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import gui.OutputElement;

/**
 * This class presents raw-data in a table.
 * 
 * @author Philipp
 *
 */
public class ContentTableController extends OutputElement implements Initializable {
	
	@FXML
    private TableView<InternAccount> table;
	TableColumn<InternAccount, Integer> retweetColumn;
	ObservableList<InternAccount> data;
	
	@Override
	public void update(UpdateType type) {
		if (type == UpdateType.TWEET) {
			ObservableList<Account> accountList = FXCollections.observableArrayList(superController.getDataByAccount());		
			if (accountList.isEmpty()) {
				table.setItems(null);
			} else {
				table.setItems(accountList);
			}	
		} else if (type == UpdateType.LOCATION) {
			addLocationColumns();
		}
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.initialize(location, resources);
		superController.subscribe(this);
		
		data = FXCollections.observableArrayList();
		
		addAccountsColumn();
		addRetweetsColumn();
		addFollowerColumn();
	}
	
	/**
	 * Adds a column containing the names of the accounts to the table.
	 */
	private void addAccountsColumn() {
		TableColumn<InternAccount, String> accountsColumn = new TableColumn<InternAccount, String>("Accounts");		
		accountsColumn.setCellValueFactory(new PropertyValueFactory<InternAccount, String>("name"));		
		table.getColumns().add(accountsColumn);
	}
	
	
	
	/**
	 * Adds columns containing the number of retweets per country
	 * a certain account received to the table.
	 */
	private void addRetweetsColumn() {
		retweetColumn = new TableColumn<>("Retweets");
		
		TableColumn<InternAccount, Integer> sumColumn = new TableColumn<>()
		
		// TODO: only less than ideal solution, because retweets aren't given country-wise
		retweetColumn.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<Account, Integer>, ObservableValue<Integer>>() {

			@Override
			public ObservableValue<Integer> call(CellDataFeatures<Account, Integer> account) {
				int retweetNumber = 0;				
				if (account.getValue() != null) {
					retweetNumber = account.getValue().getRetweets().get(0).getCounter();					
				} 				
				return new SimpleIntegerProperty(retweetNumber).asObject();
			}
			
		});
		
		table.getColumns().add(retweetColumn);
	}
	
	/**
	 * This method adds country subcolumns to retweetColumn.
	 */
	private void addLocationColumns() {
		// TODO: improve performance (have to iterate over retweetlist for every location)		
		for (Location currentLocation : superController.getLocations()) {
			final Location tempLocation = currentLocation;
			TableColumn<Account, Integer> countryColumn = new TableColumn<>(tempLocation.toString());
			countryColumn.setCellValueFactory(
					new Callback<TableColumn.CellDataFeatures<Account, Integer>, ObservableValue<Integer>>() {

						@Override
						public ObservableValue<Integer> call(
								CellDataFeatures<Account, Integer> account) {
							int retweetsPerCountry = 0;
							if (account.getValue() != null) {
								for (Retweets r : account.getValue().getRetweets()) {
									if (r.getLocation() == tempLocation.getId()) {
										retweetsPerCountry = r.getCounter();
										break;
									}
								}
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
		TableColumn<Account, Integer> followerColumn = new TableColumn<>("Follower");
		
		followerColumn.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<Account, Integer>, ObservableValue<Integer>>() {

			@Override
			public ObservableValue<Integer> call(CellDataFeatures<Account, Integer> account) {
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
