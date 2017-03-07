package com.irs.main.model;

import com.irs.yelp.BusinessDTO;
import com.irs.yelp.BusinessResponseDTO;

import android.location.Location;
import com.irs.yelp.SortType;
import com.irs.yelp.YelpApi;

import java.util.HashMap;

/**
 * DataController class which accesses the Yelp API to get Restaurant Data in the form of the
 * Restaurant POJO
 */
public class RestaurantDataModel {
    private YelpApi api = YelpApi.getInstance();
    private static double latitude, longitude;

    /**
     * Method to retrieve a list of restaurants based on a search query
     *
     * @param count       number of restaurants to retrieve (max 50)
     * @param sortType    how to sort the response
     * @param openNow     retrieve only open restaurants
     * @return list of restaurants
     */
    public static BusinessDTO[] getRestaurants(
            String location,
            String category,
            String query,
            SortType sortType,
            int radius,
            int count,
            boolean openNow) {

        BusinessDTO[] restaurants = new BusinessDTO[count];

        // Set the POST params based on method parameters
        HashMap<String, String> params = new HashMap<>();

        params.put("latitude", "" + latitude);
        params.put("longitude", "" + longitude);
        params.put("categories", ((category == null || category == "") ? "food" : category));
        params.put("term", query);
        params.put("sort_by", "" + sortType);
        params.put("radius", "" + radius);
        params.put("limit", "" + count);
        params.put("open_now", openNow ? "true" : "false");

        System.err.println("Lat: " + latitude + "\tLong: "+ longitude);
        System.err.println("RUNNING RESTAURANT SEARCH");
        // get the response
        BusinessResponseDTO response = YelpApi.getInstance().businessSearch(params);

        return response.businesses();
    }

    public static void setLatitude(double userLat){
        latitude = userLat;
    }
    public static void setLongitude(double userLong){
        longitude = userLong;
    }
}
