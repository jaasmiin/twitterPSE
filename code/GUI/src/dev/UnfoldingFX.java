package dev;

import javax.swing.JPanel;

import unfolding.MyUnfoldingMap;
import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class UnfoldingFX extends Application {
	private SwingNode mapSwingNode;
	
    @Override
    public void start(Stage stage) {
        mapSwingNode = new SwingNode();
        
        MyUnfoldingMap map = new MyUnfoldingMap();
		map.init();
		
		JPanel contentPane = new JPanel();
        contentPane.setSize(900, 600);
    	mapSwingNode.resize(contentPane.getWidth(), contentPane.getHeight());                
        mapSwingNode.setContent(contentPane);        
		mapSwingNode.getContent().add(map);

        StackPane pane = new StackPane();
        pane.getChildren().add(mapSwingNode);

        stage.setScene(new Scene(pane, contentPane.getWidth(), contentPane.getHeight()));
        stage.show();
        
        new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					mapSwingNode.getContent().repaint();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}