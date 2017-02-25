package com.example.jun.hambre_main.controller;

import com.example.jun.hambre_main.view.DietRestrictionView;
import com.example.jun.yelp.BusinessModel;
import com.example.jun.yelp.BusinessResponseModel;
import com.example.jun.yelp.YelpApi;
import java.util.HashMap;

public class RestaurantFinderController {
    public static BusinessModel[] findRestaurants(HashMap<String, String> param){
        YelpApi api = YelpApi.getInstance();
        BusinessResponseModel businessResponse = api.businessSearch(param);
        BusinessModel[] businesses = businessResponse.businesses();

        return businesses;
    }
}
