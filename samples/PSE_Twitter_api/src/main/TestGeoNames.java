package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.geonames.GeoNamesException;
import org.geonames.Toponym;
import org.geonames.ToponymSearchCriteria;
import org.geonames.ToponymSearchResult;
import org.geonames.WebService;

import twitter4j.GeoLocation;

public class TestGeoNames implements Runnable {

    private String loc;

    public TestGeoNames(String location) {
        loc = location;
    }

    public void run() {
        try {
            getCountryId(loc);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static String getCountryId(String location) throws Exception {

        String ret = null;
        if (location != null && location.length() > 0) {

            WebService.setGeoNamesServerFailover(null);
            WebService.setUserName("KIT_PSE");
            WebService.setConnectTimeOut(1000);

            ToponymSearchCriteria tsc = new ToponymSearchCriteria();
            // tsc.setFeatureCode(location);
            tsc.setLanguage("en");
            // tsc.setName(location);
            tsc.setQ(location);
            ToponymSearchResult result = WebService.search(tsc);

            HashMap<String, Integer> list = new HashMap<String, Integer>();
            for (Toponym t : result.getToponyms()) {
                if (list.containsKey(t.getCountryName())) {
                    int value = list.get(t.getCountryName());
                    value++;
                    list.put(t.getCountryName(), value);
                } else {
                    list.put(t.getCountryName(), 1);
                }
            }
            int max = 0;
            Set<Entry<String, Integer>> set = list.entrySet();
            Iterator<Entry<String, Integer>> it = set.iterator();
            while (it.hasNext()) {
                Entry<String, Integer> val = it.next();
                if (max < val.getValue()) {
                    max = val.getValue();
                    ret = val.getKey();
                }
            }
        }
        return ret;
    }

    public static String getCountryId(GeoLocation location) throws IOException,
            GeoNamesException {
        return WebService.countryCode(location.getLatitude(),
                location.getLongitude());
    }

}
