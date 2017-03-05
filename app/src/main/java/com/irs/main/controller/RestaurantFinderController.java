package com.irs.main.controller;

import com.irs.yelp.BusinessDto;
import com.irs.yelp.BusinessResponseDto;
import com.irs.yelp.YelpApi;

import java.util.HashMap;

class RestaurantFinderController {
    public static BusinessDto[] findRestaurants(HashMap<String, String> param) {
        YelpApi api = YelpApi.getInstance();
        BusinessResponseDto businessResponse = api.businessSearch(param);

        return businessResponse.businesses();
    }
}
