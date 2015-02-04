package gui.unfoldingIntegration;

import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.JFrame;

import gui.OutputElement;
import gui.standardMap.StandardMapDialog;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.layout.BorderPane;

public class IntegrationController extends OutputElement implements Initializable {
	
    @FXML
    private BorderPane mapPane;
    
    private StandardMapDialog mapApp;
        
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.initialize(location, resources);
		superController.subscribe(this);

		mapApp = new StandardMapDialog(superController);
		mapApp.setVisible(false);
		mapApp.setAlwaysOnTop(true);
	}

	@Override
	public void update(UpdateType type) {
		switch (type) {
		case GUI_STARTED : 
			
		case WINDOW_RESIZE : 
			positionDialogue();
			break;
			
		case MAP_SELECTED :
			mapApp.setVisible(true);
			break;
			
		case MAP_UNSELECTED :
			mapApp.setVisible(false);
			break;
			
		case CLOSE :
			mapApp.closeMap();
			
		default : 
			// nothing to do
		}
		
	}
	
	private void positionDialogue() {
		Bounds border = mapPane.localToScreen(mapPane.getBoundsInLocal());
		mapApp.setBounds((int) border.getMinX(),(int) border.getMinY(),
				(int) border.getWidth(),(int) border.getHeight());	
		mapApp.setSize((int) border.getWidth(), (int) border.getHeight());
	}
	

}
