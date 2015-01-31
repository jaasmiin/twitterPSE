
package gui.standardMap;

import gui.GUIController;
import gui.GUIElement.UpdateType;

import java.util.HashMap;
import java.util.Set;

import javax.swing.JDialog;

import unfolding.MyDataEntry;
import unfolding.MyUnfoldingMap;
import mysql.result.TweetsAndRetweets;

@SuppressWarnings("serial")
public class StandardMapDialog extends JDialog {
	private GUIController superController;
	private TweetsAndRetweets uneditedData;
	private MyUnfoldingMap map;
	private HashMap<String, MyDataEntry> calculatedData;

	public StandardMapDialog(GUIController superController) {
		this.superController = superController;
		setSize(600, 400);
		setTitle("Map");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		map = new MyUnfoldingMap(superController);
		add(map);
		map.init();
		setVisible(true);
	}

	public void update(UpdateType type) {
		switch (type) {
		case CLOSE:
			if (map != null) {
				map.exit();
			}
			break;
		case TWEET:
			uneditedData = superController.getDataByLocation();
			HashMap<String, Integer> forCalc = new HashMap<String, Integer>();
			for (mysql.result.Retweets r : uneditedData.getRetweets()) {
				int counter = r.getCounter();
				String id = r.getLocationCode();
				forCalc.put(id, counter);
				
			}
			Set<String> keySet = forCalc.keySet();
			for (String key : keySet) {
				System.out.println(key + " - " + forCalc.get(key));
			}
			System.out.println("############################################################");
			calculatedData = superController.getDisplayValuePerCountry(forCalc,1);
			
		    keySet = calculatedData.keySet();
			for (String key : keySet) {
				System.out.println(key + " - " + calculatedData.get(key));
			}
			
			map.update(calculatedData);
			map.redraw();

			// TODO: repaint pane
			// TODO: Get calculated data from somewhere
			// map.update(); insert new data
			break;
		case GUI_STARTED:
		    map.resetMarkers();
			map.redraw();
			setVisible(true);
			break;
		
		default:
		    map.resetMarkers();
		}
	}

}

