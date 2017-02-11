package com.example.jun.hambre_main;

import com.google.gson.Gson;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jun.yelp.BusinessModel;
import com.example.jun.yelp.BusinessResponseModel;
import com.example.jun.yelp.YelpApi;
import com.example.jun.yelp.YelpUtilities;

import java.util.HashMap;

/**
 * This class is a stub class to test the integration of Yelp API into the system
 * allow the user to input a cuisine type/city name to see the responses of yelp api
 */

public class yelpStub extends AppCompatActivity {
    private Location location;
    private EditText cuisine;
    private Button show;
    private TextView first;
    private TextView second;
    private TextView third;
    private TextView fourth;

    private BusinessModel business1;
    private LocationManager locationManager;
    private String provider;
    private Activity myself;

    private int LOCATION_ACCESS_VALUE = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        myself = this;
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);

        setContentView(R.layout.yelpstub);

        cuisine = (EditText) findViewById(R.id.culture);
        show = (Button) findViewById(R.id.show);
        first = (TextView) findViewById(R.id.first);
        second = (TextView) findViewById(R.id.second);
        third = (TextView) findViewById(R.id.third);
        fourth = (TextView) findViewById(R.id.fourth);

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String culture = cuisine.getText().toString();
                if (culture.equals(""))
                    Toast.makeText(yelpStub.this, "enter cuisine culture", Toast.LENGTH_SHORT).show();
                else {
                    YelpApi api = new YelpApi();

                    if (ActivityCompat.checkSelfPermission(myself, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(myself, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        GPS_Enabled();
                    }
                    Location location = locationManager.getLastKnownLocation(provider);
                    // build params
                    HashMap<String, String> params = new HashMap<>();
                    params.put("latitude", String.valueOf(location.getLatitude()));
                    params.put("longitude", String.valueOf(location.getLongitude()));
                    params.put("categories", "food");
                    params.put("term", culture);
                    params.put("radius_filter", "40000");

                    BusinessResponseModel businessResponse = api.businessSearch(params);
                    try {
                        business1 = (businessResponse.businesses())[0];
                        first.setText(business1.name());
                        BusinessModel business2 = (businessResponse.businesses())[1];
                        second.setText(business2.name());
                        BusinessModel business3 = (businessResponse.businesses())[2];
                        third.setText(business3.name());
                        BusinessModel business4 = (businessResponse.businesses())[3];
                        fourth.setText(business4.name());
                    } catch (Exception e) {
                        first.setText("something went wrong");
                        e.printStackTrace();
                    }
                }
            }
        });

        first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String coordinates = business1.coordinates().toString();
                String label = business1.name();
                String uriBegin = "geo:" + coordinates;
                String query = coordinates + "(" + label + ")";
                String encodedQuery = Uri.encode(query);
                String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                Uri uri = Uri.parse(uriString);
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    private void GPS_Enabled() {
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // check if enabled and if not send user to the GSP settings
        // Better solution would be to display a dialog and suggesting to
        // go to the settings
        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    }
}
