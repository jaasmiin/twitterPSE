package gui.selectionOfQuery;

import java.net.URL;
import java.util.ResourceBundle;

import mysql.result.Category;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeView;
import gui.InputElement;
import gui.base.GUIController;

public class SelectionOfQueryController implements EventHandler<Event>, InputElement {
	@FXML
	private TextField txtFilterSearch;
	@FXML
	private TreeView<String> trwCategory, trwLocation;
	@FXML
	private TitledPane tipLocation, tipAccount, tipCategory;
	
	private GUIController parent;
	
	
	/**
	 * Set super controller.
	 * @param parent is a super controller, which coordinates between GUI elements.
	 */
	public void setParent(GUIController parent) {
		System.out.println("setParent");
		this.parent = parent;
		
		trwCategory.setOnMouseClicked(this);
		trwLocation.setOnMouseClicked(this);
		txtFilterSearch.setOnKeyReleased(this);
		
		for (Category category : parent.getCategories()) {
			// TODO: add categories
		}
	}
	
	@Override
	public void update(UpdateType type) {
		if (type == UpdateType.TWEET) {
			// TODO: load data and update elements
		} else if (type == UpdateType.LOCATION) {
			// TODO: load data and update elements
		} else if (type == UpdateType.CATEGORY) {
			// TODO: load data and update elements
		} 
	}
	
	@Override
	public void handle(Event e) {
		if (e.getSource().equals(trwCategory)) {
			System.out.println("Kategorie");
			// TODO: update selection list & map
		} else if (e.getSource().equals(trwLocation)) {
			System.out.println("Ort");
			// TODO: update selection list & map
		} else if (e.getSource().equals(txtFilterSearch)) {
			System.out.println("Eingabe: " + txtFilterSearch.getText());
			// TODO: reload list
		} else {
			System.out.println("Something else.");
			// TODO: update selection list & map
		}
	}

}
