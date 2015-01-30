package unfolding;

/**
 * 
 */



import gui.GUIController;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import processing.core.PApplet;

/**
 * @author Lidia
 * 
 */
public class MyUnfoldingMap extends PApplet {

    /**
     * default serial version uid
     */
    private static final long serialVersionUID = 1L;
    private UnfoldingMap map1;
    //private UnfoldingMap map2;
    private UnfoldingMap currentMap;
    private HashMap<String, DataEntry> dataEntriesMap;
    private List<Marker> countryMarker;
    private List<String> setValues;

    public MyUnfoldingMap() {
    	super();
    	this.setSize(900, 600);
	}

    public void setup() {  //check size of map
        size(900, 600);
        smooth();
        
        map1 = new UnfoldingMap(this, P2D);
        //map2 = new UnfoldingMap(this, new Google.GoogleMapProvider());
        
        currentMap = map1;

        currentMap.zoomLevel(1);
        currentMap.setZoomRange(2, 4);
      
        MapUtils.createDefaultEventDispatcher(this, map1);
        
        List<Feature> countries = GeoJSONReader.loadData(this, "countries.geo.json");

        countryMarker = MapUtils.createSimpleMarkers(countries);

//        dataEntriesMap = loadCountriesFromCSV("countries.csv");
        setValues = new ArrayList<String>();

        loop();
    }
    public UnfoldingMap getMap() {
    	return currentMap;
    }
    
    public void draw() {
        //switchProvider();
        currentMap.draw();
    }

    /**
     * Shades countrys dependent on their relative frequency of tweets
     */
    public void shadeCountries() {
        for (Marker marker : countryMarker) {
            String countryId = marker.getId();

            DataEntry dataEntry = dataEntriesMap.get(countryId);

            if (dataEntry != null && dataEntry.getValue() != -1) {
                //Take value as brightness
                Double transparency = dataEntry.getValue();
                float transpa = Float.parseFloat(transparency.toString());

                marker.setColor(color(39, 190, 7, transpa));
                
                marker.setStrokeColor(color(73, 118, 41));
                marker.setStrokeWeight(2);
            } else {
                // value doesn't exist
                marker.setColor(color(100, 120));
            }

        }
    }

    /**
     * Updates new values to be visualized on the map.
     * 
     * @param changedEntries
     *            String array containing country id an new value of it
     */
    public void update(HashMap<String, Double> changedEntries) {

        if (!setValues.isEmpty()) {
            for (String id : setValues) {

                DataEntry edit = dataEntriesMap.get(id);
                edit.setValue((double) -1);
                dataEntriesMap.put(id, edit);
            }
            setValues.clear();
        }

        for(Entry<String, Double> e: changedEntries.entrySet()) {
            
            DataEntry newEntry = dataEntriesMap.get(e.getKey());
            newEntry.setValue(e.getValue());
            
            dataEntriesMap.put(e.getKey(), newEntry);
            setValues.add(e.getKey());
        }
        shadeCountries();
        redraw();
    }    
//    /**
//     * Switches provider of the map
//     * By pressing '1' an '2'
//     */
//    public void switchProvider() {
//        if(key == '1') {
//            currentMap = map1;
//        }
//        else if (key == '2') {
//            currentMap = map2;
//        }
//    }
    

    private HashMap<String, DataEntry> loadCountriesFromCSV(String file) {
        HashMap<String, DataEntry> dataEntriesMap = new HashMap<String, DataEntry>();

        String[] rows = loadStrings(file);
        for (String row : rows) {
            // Reads country name and countryID from CSV row
            String[] column = row.split(";");
            if (column.length >= 3) {
                DataEntry dataEntry = new DataEntry();
                dataEntry.setCountryName(column[0]);
                dataEntry.setCountryId3Chars(column[1]);
                dataEntry.setCountryId2Chars(column[2]);
                dataEntry.setValue((double) -1);
                dataEntriesMap.put(column[2], dataEntry);
            }
        }

        return dataEntriesMap;
    }
}
