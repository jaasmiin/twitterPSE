package unfolding;

import gui.GUIController;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import unfolding.MyDataEntry;
import processing.core.PApplet;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.utils.MapUtils;

/**
 * Class of a unfolding map that is used to visualize Tweets und Retweets spread
 * 
 * @author Lidia
 * 
 */
public final class MyUnfoldingMap extends PApplet {

    private static final double CONST = 0.00000000001;
    /**
     * default serial version uid
     */
    private static final long serialVersionUID = 1L;

    private static final MyUnfoldingMap SINGLETON = new MyUnfoldingMap();
    private static GUIController superController;

    private UnfoldingMap map1;
    private UnfoldingMap map2;
    private UnfoldingMap currentMap;
    private HashMap<String, MyDataEntry> dataEntriesMap;
    private List<Marker> countryMarker;

    //private AtomicInteger oldKey = new AtomicInteger();
    /**
     * List of countryIds which are colored in the map
     */
    private List<String> setValues;

    /**
     * HashMap to match between 2 char and 3 char country id key --> 3 char
     * value --> 2 char
     */
    private HashMap<String, String> countryIdTrans;

    /**
     * Maximal value of the displayed entries
     */
    private float maxValue = 0;

    private MyUnfoldingMap() {
        super();
        this.setSize(900, 600);
    }

    /**
     * Returns Singleton.
     * 
     * @param controller
     *            GuiController
     * @return Instance of MyUnfoldingMap
     */
    public static MyUnfoldingMap getInstance(GUIController controller) {
        if (MyUnfoldingMap.superController == null) {
            MyUnfoldingMap.superController = controller;
        }
        return SINGLETON;
    }

    /**
     * Sets GUIController
     * 
     * @param controller
     *            GUIController
     */
    public void setController(GUIController controller) {
        superController = controller;
    }

    @Override
    public void setup() { // check size of map
        size(900, 600);
        smooth();

        map1 = new UnfoldingMap(this, P2D);
        map2 = new UnfoldingMap(this, new Google.GoogleMapProvider());

        currentMap = map1;

        map1.zoomLevel(0);
        map1.setZoomRange(2, 4);
        map1.setBackgroundColor(140);

        map2.zoomLevel(0);
        map2.setZoomRange(2, 4);
        map2.setBackgroundColor(140);

        MapUtils.createDefaultEventDispatcher(this, map1, map2);

        // Load country polygons
        List<Feature> countries = GeoJSONReader.loadData(this,
                "countries.geo.json");
        countryMarker = MapUtils.createSimpleMarkers(countries);
        map1.addMarkers(countryMarker);
        map2.addMarkers(countryMarker);
        resetMarkers();

        dataEntriesMap = loadCountriesFromCSV("countries.csv");
        setValues = new ArrayList<String>();

        /*
         * //TEST HashMap<String, Double> test = new HashMap<String, Double>();
         * test.put("AR", 7.45974); test.put("US", 19.395875); test.put("DE",
         * 4.3595); update(test);
         */

    }

    /**
     * Returns the current map
     * 
     * @return current map
     */
    public UnfoldingMap getMap() {
        return currentMap;
    }

    @Override
    public void draw() {
        switchProvider();
        currentMap.draw();
    }

    /**
     * called when a mouse click is noticed
     * 
     * tries to get the country clicked on and calls handler
     * 
     * @param e
     *            the event object (not needed, parameter specified by
     *            Interface)
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        float x = mouseX;
        float y = mouseY;

        Marker country = currentMap.getFirstHitMarker(x, y);
        if (country != null) {
            String countryId = country.getId();
            if (countryId == null)
                return;

            // 3 char Ländercode in 2 char umwandeln
            String char2Code = countryIdTrans.get(countryId);
            if (char2Code == null)
                return;

            MyDataEntry location = dataEntriesMap.get(char2Code);
            if (location == null)
                return;

            superController.setMapDetailInformation(location);
        }
    }

    private int getColor(float p) {
        float red = ((Double) (p < 50 ? 255.0 : 256 - (p - 50) * 5.12))
                .floatValue();
        float green = ((Double) (p > 50 ? 255 : p * 5.12)).floatValue();
        return color(red, green, 0, 100);
    }

    /**
     * Shades countrys dependent on their relative frequency of tweets
     */
    public void shadeCountries() {
        if (countryMarker != null) {
            for (Marker m : countryMarker) {
                String countryId = m.getId();
                String id = countryIdTrans.get(countryId);

                MyDataEntry dataEntry = dataEntriesMap.get(id);

                // Strokes
                m.setStrokeColor(color(241, 241, 241, 50));
                m.setStrokeWeight(1);

                if (dataEntry != null
                        && (dataEntry.getValue() >= CONST || dataEntry
                                .getValue() <= -CONST)) {
                    // Take value as brightness

                    // Double transparency = dataEntry.getValue();
                    // float transpa =
                    // Float.parseFloat(transparency.toString());
                    float transpa = dataEntry.getValue().floatValue();
                    int t = getColor(map(transpa, 0, maxValue, 0, 100));
                    System.out.println(m.getId() + "   transpa " + transpa
                            + "    transpa convertiert "
                            + map(transpa, 0, maxValue, 0, 100));
                    System.out.println();
                    if (currentMap == map1) {
                        m.setColor(t);
                    }
                    if (currentMap == map2) {
                        m.setColor(t);
                    }

                } else {
                    // value doesn't exist
                    m.setColor(color(173, 173, 173, 50));
                }

            }
        }
    }

