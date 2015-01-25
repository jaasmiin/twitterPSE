package gui.standardMap;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.JFrame;
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
						JFrame jf = new JFrame();
				        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
						jf.setBounds(100, 100, 450, 300);
						
						PApplet map = new MyUnfoldingMap();
						map.init();
				        
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						JPanel contentPane = new JPanel();
						contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
						contentPane.setLayout(new BorderLayout(0, 0));
						
						jf.setContentPane(contentPane);
						
						mapSwingNode.setContent(contentPane);

				        StackPane pane = new StackPane();
				        pane.getChildren().add(mapSwingNode);
				        
				        mapSwingNode.resize(100, 200);
				        
				        jf.getContentPane().add(map);
				        
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