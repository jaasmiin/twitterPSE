package gui.standardMap;

import java.awt.BorderLayout;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

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
    private StackPane pane;

    TweetsAndRetweets uneditedData;
    MyUnfoldingMap map;  
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        superController.subscribe(this);
        
        map = new MyUnfoldingMap();
        
        final JPanel mapJPanel = new JPanel(new BorderLayout());
        mapJPanel.add(map);
        
        final SwingNode mapSwingNode = new SwingNode();
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                mapSwingNode.setContent(mapJPanel);
            }
        });
        
        pane = new StackPane();
        pane.getChildren().add(mapSwingNode);
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
