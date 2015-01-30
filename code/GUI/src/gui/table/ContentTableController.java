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
    private TableView<Account> table;
	
	@Override
	public void update(UpdateType type) {
		System.out.println("ContentTable updated");
		if (type == UpdateType.TWEET) {
			ObservableList<Account> accountList = FXCollections.observableArrayList(superController.getDataByAccount());
			System.out.println("AccountsList.size = " + accountList.size());
			table.setItems(accountList);	
		}		
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.initialize(location, resources);
		superController.subscribe(this);
		System.out.println("ContentTable initialized");
		
		addAccountsColumn();
		addTweetsColumn();
		addRetweetsColumn();		
	}
	
	/**
	 * Adds a column containing the names of the accounts to the table.
	 */
	private void addAccountsColumn() {
		TableColumn<Account, String> accountsColumn = new TableColumn<Account, String>("Accounts");		
		accountsColumn.setCellValueFactory(new PropertyValueFactory<Account, String>("name"));		
		table.getColumns().add(accountsColumn);
	}
	
	/**
	 * Adds a column containing the number of tweets that an account sent to the table.
	 */
	private void addTweetsColumn() {
		TableColumn<Account, Integer> tweetsColumn = new TableColumn<Account, Integer>("Tweets");		
		
		tweetsColumn.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<Account, Integer>, ObservableValue<Integer>>() {

			@Override
			public ObservableValue<Integer> call(CellDataFeatures<Account, Integer> account) {
				int tweetNumber = 0;
				if (account.getValue() != null) {
					tweetNumber = account.getValue().getTweets().size();
				} 				
				return new SimpleIntegerProperty(tweetNumber).asObject();
			}
			
		});		
		
		table.getColumns().add(tweetsColumn);
	}
	
	/**
	 * Adds columns containing the number of retweets per country
	 * a certain account received to the table.
	 */
	private void addRetweetsColumn() {
		TableColumn<Account, Integer> retweetColumn = new TableColumn<>("Retweets");		
		
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
		
		table.getColumns().add(retweetColumn);
	}

}
