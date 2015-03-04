package gui.unfoldingIntegration;

import gui.GUIController;
import gui.GUIElement.UpdateType;

import java.awt.Frame;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JFrame;

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
public class IntegratedMapDialog extends JDialog {
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
    public IntegratedMapDialog(GUIController superController) {
    	this.superController = superController;
        setSize(800, 600);
        setTitle("Map");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setUndecorated(true);
        map = MyUnfoldingMap.getInstance(superController);
        add(map);
        map.init();
    }
    
    public void changeSize(int x, int y, int width, int height) {
    	this.setBounds(x, y, width, height);
    	map.setSize(width, height);
    }
    
    public void closeMap() {
    	map.exit();
    }
    
    public void toggleVisibility() {
    	setVisible(!isVisible());
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
    @SuppressWarnings("incomplete-switch")
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
                LocalDate test = buildLocalDate(r.getDate());
                // System.out.println("                                                                               "
                // + test);
                // Check if Tweet/Retweet-objct Date is in the needed interval
                if (inRange(start, end, test)) {

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
            calculatedData = superController.getDisplayValuePerCountry(forCalc,
                    1);

            keySet = calculatedData.keySet();
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

    /**
     * converts LocalDate to int numbers
     * 
     * @param input
     *            LocalDate
     * @return array with 0:Year 1:Month 3:Day null if invalid input
     */
    private int[] convertLocalDateToInt(LocalDate date) {
        if (date == null) {
            return null;
        }
        int year, month, day;
        year = date.getYear();
        month = date.getMonthValue();
        day = date.getDayOfMonth();
        int[] result = {year, month, day };
        return result;
    }

    /**
     * converts Date to int numbers
     * 
     * @param input
     *            LocalDate
     * @return array with 0:Year 1:Month 3:Day null if invalid input
     */
    private int[] converDateToInt(Date date) {
        if (date == null) {
            return null;
        }

        int year, month, day;
        String string = date.toString();
        String[] result = string.split("-");
        if (result.length != 3) {
            return null;
        }
        year = Integer.parseInt(result[0]);
        month = Integer.parseInt(result[1]);
        day = Integer.parseInt(result[2]);

        int[] intResult = {year, month, day };
        return intResult;
    }

    /**
     * decides whether date is in a given date range
     * 
     * @param rangeStart
     *            start of range
     * @param rangeEnd
     *            end of range
     * @param date
     *            date to test
     * @return true if date is in range (date == endpoint is also true), false
     *         otherwise
     */
    private boolean inRange(LocalDate rangeStart, LocalDate rangeEnd,
            LocalDate date) {
        if (rangeStart == null || rangeEnd == null || date == null) {
            return false;
        }
        // check intverall
        if (rangeEnd.isBefore(rangeStart)) {
            return false;
        }
        if (date.isBefore(rangeStart)) {
            return false;
        }
        if (date.isAfter(rangeEnd)) {
            return false;
        }
        return true;
    }

    /**
     * build LocalDate of given Date
     * 
     * @param date
     *            date in format Date
     * @return localDate of the same date or null if input was invalid
     */
    private LocalDate buildLocalDate(Date date) {
        int[] result = converDateToInt(date);
        if (result == null) {
            return null;
        }
        /*
         * Month month = ; switch (result[1]) { case 1: month = Month.JANUARY;
         * break; case 2: month = Month.FEBRUARY; break; case 3: month =
         * Month.MARCH; break; case 4: month = Month.APRIL; break; case 5: month
         * = Month.MAY; break; case 6: month = Month.JUNE; break; case 7: month
         * = Month.JULY; break; case 8: month = Month.AUGUST; break; case 9:
         * month = Month.SEPTEMBER; break; case 10: month = Month.OCTOBER;
         * break; case 11: month = Month.NOVEMBER; break; case 12: month =
         * Month.DECEMBER; break; default: return null; }
         */
        LocalDate locDate = LocalDate.of(result[0], result[1], result[2]);
        return locDate;
    }

}
