package gui.standardMap;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import processing.core.PApplet;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
        mapSwingNode.minHeight(300);
        mapSwingNode.maxHeight(300);
        mapSwingNode.minWidth(300);
        map = new MyUnfoldingMap();
        
        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setBounds(100, 100, 450, 300);
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		jf.setContentPane(contentPane);
		PApplet map = new MyUnfoldingMap();
		jf.getContentPane().add(map);
		map.init();
		
//        jf.setLayout(new FlowLayout());
//        PApplet map = new MyUnfoldingMap();
//        jf.add(map);
//        jf.setPreferredSize(new Dimension(200, 200));
//        System.out.println("abc");
//        jf.getRootPane().setSize(200, 200);
//        map.init();
//        mapJFrame.getRootPane().setSize(300, 300);
        
        mapSwingNode.setContent(jf.getRootPane());
        
        
    }
    
    class MyFrame extends JFrame{
        public MyFrame(){
            super("Embedded UnfoldingMap");
            setLayout(new BorderLayout());
            PApplet map = new MyUnfoldingMap();
            add(map, BorderLayout.CENTER);
            setPreferredSize(new Dimension(200, 200));
            map.init();
        }
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