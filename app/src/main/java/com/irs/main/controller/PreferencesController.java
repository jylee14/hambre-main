package com.irs.main.controller;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.irs.main.R;
import com.irs.main.model.FBGoogLoginModel;
import com.irs.main.model.UserModel;
import com.irs.yelp.SortType;

public class PreferencesController extends FragmentActivity{
    private TextView maxRad;
    private UserModel user = UserModel.getInstance();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        Button diet = (Button) findViewById(R.id.DPref);
        SeekBar rad = (SeekBar) findViewById(R.id.radius);
        maxRad = (TextView) findViewById(R.id.currMax);
        RadioGroup pref = (RadioGroup) findViewById(R.id.sorting);
        RadioButton rate = (RadioButton) findViewById(R.id.rate);
        RadioButton dist = (RadioButton) findViewById(R.id.dist);
        Button cont = (Button) findViewById(R.id.cont);

        pref.check((UserModel.getInstance().getSortType() == SortType.distance) ? R.id.dist : R.id.rate);

        rad.setProgress(user.getMaxDist());
        maxRad.setText("" + user.getMaxDist() + " mi");

        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setSortType(SortType.rating);
            }
        });
        dist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setSortType(SortType.distance);
            }
        });
        setLogout();
        distanceBar(rad);
        toNextPage(cont);
        toDietPreferences(diet);
    }

    private void setLogout() {
        // TODO: 3/8/17 add confirmation dialogue
        Button logout = (Button)findViewById(R.id.button_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FBGoogLoginModel.logout();
                startActivity(new Intent(PreferencesController.this, LandingController.class));
            }
        });
    }

    private void toDietPreferences(Button diet) {
        /**
         * Diet Button will redirect the user to the dietary restrictions page
         */
        diet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PreferencesController.this, DietRestriction.class);
                Bundle bundle = getIntent().getExtras();
                if (bundle != null) i.putExtras(bundle);
                startActivity(i);
            }
        });
    }

    /**
     * move the user onto next page
     */
    private void toNextPage(Button cont) {
        cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getMaxDist() == 0) {
                    Toast.makeText(PreferencesController.this, "Radius cannot be 0 miles", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        // save preferences
                        UserModel.getInstance().saveToDatabaseAsync();
                        System.out.println("SAVED TO DB FROM PREFERENCES");

                        // switch to food finder screen
                        startActivity(new Intent(PreferencesController.this, FoodFinderController.class));
                    } catch (Exception e) {
                        startActivity(new Intent(PreferencesController.this, Error.class));
                    }
                }
            }
        });
    }

    /**
     * change the preference of the user.
     * Need to find a way to propagate the value here throughout the app
     */
    private void distanceBar(SeekBar rad) {
        rad.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                user.setMaxDist(seekBar.getProgress());
                maxRad.setText(user.getMaxDist() + " mi");
            }
        });
    }
}