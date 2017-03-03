package com.irs.main.model;

import com.irs.yelp.BusinessModel;
import com.irs.yelp.BusinessResponseModel;
import com.irs.yelp.SortType;
import com.irs.yelp.YelpApi;

import java.util.HashMap;

/**
 * DataController class which accesses the Yelp API to get Restaurant Data in the form of the
 * Restaurant POJO
 */
public class RestaurantDataModel {

    private YelpApi api = YelpApi.getInstance();

    /**
     * Method to retrieve a list of restaurants based on a search query
     *
     * @param searchQuery query to enter into yelp API
     * @param count       number of restaurants to retrieve (max 50)
     * @param sortType    how to sort the response
     * @param openNow     retrieve only open restaurants
     * @return list of restaurants
     */
    public static BusinessModel[] getRestaurants(
            String location,
            String category,
            String query,
            SortType sortType,
            int radius,
            int count,
            boolean openNow) {

        BusinessModel[] restaurants = new BusinessModel[count];

        // Set the POST params based on method parameters
        HashMap<String, String> params = new HashMap<>();
        // TODO: get location from GPS
        params.put("location", "9450%20Gilman%20Dr.%20La%20Jolla%20CA%2092092");
        params.put("categories", ((category == null || category == "") ? "food" : category));
        params.put("term", query);
        params.put("sort_by", "" + sortType);
        params.put("radius", "" + radius);
        params.put("limit", "" + count);
        params.put("open_now", openNow ? "true" : "false");

        System.err.println("RUNNING RESTAURANT SEARCH");
        // get the response
        BusinessResponseModel response = YelpApi.getInstance().businessSearch(params);

        return response.businesses();
    }
}
