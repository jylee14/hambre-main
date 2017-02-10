package com.example.jun.hambre_main;

import com.example.jun.yelp.YelpUtilities;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class RestaurantDataControllerTest {

    @Test
    public void getRestaurants_test() {
        RestaurantDataController dataController = new RestaurantDataController();
        RestaurantModel[] restaurants = dataController.getRestaurants("mexican", 20);

        for (RestaurantModel restaurant : restaurants) {
            System.out.println(restaurant.getName());
        }
    }

    @Test
    public void yelpUtilities_test() {
        YelpUtilities yelp = YelpUtilities.getInstance();
    }
}