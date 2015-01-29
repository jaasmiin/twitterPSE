package dev;

import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JDialog;
import javax.swing.JPanel;

import processing.core.PApplet;
import unfolding.MyUnfoldingMap;
import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class UnfoldingFX extends Application {
	private SwingNode mapSwingNode = new SwingNode();
	private MyUnfoldingMap map = new MyUnfoldingMap();
    @Override
    public void start(Stage stage) {
//		Frame frame = new Frame("Map");
//		frame.setSize(600, 400);
//		frame.add(map);
		JDialog dialog = new JDialog();
		dialog.setSize(600, 400);
		dialog.add(map);
		map.init();
		dialog.setVisible(true);
//		
//		JPanel contentPane = new JPanel();
//        contentPane.setSize(900, 600);
//        contentPane.addMouseListener(new MouseListener() {
//        	private int x = 0;
//        	private int y = 0;
//			
//			@Override
//			public void mouseReleased(MouseEvent e) {
//				map.mouseReleased(e);
//			}
//			
//			@Override
//			public void mousePressed(MouseEvent e) {
//				x = e.getX();
//				y = e.getY();
//				map.mousePressed(e);
//			}
//			
//			@Override
//			public void mouseExited(MouseEvent e) {
//				
//				
//			}
//			
//			@Override
//			public void mouseEntered(MouseEvent e) {
//				map.mouseEntered();
//				
//			}
//			
//			@Override
//			public void mouseClicked(MouseEvent e) {
//				map.mouseClicked(e);
//				if (e.getClickCount() == 2)
//					map.getMap().zoomIn();
//			}
//		});
//    	mapSwingNode.resize(contentPane.getWidth(), contentPane.getHeight());                
//        mapSwingNode.setContent(contentPane);        
//		mapSwingNode.getContent().add(map);
//
//        StackPane pane = new StackPane();
//        pane.getChildren().add(mapSwingNode);
//
//        stage.setScene(new Scene(pane, contentPane.getWidth(), contentPane.getHeight()));
//        stage.show();
//        stage.getScene().getWindow().setOnCloseRequest(new EventHandler<WindowEvent>() {
//			@Override
//			public void handle(WindowEvent event) {
//				map.exit();
//			}
//		});
//        map.getMap().draw();
//        map.redraw();
//        mapSwingNode.getContent().repaint();
//        new Thread(new Runnable() {
//			@Override
//			public void run() {
////				while (true) {
//					
//					try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					mapSwingNode.getContent().repaint();
////				}
//			}
//		}).start();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}