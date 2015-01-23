package gui.databaseOptions;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import mysql.result.Category;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
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
	@FXML
	private Button zurueck;
	@FXML 
	private Button fertig;
	@FXML
	private TreeView<Category> trvCat;
	@FXML
	private TextField txtCatSearch;
	@FXML
	private ListView<Category> listCat;

	@Override
	public void update(UpdateType type) {
		// TODO Auto-generated method stub
	}

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.initialize(location, resources);
		if(addCat != null) {
			addCat.setOnAction(new MyActionEventHandler());
			//TODO addLoc
			//TODO addAcount
		}
		//popUp add Category
		if(txtCatSearch != null) {
			System.out.println("eingabe");
			txtCatSearch.setOnKeyPressed(new MyEventHandler());
			trvCat.setOnMouseClicked(new MyEventHandler());
		}
	}
	/**
	 * creates tree for treeView
	 * @param rootCategory
	 */
	private void updateCategory(Category rootCategory) {
		TreeItem<Category> rootItem = new TreeItem<Category>(rootCategory);
		rootItem.setExpanded(true);
		updateCategoryRec(rootCategory, rootItem);
		// TODO: implement
		trvCat.setRoot(rootItem);
	}
	private void updateCategoryRec(Category category, TreeItem<Category> item) {
		for (Category childCategory : category.getChilds()) {
			TreeItem<Category> child = new TreeItem<Category>(childCategory);
			child.setExpanded(true);
			updateCategoryRec(childCategory, child);
			item.getChildren().add(child);
		}
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
				// popUp for InputDialog
				// Load the fxml file and create a new stage for the popup
			    FXMLLoader loader = new FXMLLoader(DatabaseOptController.class.getResource("CatSelect.fxml"));
			    AnchorPane page;
			    
				try {
					
					page = (AnchorPane) loader.load();
					Stage dialogStage = new Stage();
				    dialogStage.setTitle("Kategorie auswählen");
				    dialogStage.initModality(Modality.WINDOW_MODAL);
				    //dialogStage.initOwner(primaryStage);
				    Scene scene = new Scene(page);
				    dialogStage.setScene(scene);
				    dialogStage.showAndWait();
				} catch (IOException e1) {
					
					System.out.println("Unable to construct popUp");
					e1.printStackTrace();
				}
			    
			}
		}	
	}
	/**
	 * Inner class that manages all Event activities
	 * @author Matthias
	 *
	 */
	private class MyEventHandler implements EventHandler<Event>{

		@Override
		public void handle(Event event) {
			System.out.println("kontroller:" + event.getSource());
			if(event.getSource().equals(txtCatSearch)) {
				String input = txtCatSearch.getText();
				System.out.println(input);
				updateCategory(superController.getCategoryRoot(input));
			}
			if(event.getSource().equals(trvCat)) {
				Category cat = trvCat.getSelectionModel().getSelectedItem().getValue();
				listCat.getItems().add(cat);
			}
			
		}
		
	}
}
