package gui.standardMap;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import processing.core.PApplet;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import unfolding.MyUnfoldingMap;
import mysql.result.TweetsAndRetweets;
import gui.OutputElement;
import gui.RunnableParameter;

public class StandardMapController extends OutputElement implements Initializable {
    
    @FXML
    private SwingNode mapSwingNode;
    @FXML
    private StackPane pane;
    
    TweetsAndRetweets uneditedData;
    MyUnfoldingMap map;  
    MyUnfoldingMap map2 = new MyUnfoldingMap();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        superController.subscribe(this);
    	
    	
    	
        createThread(mapSwingNode);
        
    } 
    
    private void createThread(final SwingNode s) {
        s.resize(900, 600);
        pane.setMinSize(400, 400);
        map = new MyUnfoldingMap();
        map.setSize(900, 600);
        map.displayHeight = 600;
        map.displayWidth = 900;
        
        map.init();
       
        System.out.println();
        JLayeredPane contentPane = new JLayeredPane();

        contentPane.setSize(900, 600);
                  
                       
        try {
             Thread.sleep(1500);
        } catch (InterruptedException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
        }
                        
        contentPane.add(map);
        contentPane.moveToFront(map);
        s.setContent(contentPane);

        map.setVisible(true);
        map.loop();        
        map.redraw();
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