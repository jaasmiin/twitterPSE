package gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import gui.base.GUIController;

public abstract class GUIElement implements Initializable {
	protected GUIController superController;
	public static enum UpdateType {TWEET, CATEGORY, LOCATION, ACCOUNT};
	public abstract void update(UpdateType type);
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		superController = GUIController.getInstance();
	}
	
}
