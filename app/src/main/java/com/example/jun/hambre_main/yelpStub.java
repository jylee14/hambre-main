package com.example.jun.hambre_main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jun.yelp.BusinessModel;
import com.example.jun.yelp.BusinessResponseModel;
import com.example.jun.yelp.YelpApi;

import java.util.HashMap;

/**
 * This class is a stub class to test the integration of Yelp API into the system
 * allow the user to input a cuisine type/city name to see the responses of yelp api
 */

public class yelpStub extends AppCompatActivity{
    private EditText cuisine;
    private Button show;
    private TextView first;
    private TextView second;
    private TextView third;
    private TextView fourth;

    private BusinessModel business1;
    private BusinessModel business2;
    private BusinessModel business3;
    private BusinessModel business4;

    private Bundle bundle;  //from preferences page
    private int sort = 2;   //default is rating
    private int rad = 1600; //min is 1 mile

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.yelpstub);

        cuisine = (EditText)findViewById(R.id.culture);
        show = (Button)findViewById(R.id.show);
        first = (TextView)findViewById(R.id.first);
        second = (TextView)findViewById(R.id.second);
        third = (TextView)findViewById(R.id.third);
        fourth = (TextView)findViewById(R.id.fourth);

        bundle = getIntent().getExtras();
        sort = bundle.getInt("sort");
        rad = bundle.getInt("radius");

        show.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String culture = cuisine.getText().toString();
                if(culture == "")
                    Toast.makeText(yelpStub.this, "enter cuisine culture", Toast.LENGTH_SHORT).show();
                else {
                    YelpApi api = YelpApi.getInstance();

                    // build params
                    HashMap<String, String> params = new HashMap<>();
                    params.put("location", "9450 Gilman Dr. La Jolla CA, 92092");
                    params.put("categories", "food");
                    params.put("term", culture);
                    params.put("sort", "" + Preferences.byRating);
                    params.put("radius_filter", "" + rad);

                    BusinessResponseModel businessResponse = api.businessSearch(params);
                    try{
                        business1 = (businessResponse.businesses())[0];
                        first.setText(business1.name() + "\t" + business1.price() + "\t" + business1.rating());

                        business2 = (businessResponse.businesses())[1];
                        second.setText(business2.name() + "\t" + business2.price() + "\t" + business2.rating());

                        business3 = (businessResponse.businesses())[2];
                        third.setText(business3.name() + "\t" + business3.price() + "\t" + business3.rating());

                        business4 = (businessResponse.businesses())[3];
                        fourth.setText(business4.name() + "\t" + business4.price() + "\t" + business4.rating());
                    }catch(Exception e){
                        first.setText("something went wrong");
                        e.printStackTrace();
                    }
                }
            }
        });

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
