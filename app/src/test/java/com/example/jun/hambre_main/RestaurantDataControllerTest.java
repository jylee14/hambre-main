package com.example.jun.hambre_main;

import com.example.jun.yelp.SortType;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * RestaurantDataController class tests
 */
public class RestaurantDataControllerTest {

    @Test
    public void getRestaurants_test() {
        RestaurantDataController dataController = new RestaurantDataController();
        RestaurantModel[] restaurants = dataController.getRestaurants("food", 20, SortType.best_match, true);

        for (RestaurantModel restaurant : restaurants) {
            System.out.println(restaurant != null ? restaurant.getName() : "Restaurant Not Found");
        }
    }
}