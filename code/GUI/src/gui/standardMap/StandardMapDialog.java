package gui.standardMap;

import gui.GUIController;
import gui.GUIElement.UpdateType;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Set;

import javax.swing.JDialog;

import unfolding.MyDataEntry;
import unfolding.MyUnfoldingMap;
import mysql.result.TweetsAndRetweets;

/**
 * Sets up the Dialog window containing the map
 * 
 * @author Lidia
 * 
 */
@SuppressWarnings("serial")
public class StandardMapDialog extends JDialog {
    private GUIController superController;
    private TweetsAndRetweets uneditedData;
    private MyUnfoldingMap map;
    private HashMap<String, MyDataEntry> calculatedData;

    /**
     * Constructor.
     * 
     * @param superController
     *            GUIController
     */
    public StandardMapDialog(GUIController superController) {
        this.superController = superController;
        setSize(600, 400);
        setTitle("Map");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        map = MyUnfoldingMap.getInstance(superController);
        add(map);
        map.init();
    }

    /**
     * updates the map
     * 
     * @param type
     *            type of the update event
     * @param start
     *            start date of range of the query
     * @param end
     *            end date of range of the query
     */
    public void update(UpdateType type, LocalDate start, LocalDate end) {
        switch (type) {
        case CLOSE:
            if (map != null) {
                map.exit();
            }
            break;
        case TWEET_BY_DATE:
            // System.out.println("LocalDate: " + start + "   -   " + end);
            // aggregate relevant data and check if dates are valid
            // System.out.println();
            if (start == null) {
                start = LocalDate.MIN;
            }
            if (end == null) {
                end = LocalDate.MAX;
            }

            uneditedData = superController.getDataByLocationAndDate();
            HashMap<String, Integer> forCalc = new HashMap<String, Integer>();
            
            for (mysql.result.Retweets r : uneditedData.getRetweets()) {
                // convert date
                LocalDate test = Dates.buildLocalDate(r.getDate());
                // System.out.println("                                                                               "
                // + test);
                // Check if Tweet/Retweet-objct Date is in the needed interval
                if (Dates.inRange(start, end, test)) {

                    // Check if counter for location is already in the hashMap
                    if (forCalc.containsKey(r.getLocationCode())) {
                        int count = forCalc.get(r.getLocationCode());
                        count += r.getCounter();
                        forCalc.put(r.getLocationCode(), count);
                    } else {
                        int counter = r.getCounter();
                        String id = r.getLocationCode();
                        forCalc.put(id, counter);
                    }
                }
            }
            
            Set<String> keySet = forCalc.keySet();
            // for (String key : keySet) {
            // System.out.println(key + " - " + forCalc.get(key));
            // }
            // System.out.println("############################################################");
            System.out.println("start: " + forCalc.size() + "   keySet" + keySet.size());
            calculatedData = superController.getDisplayValuePerCountry(forCalc,
                    1);
            System.out.println("end");
            
            // for (String key : keySet) {
            // System.out.println(key + " - "
            // + calculatedData.get(key).getRetweetsLandFiltered());
            // }
            
            map.update(calculatedData);
            map.redraw();

            break;
        case GUI_STARTED:
            map.update(new HashMap<String, MyDataEntry>());
            map.redraw();
            setVisible(true);
            break;
        default:
        }
    }

  

}
