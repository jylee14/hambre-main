package com.irs.main.controller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.irs.main.R;
import com.irs.main.model.UserModel;
import com.irs.yelp.BusinessDto;
import com.irs.yelp.CoordinatesDto;

import java.lang.reflect.Array;
import java.util.ArrayList;

//TODO check into adding a distance and if they are open or closed currently
public class SelectRestaurantController extends FragmentActivity implements Runnable {
    private ListView list;
    private BusinessDto[] businesses;
    private String[] names;
    private String[] url;
    private String[] imageUrl;
    private double[] ratings;
    private String[] prices;
    private CoordinatesDto[] coordinates;
    private double[] distances;

    private final UserModel user = UserModel.getInstance();

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //animate Activity transition
        overridePendingTransition(R.anim.animation_activity_enter,
                R.anim.animation_activity_leave);

        setContentView(R.layout.activity_select_restaurant);

        try {
            run();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("He's dead Jim");
        }

    }

    public void run() {
        businesses = BusinessDto.toBusinessModel(getIntent().getParcelableArrayExtra("model"));
        businesses = trimArray(businesses);

        System.out.println(businesses.length);

        names = new String[businesses.length];
        url = new String[businesses.length];
        imageUrl = new String[businesses.length];
        ratings = new double[businesses.length];
        prices = new String[businesses.length];
        coordinates = new CoordinatesDto[businesses.length];
        distances = new double[businesses.length];

        for (int i = 0; i < businesses.length; i++) {
            names[i] = businesses[i].name();
            url[i] = businesses[i].url();
            imageUrl[i] = businesses[i].image_url();
            ratings[i] = businesses[i].rating();
            prices[i] = businesses[i].price();
            coordinates[i] = businesses[i].coordinates();
            distances[i] = businesses[i].distance();
        }

        RestaurantListController adapter = new
                RestaurantListController(SelectRestaurantController.this,
                names, url, imageUrl, ratings, prices, coordinates, distances);

        list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);

        //open yelp page of selected restaurant in browser
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(url[position]));
                startActivity(browserIntent);
            }
        });
    }

    private BusinessDto[] trimArray(BusinessDto[] businesses){
        System.out.println("Removing restaurants that are too far away");
        System.out.println("User's max distance: " + user.getMaxDist());
        ArrayList<BusinessDto> nearby = new ArrayList<>();

        for(int i = 0; i < businesses.length; i++){
            System.out.println(businesses[i].distance());
            double restaurantDistance = businesses[i].distance()/1609.344;

            if(restaurantDistance < user.getMaxDist()){
                nearby.add(businesses[i]);
            }
        }

        nearby.trimToSize();
        return nearby.toArray(new BusinessDto[0]);
    }
}
