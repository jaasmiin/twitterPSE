package gui.standardMap;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import javax.swing.JFrame;

import processing.core.PApplet;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.embed.swing.SwingNode;
import unfolding.MyUnfoldingMap;
import mysql.result.TweetsAndRetweets;
import gui.OutputElement;

public class StandardMapController extends OutputElement implements
        Initializable {

    @FXML
    private SwingNode mapSwingNode;

    private TweetsAndRetweets uneditedData;
    MyUnfoldingMap map;

    private HashMap<String, Double> calculatedData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
        superController.subscribe(this);

        addMapToPane();
    }

    private void addMapToPane() {
        mapSwingNode = new SwingNode();
        mapSwingNode.maxHeight(300);
        mapSwingNode.minWidth(300);
        map = new MyUnfoldingMap();

        final JFrame mapJFrame = new MyFrame();

        mapSwingNode.setContent(mapJFrame.getRootPane());

    }

    class MyFrame extends JFrame {
        /**
         * default serial version uid
         */
        private static final long serialVersionUID = 1L;

        public MyFrame() {
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
            HashMap<String, Integer> forCalc = new HashMap<String, Integer>();
            for (mysql.result.Retweets r: uneditedData.retweets) {
                int counter = r.getCounter();
                String id = r.getLocationCode();
                forCalc.put(id, counter);
            }
            
            calculatedData = superController.getDisplayValuePerCountry(uneditedData, forCalc);
            
            //TODO: Get calculated data from somewhere
            //map.update();  insert new data
        }
    }
}