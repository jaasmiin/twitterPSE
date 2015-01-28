package gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;

public abstract class GUIElement implements Initializable {
	protected GUIController superController;
	public static enum UpdateType {TWEET, CATEGORY, LOCATION, ACCOUNT, ERROR,
		CATEGORY_SELECTION, LOCATION_SELECTION, ACCOUNT_SELECTION};
	public abstract void update(UpdateType type);
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		superController = GUIController.getInstance();
	}
	
}
