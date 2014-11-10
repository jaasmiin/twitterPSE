package main;

import java.io.IOException;

import org.geonames.GeoNamesException;
import org.geonames.Toponym;
import org.geonames.ToponymSearchCriteria;
import org.geonames.ToponymSearchResult;
import org.geonames.WebService;

import twitter4j.GeoLocation;

public class TestGeoNames {

    public static String getCountryId(String location)
            throws Exception {

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

            for (Toponym t : result.getToponyms()) {
                return t.getCountryName();
            }
        }
        return null;
    }

    public static String getCountryId(GeoLocation location) throws IOException,
            GeoNamesException {
        return WebService.countryCode(location.getLatitude(),
                location.getLongitude());
    }

}
