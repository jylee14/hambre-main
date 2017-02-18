package com.example.jun.hambre_main;

import com.example.jun.server.DBFoodModel;
import com.example.jun.server.ServerApi;
import com.example.jun.yelp.BusinessModel;
import com.example.jun.yelp.BusinessResponseModel;
import com.example.jun.yelp.YelpApi;

import org.junit.Test;

import java.util.HashMap;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ServerApiTest {

    @Test
    public void getFood_test() {
        ServerApi api = ServerApi.getInstance();

        DBFoodModel[] foodModels = api.getFood();

        for (DBFoodModel model: foodModels) {
            System.out.println(model.name());
        }
    }
}