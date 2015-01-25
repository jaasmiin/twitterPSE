package gui.table;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import mysql.result.Account;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import gui.OutputElement;

/**
 * This class presents raw-data in a table.
 * 
 * @author Philipp
 *
 */
public class ContentTableController extends OutputElement implements Initializable {

	//Add columns as fields for update method!!!
	
	@FXML
    private TableView<Account> table;
	
	@Override
	public void update(UpdateType type) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.initialize(location, resources);
		superController.subscribe(this);
		
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
	 * Adds a column containing the number of tweets that account sent to the table.
	 */
	private void addTweetsColumn() {
		TableColumn<Account, Integer> tweetsColumn = new TableColumn<Account, Integer>("Tweets");		
		tweetsColumn.setCellValueFactory(new PropertyValueFactory<Account, Integer>("tweets"));		
		table.getColumns().add(tweetsColumn);
	}
	
	/**
	 * Adds columns containing the number of retweets per country to the table.
	 */
	private void addRetweetsColumn() {
		TableColumn retweetColumn = new TableColumn("Retweets");		
	/*
		// exchange getCountries
		List<String> countries = getCountries();	
		for (String country : countries) {
			retweetColumn.getColumns().add(new TableColumn(country));
			// add CellValueFactory
			// figure out which way to get retweets per country
		}	 */
		table.getColumns().add(retweetColumn);
	}

}
