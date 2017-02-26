package com.example.jun.hambre_main.controller;

import com.example.jun.hambre_main.model.RestaurantModel;
import com.example.jun.yelp.BusinessModel;
import com.example.jun.yelp.BusinessResponseModel;
import com.example.jun.yelp.SortType;
import com.example.jun.yelp.YelpApi;

import java.util.HashMap;

/**
 * DataController class which accesses the Yelp API to get Restaurant Data in the form of the
 * RestaurantModel POJO
 */
public class RestaurantDataController {

    private YelpApi api = YelpApi.getInstance();

    /**
     * Constructor for data controller
     */
    public void RestaurantDataController() {
        // maybe check that the api is working properly here?
    }

    /**
     * Method to retrieve a list of restaurants based on a search query
     * @param searchQuery query to enter into yelp API
     * @param count number of restaurants to retrieve (max 50)
     * @param sortType how to sort the response
     * @param openNow retrieve only open restaurants
     * @return list of restaurants
     */
    public RestaurantModel[] getRestaurants(String searchQuery, int count, SortType sortType, boolean openNow) {

        RestaurantModel[] restaurants = new RestaurantModel[count];

        // Set the POST params based on method parameters
        HashMap<String, String> params = new HashMap<>();
        params.put("term", searchQuery);
        params.put("categories", "food");
        params.put("latitude", "37.7670169511878");
        params.put("longitude", "-122.42184275");
        //params.put("location", "1536 E Washington Blvd, Pasadena, CA");
        params.put("limit", count + "");
        params.put("sort_by", sortType.name());
        params.put("open_now", openNow ? "true" : "false");

        System.err.println("RUNNING RESTAURANT SEARCH");
        // get the response
        BusinessResponseModel response = api.businessSearch(params);

        // loop through converting to Restaurant objects
        int index = 0;
        for (BusinessModel business : response.businesses()) {
            if (business.name() == null) {
                System.err.println("Problem");
            }
            restaurants[index] = new RestaurantModel(
                                        business.name(),
                                        business.url(),
                                        business.image_url(),
                                        business.price() == null ? 0 : business.price().length());
            index++;
        }

        return restaurants;
    }

    // I'm not sure in what other ways we can interact with yelp data, but if we can handle more
    // specific cases than just a query, like search list of tags, search filtered, etc.
    // we should add those in here as methods for abstraction/layering
}
