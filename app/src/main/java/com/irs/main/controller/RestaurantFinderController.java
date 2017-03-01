package com.irs.main.controller;

import com.irs.yelp.BusinessModel;
import com.irs.yelp.BusinessResponseModel;
import com.irs.yelp.YelpApi;
import java.util.HashMap;

public class RestaurantFinderController {
    public static BusinessModel[] findRestaurants(HashMap<String, String> param){
        YelpApi api = YelpApi.getInstance();
        BusinessResponseModel businessResponse = api.businessSearch(param);
        BusinessModel[] businesses = businessResponse.businesses();

        return businesses;
    }
}
