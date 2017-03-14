package com.irs.main.controller;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.irs.main.DietType;
import com.irs.main.R;
import com.irs.main.model.UserModel;

public class DietRestrictionController extends FragmentActivity {
    private RadioGroup prefs;
    private Button save;
    private Button cancel;

    //private DietType dietTemp = DietType.None;

    private final UserModel user = UserModel.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_restriction);

        prefs = (RadioGroup) findViewById(R.id.choices);
        save = (Button) findViewById(R.id.save);
        cancel = (Button) findViewById(R.id.cancel);

        if(LandingController.isGuest)
            prefs.check(R.id.none);
        else
            setPreferences();

        setOnCheckedChangeListener();
        createSaveChangesOnClickListener();
        setCancelOnClickListener();
    }

    private void setCancelOnClickListener() {
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DietRestrictionController.this, "Changes discarded", Toast.LENGTH_SHORT).show();
                returnToPrev();
            }
        });
    }

    private void createSaveChangesOnClickListener() {
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!LandingController.isGuest) {
                    UserModel.getInstance().saveToDatabaseAsync();
                    Toast.makeText(DietRestrictionController.this, "Changes saved!", Toast.LENGTH_SHORT).show();
                }
                returnToPrev();
            }
        });
    }

    private void setOnCheckedChangeListener() {
        prefs.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                DietType dietTemp = DietType.None;
                switch (checkedId) {
                    case R.id.none:
                        dietTemp = DietType.None;
                        break;
                    case R.id.veggie:
                        dietTemp = DietType.Vegetarian;
                        break;
                    case R.id.vegan:
                        dietTemp = DietType.Vegan;
                        break;
                    case R.id.kosher:
                        dietTemp = DietType.Kosher;
                        break;
                    case R.id.noGlu:
                        dietTemp = DietType.GlutenFree;
                        break;
                }
                if(!LandingController.isGuest)
                    user.setDietType(dietTemp);
            }
        });
    }

    private void setPreferences() {
        switch (user.getDietType()) {
            case None:
                prefs.check(R.id.none);
                break;
            case Vegetarian:
                prefs.check(R.id.veggie);
                break;
            case Vegan:
                prefs.check(R.id.vegan);
                break;
            case Kosher:
                prefs.check(R.id.kosher);
                break;
            case GlutenFree:
                prefs.check(R.id.noGlu);
                break;
            default:
                prefs.check(R.id.none);
                break;
        }
    }

    private void returnToPrev() {
        finish();
    }
}
