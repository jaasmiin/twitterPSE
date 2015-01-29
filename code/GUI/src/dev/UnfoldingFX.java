package dev;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import unfolding.MyUnfoldingMap;
import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class UnfoldingFX extends Application {
	private SwingNode mapSwingNode = new SwingNode();
	private MyUnfoldingMap map = new MyUnfoldingMap();
    @Override
    public void start(Stage stage) {
		map.init();
		
		JPanel contentPane = new JPanel();
        contentPane.setSize(900, 600);
        contentPane.addMouseListener(new MouseListener() {
        	private int x = 0;
        	private int y = 0;
			
			@Override
			public void mouseReleased(MouseEvent e) {
				map.move(e.getX() - x, e.getY() - y);
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				x = e.getX();
				y = e.getY();
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println("Mouse clicked.");
			}
		});
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