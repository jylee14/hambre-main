package com.example.jun.hambre_main;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.jun.yelp.BusinessModel;
import com.example.jun.yelp.BusinessResponseModel;

import static com.example.jun.hambre_main.R.id.first;
import static com.example.jun.hambre_main.R.id.fourth;
import static com.example.jun.hambre_main.R.id.second;
import static com.example.jun.hambre_main.R.id.third;

/**
 * Created by jeff on 2/14/17.
 */

public class SelectRestaurantController extends AppCompatActivity {

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



        setContentView(R.layout.activity_select_restaurant);

        first = (TextView)findViewById(R.id.first);
        second = (TextView)findViewById(R.id.second);
        third = (TextView)findViewById(R.id.third);
        fourth = (TextView)findViewById(R.id.fourth);

        Intent i = getIntent();
        BusinessResponseModel businessResponse =
                (BusinessResponseModel) i.getSerializableExtra("businessResponseObject");
        try {
            business1 = (businessResponse.businesses())[0];
            first.setText(business1.name() + "\t" + business1.price() + "\t" + business1.rating());

            business2 = (businessResponse.businesses())[1];
            second.setText(business2.name() + "\t" + business2.price() + "\t" + business2.rating());

            business3 = (businessResponse.businesses())[2];
            third.setText(business3.name() + "\t" + business3.price() + "\t" + business3.rating());

            business4 = (businessResponse.businesses())[3];
            fourth.setText(business4.name() + "\t" + business4.price() + "\t" + business4.rating());
        } catch (Exception e) {
            first.setText("something went wrong");
            e.printStackTrace();
        }
    }
}
