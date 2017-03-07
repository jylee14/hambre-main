package com.irs.main.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.irs.main.R;
import com.irs.main.model.FBGoogLoginModel;

/**
 * Created by paulosliu on 3/7/17.
 */

public class LauncherController extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_launcher);
        FoodFinderController.updateFoodArray();


        if (FBGoogLoginModel.loggedIn()) {
            startActivity(new Intent(LauncherController.this, FoodFinderController.class));
        } else {
            startActivity(new Intent(LauncherController.this, LandingController.class));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
