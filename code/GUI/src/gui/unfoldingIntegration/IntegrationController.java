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
    
    private boolean guiHasFocus;
    private boolean mapSelected;
    
    private FocusState currentState;
        
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.initialize(location, resources);
		superController.subscribe(this);
		
		//when GUI starts, GUI has focus
		guiHasFocus = true;

		mapApp = new IntegratedMapDialog(superController);
		currentState = new MapUnselected(this);
		currentState.entry();
		
	}

	@Override
	public void update(UpdateType type) {
		switch (type) {
		case GUI_STARTED : 
			currentState.handleGuiStarted();
			break;
			
		case WINDOW_RESIZE :
			currentState.handleWindowResize();
			break;
			
		case MAP_SELECTED :
			setMapSelected(true);
			currentState.handleMapSelected();
			break;
			
		case MAP_UNSELECTED :
			setMapSelected(false);
			currentState.handleMapUnselected();
			break;
			
		case WINDOW_FOCUS_CHANGED :
			toggleGuiFocused();
			currentState.handleWindowFocusChanged();
			break;
			
		case CLOSE :
			currentState.handleClose();
			break;
			
		default : 
			// nothing to do
		}
		currentState.changeState();
		System.out.println(currentState.toString());
	}
	
	/**
	 * Toggles guiHasFocus.
	 */
	private void toggleGuiFocused() {
		guiHasFocus = !guiHasFocus;
	}
		
	/**
	 * Sets the value of mapSelected.
	 * 
	 * @param selected
	 */
	private void setMapSelected(boolean selected) {
		mapSelected = selected;
	}
	
	/**
	 * This method positions the dialogue containing the map.
	 * 
	 * The dialogue will be displayed inside the border of the mapPane
	 * with insets of 5 pxls on every side.
	 */
	protected void positionDialogue() {
		Bounds border = mapPane.localToScreen(mapPane.getBoundsInLocal());
		mapApp.changeSize((int) border.getMinX() + 5, (int) border.getMinY() + 5, 
				(int) border.getWidth() - 5,(int) border.getHeight() - 5);
	}
	
	/**
	 * Toggles the visibility of the dialogue containing the map. 
	 *   
	 * @param visible 
	 */
	protected void showDialogue(boolean visible) {
		mapApp.setVisible(visible);
		mapApp.setAlwaysOnTop(visible);
	}
	
	/**
	 * Closes the map.
	 */
	protected void closeMap() {
		mapApp.closeMap();
	}
	
	/**
	 * Gets wether the map tab is selected.
	 * 
	 * @return
	 */
	protected boolean isMapSelected() {
		return mapSelected;
	}
	
	/**
	 * Gets wether GUI is focused or not.
	 * 
	 * @return
	 */
	protected boolean hasGuiFocus() {
		return guiHasFocus;
	}
	
	/**
	 * Gets wether map is focused or not.
	 * 
	 * @return
	 */
	protected boolean hasMapFocus() {
		return mapApp.hasFocus();
	}
	
	/**
	 * Sets the current state of this controller.
	 * 
	 * @param state
	 */
	protected void setState(FocusState state) {
		this.currentState = state;
	}
	
}
