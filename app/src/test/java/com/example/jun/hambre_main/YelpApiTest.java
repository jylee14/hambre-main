package com.example.jun.hambre_main;

import com.example.jun.yelp.BusinessModel;
import com.example.jun.yelp.BusinessResponseModel;
import com.example.jun.yelp.YelpApi;
import com.example.jun.yelp.YelpUtilities;

import org.junit.Test;

import java.util.HashMap;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class YelpApiTest {

    @Test
    public void yelpUtilities_test() {
        YelpUtilities yelp = YelpUtilities.getInstance();
        HashMap<String, String> params = new HashMap<>();
        params.put("term", "food");
        params.put("latitude", "37.7670169511878");
        params.put("longitude", "-122.42184275");
        BusinessResponseModel businessResponse = yelp.getYelpApi().businessSearch(params);

        for (BusinessModel business : businessResponse.businesses()) {
            System.out.println(business.name());
        }
    }

    @Test
    public void categorySearch_test() {
        YelpApi api = YelpUtilities.getInstance().getYelpApi();

        // build params
        HashMap<String, String> params = new HashMap<>();
        params.put("latitude", "37.786882");
        params.put("longitude", "-122.399972");
        params.put("categories", "food");
        params.put("term", "japanese");

        BusinessResponseModel businessResponse = api.businessSearch(params);

        for (BusinessModel business : businessResponse.businesses()) {
            System.out.println(business.name());
        }
    }
}