package main;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;

import processing.core.PApplet;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.utils.MapUtils;

/**
 * Visualizes population density of the world as a choropleth map. Countries are
 * shaded in proportion to the population density.
 * 
 * It loads the country shapes from a GeoJSON file via a data reader, and loads
 * the population density values from another CSV file (provided by the World
 * Bank). The data value is encoded to transparency via a simplistic linear
 * mapping.
 */
public class MapTest extends PApplet {

    private static String path = "I:\\Studium\\PSE";
    private static final long serialVersionUID = 1L;
    UnfoldingMap map;
    HashMap<String, DataEntry> dataEntriesMap;
    List<Marker> countryMarkers;

    public void setup() {
        size(800, 600, OPENGL);
        smooth();
        map = new UnfoldingMap(this, 50, 50, 700, 500, new Google.GoogleTerrainProvider());
        map.zoomToLevel(2);
        map.setBackgroundColor(240);
        MapUtils.createDefaultEventDispatcher(this, map);
        // Load country polygons and adds them as markers
        List<Feature> countries = GeoJSONReader
                .loadData(
                        this,
                        path + "\\unfolding_app_template_with_examples_0.9.6\\data\\data\\countries.geo.json");
        countryMarkers = MapUtils.createSimpleMarkers(countries);
        map.addMarkers(countryMarkers);
        // Load population data
        dataEntriesMap = loadPopulationDensityFromCSV(path  + "\\unfolding_app_template_with_examples_0.9.6\\data\\data\\countries-population-density.csv");
        // Country markers are shaded according to its population density (only
        // once)
        shadeCountries();
    }

    public void draw() {
        background(240);
        // Draw map tiles and country markers
        map.draw();
        addPoint(new twitter4j.GeoLocation(48.756093, 9.138247));
    }

    public void shadeCountries() {
        for (Marker marker : countryMarkers) {
            // Find data for country of the current marker
            String countryId = marker.getId();
            DataEntry dataEntry = dataEntriesMap.get(countryId);
            if (dataEntry != null && dataEntry.value != null) {
                // Encode value as brightness (values range: 0-1000)
                float transparency = map(dataEntry.value, 0, 700, 10, 255);
                marker.setColor(color(255, 0, 0, transparency));
            } else {
                // No value available
                marker.setColor(color(100, 120));
            }
        }
    }

    public HashMap<String, DataEntry> loadPopulationDensityFromCSV(
            String fileName) {
        HashMap<String, DataEntry> dataEntriesMap = new HashMap<String, DataEntry>();
        String[] rows = loadStrings(fileName);
        for (String row : rows) {
            // Reads country name and population density value from CSV row
            String[] columns = row.split(";");
            if (columns.length >= 3) {
                DataEntry dataEntry = new DataEntry();
                dataEntry.countryName = columns[0];
                dataEntry.id = columns[1];
                dataEntry.value = Float.parseFloat(columns[2]);
                dataEntriesMap.put(dataEntry.id, dataEntry);
            }
        }
        return dataEntriesMap;
    }

    public void addPoint(twitter4j.GeoLocation loc) {
        Location location = new Location(loc.getLatitude(), loc.getLongitude());
        // Create point markers for locations
        SimplePointMarker marker = new SimplePointMarker(location);
       marker.setColor(color(255,0,0,100));
       marker.setStrokeColor(color(255,0,0));

        // Add markers to the map
        map.addMarkers(marker);
    }

    class DataEntry {
        String countryName;
        String id;
        Integer year;
        Float value;
    }
}
