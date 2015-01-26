package gui.standardMap;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        superController.subscribe(this);
    	new Thread(new Runnable() {
			@Override
			public void run() {
		        Platform.runLater(new Runnable() {
					@Override
					public void run() {
						map = new MyUnfoldingMap();
						
						
						JLayeredPane contentPane = new JLayeredPane();
//						contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
//						contentPane.setLayout(new BorderLayout(0, 0));
						contentPane.setSize(100,200);
										        
				        mapSwingNode.resize(100, 200);
				        
				        //mapSwingNode.getContent().add(map, BorderLayout.CENTER);
				        map.init();
				        try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				        
				        contentPane.add(map);
				        mapSwingNode.setContent(contentPane);
				        map.resize(400, 400);

				        contentPane.setSize(400, 400);
				        //map.setSize(400, 400);
					}
				});
		       
			}
		}).start();
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