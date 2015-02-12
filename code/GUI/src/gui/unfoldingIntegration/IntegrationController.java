package gui.unfoldingIntegration;

import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.JFrame;

import gui.OutputElement;
import gui.standardMap.StandardMapDialog;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

public class IntegrationController extends OutputElement implements Initializable {
	
    @FXML
    private BorderPane mapPane;
    
    private IntegratedMapDialog mapApp;
        
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.initialize(location, resources);
		superController.subscribe(this);

		mapApp = new IntegratedMapDialog(superController);
		//mapApp.setVisible(false);
		//mapApp.setAlwaysOnTop(true);
		showDialogue(false);
		
	}

	@Override
	public void update(UpdateType type) {
		switch (type) {
		case GUI_STARTED : 
			
		case WINDOW_RESIZE : 
			positionDialogue();
			break;
			
		case MAP_SELECTED :
			//mapApp.setVisible(true);
			showDialogue(true);
			break;
			
		case MAP_UNSELECTED :
			
		case WINDOW_HIDING :
			showDialogue(false);
			break;
			
		case CLOSE :
			mapApp.closeMap();
			
		default : 
			// nothing to do
		}
		
	}
	
	/**
	 * This method positions the dialogue containing the map.
	 * 
	 * The dialogue will be displayed inside the border of the mapPane
	 * with insets of 5 pxls on every side.
	 */
	private void positionDialogue() {
		Bounds border = mapPane.localToScreen(mapPane.getBoundsInLocal());
		mapApp.changeSize((int) border.getMinX() + 5, (int) border.getMinY() + 5, 
				(int) border.getWidth() - 5,(int) border.getHeight() - 5);
	}
	
	/**
	 * Toggles the visibility of the dialogue containing the map. 
	 *   
	 * @param visible 
	 */
	private void showDialogue(boolean visible) {
		mapApp.setVisible(visible);
		mapApp.setAlwaysOnTop(visible);
	}
	

	

}
