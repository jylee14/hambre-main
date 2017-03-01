package com.irs.main.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.irs.main.R;

public class DietRestriction extends AppCompatActivity {
    private RadioGroup prefs;
    private Button save;
    private Button cancel;

    static int index = -1; //default to no food preferences
    static final String[] categories = {"Vegetarian", "Vegan", "Kosher", "gluten_free"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_restriction);

        prefs = (RadioGroup) findViewById(R.id.choices);
        save = (Button) findViewById(R.id.save);
        cancel = (Button) findViewById(R.id.cancel);

        //set the preference to the preference of the user on the server
        setPreferences();

        setOnCheckedChangeListener();
        createSaveChangesOnClickListener();
        setCancelOnClickListener();
    }

    private void setCancelOnClickListener() {
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DietRestriction.this, "Changes discarded", Toast.LENGTH_SHORT).show();
                returnToPrev();
            }
        });
    }

    private void createSaveChangesOnClickListener() {
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if not guest mode
                //push update to server);
                //database.getUser(username)
                //username.updateDiet(newPref, prefIndex);
                Toast.makeText(DietRestriction.this, "Changes saved!", Toast.LENGTH_SHORT).show();
                returnToPrev();
                //else
                //propagate the data throughout the app via preferenceID
            }
        });
    }

    private void setOnCheckedChangeListener() {
        prefs.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.none:
                        index = -1;
                        break;
                    case R.id.veggie:
                        index = 0;
                        break;
                    case R.id.vegan:
                        index = 1;
                        break;
                    case R.id.kosher:
                        index = 2;
                        break;
                    case R.id.noGlu:
                        index = 3;
                        break;
                }
            }
        });
    }

    private void setPreferences() {
        switch (index) {
            case -1:
                prefs.check(R.id.none);
                break;
            case 0:
                prefs.check(R.id.veggie);
                break;
            case 1:
                prefs.check(R.id.vegan);
                break;
            case 2:
                prefs.check(R.id.kosher);
                break;
            case 3:
                prefs.check(R.id.noGlu);
                break;
            default:
                prefs.check(R.id.none);
                break;

        }
    }

    private void returnToPrev() {
        Intent i = new Intent(DietRestriction.this, PreferencesController.class);
        startActivity(i);
    }
}
