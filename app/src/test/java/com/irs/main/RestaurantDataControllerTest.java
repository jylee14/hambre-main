package com.irs.main;

import com.irs.main.model.RestaurantDataModel;
import com.irs.yelp.SortType;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * RestaurantDataModel class tests
 */
/**
public class RestaurantDataControllerTest {

    @Test
    public void getRestaurants_test() {
        RestaurantDataModel dataController = new RestaurantDataModel();
        Restaurant[] restaurants = dataController.getRestaurants("food", 20, SortType.best_match, true);

        for (Restaurant restaurant : restaurants) {
            System.out.println(restaurant != null ? restaurant.getName() : "Restaurant Not Found");
        }
    }
}
 */