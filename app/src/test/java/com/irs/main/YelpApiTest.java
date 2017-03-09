package com.irs.main;

import com.irs.yelp.BusinessDto;
import com.irs.yelp.BusinessResponseDto;
import com.irs.yelp.YelpApi;

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
        YelpApi api = YelpApi.getInstance();
        HashMap<String, String> params = new HashMap<>();
        params.put("term", "food");
        params.put("latitude", "37.7670169511878");
        params.put("longitude", "-122.42184275");
        params.put("radius_filter", "40000");
        BusinessResponseDto businessResponse = api.businessSearch(params);

        for (BusinessDto business : businessResponse.businesses()) {
            System.out.println(business.name());
        }
    }

    @Test
    public void categorySearch_test() {
        YelpApi api = YelpApi.getInstance();

        // build params
        HashMap<String, String> params = new HashMap<>();
        params.put("latitude", "32.874633");
        params.put("longitude", "-117.242336");
        params.put("categories", "food");
        params.put("sort_by", "rating");
        params.put("radius", 1600*10 + "");
        params.put("term", "japanese");

        BusinessResponseDto businessResponse = api.businessSearch(params);

        for (BusinessDto business : businessResponse.businesses()) {
            System.out.println(business.toString());
        }
    }
}