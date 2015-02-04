package application;

import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.JFrame;

import unfolding.examples.ChoroplethMapApp;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;

public class MapController implements Initializable {

    @FXML
    private Tab mapTab;

    @FXML
    private Tab notMap;

    @FXML
    private AnchorPane mapAnchorPane;
    
    private ChoroplethMapApp mapApp;
    
    private JFrame frame;
    
    private ObservableValue<Bounds> positioning;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		addSelectionHandler();
		
		mapApp = new ChoroplethMapApp();
		mapApp.init();
		
		frame = new JFrame();
		frame.getContentPane().add(mapApp);
		frame.setUndecorated(true);
		frame.setSize(400, 300);
		
		frame.setVisible(false);
		frame.setAlwaysOnTop(true);
		frame.setLocation(50, 50);
		
		
	}
	
	public void positionFrame() {
		Bounds border = mapAnchorPane.localToScreen(mapAnchorPane.getBoundsInLocal());
		// TODO: try to pack border into observableValue and add change listener so map grows 
		frame.setBounds((int) border.getMinX(),(int) border.getMinY(),
				(int) border.getWidth(),(int) border.getHeight());
					
		System.out.println("Xpos = " + border.getMinX() + ", YPos = " + border.getMinY());
	}
	
	public void closeMap() {
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		mapApp.exit();
	}
	
    
    private void addSelectionHandler() {
    	mapTab.setOnSelectionChanged(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				System.out.println("Selection changed");
				if (mapTab.isSelected()) {
					frame.setVisible(true);
				} else {
					frame.setVisible(false);
				}				
			}
    		
    	});
    }

}
