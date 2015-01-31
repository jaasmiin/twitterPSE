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
public class ContentTableController extends OutputElement implements
        Initializable {

    @FXML
    private TableView<Account> table;
    TableColumn<Account, Integer> retweetColumn;

    @Override
    public void update(UpdateType type) {
        if (type == UpdateType.TWEET) {
            ObservableList<Account> accountList = FXCollections
                    .observableArrayList(superController.getDataByAccount());
            if (accountList.isEmpty()) {
                table.setItems(null);
            } else {
                table.setItems(accountList);
            }
        } else if (type == UpdateType.LOCATION) {
            // addLocationColumns();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        superController.subscribe(this);

        addAccountsColumn();
        addTweetsColumn();
        addRetweetsColumn();
        addFollowerColumn();
    }

    /**
     * Adds a column containing the names of the accounts to the table.
     */
    private void addAccountsColumn() {
        TableColumn<Account, String> accountsColumn = new TableColumn<Account, String>(
                "Accounts");
        accountsColumn
                .setCellValueFactory(new PropertyValueFactory<Account, String>(
                        "name"));
        table.getColumns().add(accountsColumn);
    }

    /**
     * Adds a column containing the number of tweets that an account sent to the
     * table.
     */
    private void addTweetsColumn() {
        TableColumn<Account, Integer> tweetsColumn = new TableColumn<Account, Integer>(
                "Tweets");

        tweetsColumn
                .setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Account, Integer>, ObservableValue<Integer>>() {

                    @Override
                    public ObservableValue<Integer> call(
                            CellDataFeatures<Account, Integer> account) {
                        int tweetNumber = 0;
                        // TODO: remove test print
                        System.out.println("account.getName = "
                                + account.getValue().getName());
                        System.out.println("account.getTweets.size = "
                                + account.getValue().getTweets().size());
                        System.out.println("account.getCounter = "
                                + account.getValue().getTweets().get(0)
                                        .getCounter());

                        if (account.getValue() != null) {
                            tweetNumber = account.getValue().getTweets().get(0)
                                    .getCounter();
                        }
                        return new SimpleIntegerProperty(tweetNumber)
                                .asObject();
                    }

                });

        table.getColumns().add(tweetsColumn);
    }

    /**
     * Adds columns containing the number of retweets per country a certain
     * account received to the table.
     */
    private void addRetweetsColumn() {
        retweetColumn = new TableColumn<>("Retweets");

        // TODO: only less than ideal solution, because retweets aren't given
        // country-wise
        retweetColumn
                .setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Account, Integer>, ObservableValue<Integer>>() {

                    @Override
                    public ObservableValue<Integer> call(
                            CellDataFeatures<Account, Integer> account) {
                        int retweetNumber = 0;
                        if (account.getValue() != null) {
                            retweetNumber = account.getValue().getRetweets()
                                    .get(0).getCounter();
                        }
                        return new SimpleIntegerProperty(retweetNumber)
                                .asObject();
                    }

                });

        table.getColumns().add(retweetColumn);
    }

    /**
     * This method adds country subcolumns to retweetColumn.
     */
    private void addLocationColumns() {
        // TODO: improve performance (have to iterate over retweetlist for every
        // location)
        for (Location currentLocation : superController.getLocations()) {
            final Location tempLocation = currentLocation;
            TableColumn<Account, Integer> countryColumn = new TableColumn<>(
                    tempLocation.toString());
            countryColumn
                    .setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Account, Integer>, ObservableValue<Integer>>() {

                        @Override
                        public ObservableValue<Integer> call(
                                CellDataFeatures<Account, Integer> account) {
                            int retweetsPerCountry = 0;
                            if (account.getValue() != null) {
                                for (Retweets r : account.getValue()
                                        .getRetweets()) {
                                    if (r.getLocationCode().equals(
                                            tempLocation.getLocationCode())) {
                                        retweetsPerCountry = r.getCounter();
                                        break;
                                    }
                                }
                            }
                            return new SimpleIntegerProperty(retweetsPerCountry)
                                    .asObject();
                        }

                    });
            retweetColumn.getColumns().add(countryColumn);
        }
    }

    /**
     * This method adds a column displaying the number of followers of an
     * account to the table.
     */
    private void addFollowerColumn() {
        TableColumn<Account, Integer> followerColumn = new TableColumn<>(
                "Follower");

        followerColumn
                .setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Account, Integer>, ObservableValue<Integer>>() {

                    @Override
                    public ObservableValue<Integer> call(
                            CellDataFeatures<Account, Integer> account) {
                        int followerNumber = 0;
                        if (account.getValue() != null) {
                            followerNumber = account.getValue().getFollower();
                        }
                        return new SimpleIntegerProperty(followerNumber)
                                .asObject();
                    }

                });

        table.getColumns().add(followerColumn);
    }

}
