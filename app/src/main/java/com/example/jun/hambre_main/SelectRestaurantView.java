package com.example.jun.hambre_main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.jun.yelp.BusinessModel;
import com.example.jun.yelp.BusinessResponseModel;
import com.example.jun.yelp.YelpApi;

import java.util.ArrayList;
import java.util.HashMap;

public class SelectRestaurantView extends AppCompatActivity {

    private TextView first;
    private TextView second;
    private TextView third;
    private TextView fourth;

    private BusinessModel business1;
    private BusinessModel business2;
    private BusinessModel business3;
    private BusinessModel business4;
    //TODO implement and display listView of restaurants

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //animate Activity transition
        overridePendingTransition(R.anim.animation_activity_enter,
                R.anim.animation_activity_leave);

        setContentView(R.layout.activity_select_restaurant);

        first = (TextView)findViewById(R.id.first);
        second = (TextView)findViewById(R.id.second);
        third = (TextView)findViewById(R.id.third);
        fourth = (TextView)findViewById(R.id.fourth);

        HashMap<String, String> params = new HashMap<>();
        ArrayList<String> param = getIntent().getStringArrayListExtra("param");

        try {
            YelpApi api = YelpApi.getInstance();

            params.put(param.get(0), param.get(1));
            params.put(params.get(2), params.get(3));
            params.put(param.get(4), param.get(5));
            params.put(params.get(6), params.get(7));
            params.put(param.get(8), param.get(9));
            params.put(param.get(9), param.get(10));
            if(DietRestriction.index >= 0)
                params.put("category_filter", DietRestriction.categories[DietRestriction.index]);

            BusinessResponseModel businessResponse = api.businessSearch(params);

            if(businessResponse != null) {

                business1 = ((businessResponse.businesses())[0]);
                first.setText(business1.name() + "     " + business1.price() + "     " + business1.rating());

                business2 = ((businessResponse.businesses())[1]);
                second.setText(business2.name() + "     " + business2.price() + "     " + business2.rating());

                business3 = ((businessResponse.businesses())[2]);
                third.setText(business3.name() + "     "+ business3.price() + "     " + business3.rating());

                business4 = ((businessResponse.businesses())[3]);
                fourth.setText(business4.name() + "     " + business4.price() + "     " + business4.rating());
            }else{
                first.setText("MY LEGS");
            }
        }catch (Exception e){
            e.printStackTrace();
            first.setText("He's dead Jim");
        }


        first.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String coordinates = business1.coordinates().toString();
                String label = business1.name();
                openMaps(coordinates, label);
            }
        });

        second.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String coordinates = business2.coordinates().toString();
                String label = business2.name();
                openMaps(coordinates, label);
            }
        });

        third.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String coordinates = business3.coordinates().toString();
                String label = business3.name();
                openMaps(coordinates, label);
            }
        });

        fourth.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String coordinates = business4.coordinates().toString();
                String label = business4.name();
                openMaps(coordinates, label);
            }
        });
    }

    private void openMaps(String coordinates, String label){
        String uriBegin = "geo:" + coordinates;
        String query = coordinates + "(" + label + ")";
        String encodedQuery = Uri.encode(query);
        String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
        Uri uri = Uri.parse(uriString);
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