    /**
     * Updates new values to be visualized on the map.
     * 
     * @param changedEntries
     *            HashMap containing country name, display value and other data
     *            for hover effect
     */
    public void update(HashMap<String, MyDataEntry> changedEntries) {
        resetMarkers();
        maxValue = 0;
        if (setValues != null && !setValues.isEmpty()) {
            for (int i = 0; i < setValues.size(); i++) {
                String id = setValues.get(i);
                MyDataEntry edit = dataEntriesMap.get(id);

                if (edit != null) {
                    edit.setValue(0);
                    edit.setRetweetsLandFiltered(0);
                    edit.setRetweetsLand(666);
                    dataEntriesMap.put(id, edit);
                }
            }
            setValues.clear();
        }
        String maxCountry = "";
        for (Entry<String, MyDataEntry> e : changedEntries.entrySet()) {
            if (!e.getKey().equals("0")) {
                MyDataEntry newEntry = dataEntriesMap.get(e.getKey());
                if (newEntry != null) {
                    System.out.println("first transfer  "
                            + e.getValue().getValue());
                    newEntry.setValue(e.getValue().getValue());
                    newEntry.setRetweetsLand(e.getValue().getRetweetsLand());
                    newEntry.setRetweetsLandFiltered(e.getValue()
                            .getRetweetsLandFiltered());
                    dataEntriesMap.put(e.getKey(), newEntry);
                    setValues.add(e.getKey());
                }

                float currentValue = e.getValue().getValue().floatValue();
                System.out.println("second transfer  " + currentValue);
                if (currentValue > maxValue) {
                    maxValue = currentValue;
                    maxCountry = e.getValue().getCountryId3Chars();
                    System.out.println("maxValue: " + maxValue + " "
                            + maxCountry);
                }
            }
        }

        shadeCountries();
    }

    /**
     * Switches provider of the map By pressing '1' an '2'
     */
    public void switchProvider() {
        if (key == '1') {
            currentMap = map1;
            shadeCountries();
        } else if (key == '2') {
            currentMap = map2;
            shadeCountries();
        }
        key = '\0';
    }

    /**
     * Loads 2 and 3 char countryIds in HashMap for further use
     * 
     * @param file
     *            countryIds as .csv
     * @return HashMap containing countryIds
     */
    private HashMap<String, MyDataEntry> loadCountriesFromCSV(String file) {
        dataEntriesMap = new HashMap<String, MyDataEntry>();
        countryIdTrans = new HashMap<String, String>();

        String[] rows = loadStrings(file);
        for (String row : rows) {
            // Reads country name and countryID from CSV row
            String[] column = row.split(";");
            if (column.length >= 3) {
                MyDataEntry dataEntry = new MyDataEntry();
                dataEntry.setCountryName(column[0]);
                dataEntry.setCountryId3Chars(column[1]);
                dataEntry.setCountryId2Chars(column[2]);
                dataEntriesMap.put(column[2], dataEntry);

                countryIdTrans.put(column[1], column[2]);
            }
        }

        return dataEntriesMap;
    }

    /**
     * Resets all colored markers
     */
    private void resetMarkers() {
        if (countryMarker != null) {
            for (Marker m : countryMarker) {
                m.setColor(color(173, 173, 173, 50));
                m.setStrokeColor(color(241, 241, 241, 50));
                m.setStrokeWeight(1);
            }
        }
    }

}
