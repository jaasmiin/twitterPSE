package gui.standardMap;



import java.net.URL;
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
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		map.redraw();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		mapSwingNode.getContent().repaint();
}
    
        
 
   @Override
    public void update(UpdateType type) {
       /* if(type.equals(UpdateType.TWEET)) {
            uneditedData = superController.getDataByLocation();
            HashMap<String, Integer> forCalc = new HashMap<String, Integer>();
            for (mysql.result.Retweets r: uneditedData.retweets) {
                int counter = r.getCounter();
                String id = r.getLocationCode();
                forCalc.put(id, counter);
            }
            
            calculatedData = superController.getDisplayValuePerCountry(uneditedData, forCalc);
            
            //TODO: Get calculated data from somewhere
            //map.update();  insert new data
        } */
    } 

}