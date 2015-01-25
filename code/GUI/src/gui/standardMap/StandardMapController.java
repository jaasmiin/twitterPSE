package gui.standardMap;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import processing.core.PApplet;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.embed.swing.SwingNode;
import unfolding.MyUnfoldingMap;
import mysql.result.TweetsAndRetweets;
import gui.OutputElement;

public class StandardMapController extends OutputElement implements Initializable {
    
    @FXML
    private SwingNode mapSwingNode;

    TweetsAndRetweets uneditedData;
    MyUnfoldingMap map;  
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        superController.subscribe(this);
        
        addMapToPane();
    }
    
    private void addMapToPane() {
        mapSwingNode = new SwingNode();
        mapSwingNode.maxHeight(300);
        mapSwingNode.minWidth(300);
        map = new MyUnfoldingMap();
        
        final JPanel mapJPanel = new JPanel();
        mapJPanel.setLayout(new BorderLayout());
        mapJPanel.add(map, BorderLayout.CENTER);
    
        mapSwingNode.setContent(mapJPanel);
        
        
    }
    
	@Override
	public void update(UpdateType type) {
		if(type.equals(UpdateType.TWEET)) {
		    uneditedData = superController.getDataByLocation();
		    //TODO: Get calculated data from somewhere
		    //map.update();  insert new data
		}
	}
}