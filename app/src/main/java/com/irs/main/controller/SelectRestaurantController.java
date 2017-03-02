package com.irs.main.controller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.irs.main.R;
import com.irs.yelp.BusinessModel;

public class SelectRestaurantController extends AppCompatActivity implements Runnable {

    private TextView first;
    private TextView second;
    private TextView third;
    private TextView fourth;

    private BusinessModel business1;
    private BusinessModel business2;
    private BusinessModel business3;
    private BusinessModel business4;

    ListView list;
    //BusinessModel[] restaurants;

    //TODO implement and display listView of restaurants

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //animate Activity transition
        overridePendingTransition(R.anim.animation_activity_enter,
                R.anim.animation_activity_leave);

        setContentView(R.layout.activity_select_restaurant);

        //RestaurantList adapter = new
          //      RestaurantList(SelectRestaurantController.this, )
        /*
        first = (TextView) findViewById(R.id.first);
        second = (TextView) findViewById(R.id.second);
        third = (TextView) findViewById(R.id.third);
        fourth = (TextView) findViewById(R.id.fourth); */

        try {
            run();
        } catch (Exception e) {
            e.printStackTrace();
            first.setText("He's dead Jim");
        }

        /*
        first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String coordinates = business1.coordinates().toString();
                String label = business1.name();
                openMaps(coordinates, label);
            }
        });

        second.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String coordinates = business2.coordinates().toString();
                String label = business2.name();
                openMaps(coordinates, label);
            }
        });

        third.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String coordinates = business3.coordinates().toString();
                String label = business3.name();
                openMaps(coordinates, label);
            }
        });

        fourth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String coordinates = business4.coordinates().toString();
                String label = business4.name();
                openMaps(coordinates, label);
            }
        });

        */
    }

    private void openMaps(String coordinates, String label) {
        String uriBegin = "geo:" + coordinates;
        String query = coordinates + "(" + label + ")";
        String encodedQuery = Uri.encode(query);
        String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
        Uri uri = Uri.parse(uriString);
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void run() {
        BusinessModel[] businesses = BusinessModel.toBusinessModel(getIntent().getParcelableArrayExtra("model"));
        String [] names = new String[businesses.length];
        String [] url = new String[businesses.length];
        String [] imageUrl = new String[businesses.length];
        double [] ratings = new double[businesses.length];
        String [] prices = new String[businesses.length];
        for(int i = 0; i < businesses.length; i++){
            names[i] = businesses[i].name();
            url[i] = businesses[i].url();
            imageUrl[i] = businesses[i].image_url();
            ratings[i] = businesses[i].rating();
            prices[i] = businesses[i].price();
        }
        RestaurantList adapter = new
                RestaurantList(SelectRestaurantController.this,
                names, url, imageUrl, ratings, prices);
        list = (ListView)findViewById(R.id.list);
        list.setAdapter(adapter);
        
        /*
        if (businesses != null) {
            business1 = (businesses[0]);
            first.setText(business1.name() + "     " + business1.price() + "     " + business1.rating());

            business2 = (businesses[1]);
            second.setText(business2.name() + "     " + business2.price() + "     " + business2.rating());

            business3 = (businesses[2]);
            third.setText(business3.name() + "     " + business3.price() + "     " + business3.rating());

            business4 = (businesses[3]);
            fourth.setText(business4.name() + "     " + business4.price() + "     " + business4.rating());
        } else {
            first.setText("MY LEGS");
            startActivity(new Intent(SelectRestaurantController.this, Error.class));
        }

        */
    }
}
