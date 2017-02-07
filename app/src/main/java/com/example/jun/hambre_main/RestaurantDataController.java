package com.example.jun.hambre_main;

/**
 * DataController class which accesses the Yelp API to get Restaurant Data in the form of the
 * RestaurantModel POJO
 */
public class RestaurantDataController {

    /**
     * code called to initiate database connection, needs to be called before
     * other method calls are made
     */
    public void connect() {
        // logon here
    }

    /**
     * Method to retrieve a list of restaurants based on a search query
     * @param searchQuery query to enter into yelp API
     * @param count number of restaurants to retrieve
     * @return list of restaurants
     */
    public RestaurantModel[] getRestaurants(String searchQuery, int count) {
        // code to get a users data, maybe auth needs to be done here, or
        // should be done beforehand?
        return new RestaurantModel[0];
    }

    // I'm not sure in what other ways we can interact with yelp data, but if we can handle more
    // specific cases than just a query, like search list of tags, search filtered, etc.
    // we should add those in here as methods for abstraction/layering
}
