package com.irs.main.controller;

import com.irs.yelp.BusinessDTO;
import com.irs.yelp.BusinessResponseDTO;
import com.irs.yelp.YelpApi;

import java.util.HashMap;

class RestaurantFinderController {
    public static BusinessDTO[] findRestaurants(HashMap<String, String> param) {
        YelpApi api = YelpApi.getInstance();
        BusinessResponseDTO businessResponse = api.businessSearch(param);

        return businessResponse.businesses();
    }
}
