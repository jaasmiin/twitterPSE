package gui.standardMap;



import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import javax.swing.JPanel;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import unfolding.MyUnfoldingMap;
import mysql.result.TweetsAndRetweets;
import gui.OutputElement;

public class StandardMapController extends OutputElement implements Initializable {

    @FXML
    private SwingNode mapSwingNode;
    
    TweetsAndRetweets uneditedData;
   
    MyUnfoldingMap map;  
    MyUnfoldingMap map2 = new MyUnfoldingMap();


    private HashMap<String, Double> calculatedData;

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
						insertMap();
					}
				});
			}
		}).start();
    }

    private void insertMap() {
        map = new MyUnfoldingMap();
		map.init();
		
		JPanel contentPane = new JPanel();
        contentPane.setSize(900, 600);
    	mapSwingNode.resize(contentPane.getWidth(), contentPane.getHeight());                
        mapSwingNode.setContent(contentPane);        
		mapSwingNode.getContent().add(map);
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) { // TODO: close this thread
					mapSwingNode.getContent().repaint();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
    }
    
        
 
   @Override
    public void update(UpdateType type) {
        /*if(type.equals(UpdateType.TWEET)) {
            uneditedData = superController.getDataByLocation();
            HashMap<String, Integer> forCalc = new HashMap<String, Integer>();
            for (mysql.result.Retweets r: uneditedData.retweets) {
                int counter = r.getCounter();
                String id = r.getLocationCode();
                forCalc.put(id, counter);
            }
            
            calculatedData = superController.getDisplayValuePerCountry(uneditedData, forCalc);
            
            //TODO: repaint pane
            //TODO: Get calculated data from somewhere
            //map.update();  insert new data
        }*/
    } 

}