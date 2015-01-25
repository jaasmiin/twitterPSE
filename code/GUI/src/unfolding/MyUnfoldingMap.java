package unfolding;
/**
 * 
 */

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;

/**
 * @author Lidia
 *
 */
public class MyUnfoldingMap extends PApplet {
    
    private UnfoldingMap map1;
    private UnfoldingMap map2;
    private UnfoldingMap currentMap;
    private HashMap<String, DataEntry> dataEntriesMap;
    private List<Marker> countryMarker;
    private List<String> setValues;

    
    public void setup() {  //check size of map 
        size(900, 600);
        
        map1 = new UnfoldingMap(this);
        map2 = new UnfoldingMap(this, new Google.GoogleMapProvider());
        
        currentMap = map1;
        
        currentMap.zoomLevel(1);
        currentMap.setZoomRange(2, 4);
        MapUtils.createDefaultEventDispatcher(this, map1, map2);
        
        List<Feature> countries = GeoJSONReader.loadData(this, "countries.geo.json");
        countryMarker = MapUtils.createSimpleMarkers(countries);
        
        dataEntriesMap = loadCountriesFromCSV("countries.csv");
        setValues = new ArrayList<String>();

        noLoop();   //Möglicherweise löschen!!!
    }
    
    public void draw() {
        switchProvider();
        currentMap.draw();
    }

    /**
     * Shades countrys dependent on their relative frequency of tweets
     */
    public void shadeCountries() {
        for (Marker marker: countryMarker) {
            String countryId = marker.getId();
            DataEntry dataEntry = dataEntriesMap.get(Integer.parseInt(countryId));
            
            if (dataEntry != null && dataEntry.getValue() != -1) {
                //Take value as brightness
                float transparency = map(dataEntry.getValue(), 0, 100, 10, 255);
                marker.setColor(color(39, 190, 7, transparency));
                marker.setStrokeColor(color(73, 118, 41));
                marker.setStrokeWeight(2);
            } else {
                //value doesn't exist 
                marker.setColor(color(100, 120));
            }
            
        }
    }
    
    /**
     * Updates new values to be visualized on the map.
     * @param changedEntries String array containing country id an new value of it
     */
    public void update(String[][] changedEntries) {
        //Reset all entries to '-1' 
        if(!setValues.isEmpty()) {
            for(String id: setValues) {
                DataEntry edit = dataEntriesMap.get(id);
                edit.setValue(-1);
                dataEntriesMap.put(id, edit);
            }
            setValues.clear();
        }
        
        for(int i = 0; i < changedEntries.length; i++) {
            String id = changedEntries[i][0];
            float newValue = Float.parseFloat(changedEntries[i][1]);
            DataEntry newEntry = dataEntriesMap.get(id);
            newEntry.setValue(newValue);
            dataEntriesMap.put(id, newEntry);
            setValues.add(id);
        }
        shadeCountries();
    }
    
    /**
     * Switches provider of the map
     * By pressing '1' an '2'
     */
    public void switchProvider() {
        if(key == '1') {
            currentMap = map1;
        }
        else if (key == '2') {
            currentMap = map2;
        }
    }
    
    private HashMap<String, DataEntry> loadCountriesFromCSV(String file) {
        HashMap<String, DataEntry> dataEntriesMap = new HashMap<String, DataEntry>();

        String[] rows = loadStrings(file);
        for (String row : rows) {
            // Reads country name and countryID from CSV row
            String[] column = row.split(";");
            if (column.length >= 3) {
                DataEntry dataEntry = new DataEntry();
                dataEntry.setCountryName(column[0]);
                dataEntry.setCountryId(column[1]);
                dataEntry.setValue(-1);
                dataEntriesMap.put(column[1], dataEntry);
            }
        }

        return dataEntriesMap;
    }
    
}
