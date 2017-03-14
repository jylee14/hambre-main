package com.irs.main.controller;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.irs.main.R;
import com.irs.main.model.FBGoogLoginModel;
import com.irs.main.model.UserModel;
import com.irs.yelp.SortType;

public class PreferencesController extends FragmentActivity {
    private TextView maxRad;
    private final UserModel user = UserModel.getInstance();
    private FBGoogLoginModel loginModel;
    private SeekBar rad;
    private RadioGroup pref;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        loginModel = FBGoogLoginModel.getInstance();

        Button diet = (Button) findViewById(R.id.DPref);
        rad = (SeekBar) findViewById(R.id.radius);
        maxRad = (TextView) findViewById(R.id.currMax);
        pref = (RadioGroup) findViewById(R.id.sorting);
        RadioButton rate = (RadioButton) findViewById(R.id.rate);
        RadioButton dist = (RadioButton) findViewById(R.id.dist);
        Button cont = (Button) findViewById(R.id.cont);

        if(LandingController.isGuest) {
            rad.setProgress(1);
            maxRad.setText("1 mi");
            pref.check(R.id.rate);
        }else{
            pref.check((UserModel.getInstance().getSortType() == SortType.distance) ? R.id.dist : R.id.rate);
            rad.setProgress(user.getMaxDist());
            maxRad.setText("" + user.getMaxDist() + " mi");
        }

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
        Button logout = (Button) findViewById(R.id.button_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    private void logout() {
        if (loginModel.isLoggedIn()) {
            new AlertDialog.Builder(this).setTitle("Logging out...")
                    .setMessage("Do you want to save your preferences before logging out?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // save preferences
                            UserModel.getInstance().saveToDatabaseAsync();
                            logoutCommon();
                        }

                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            logoutCommon();
                        }
                    })
                    .show();
        } else {
            Toast.makeText(this, "You're not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void logoutCommon(){
        if (loginModel.isLoggedIntoGoogle())
            PreferencesController.this.logoutOfGoogle();
        else
            logoutOfFacebook();

        loginModel.loggedOut();
    }

    private void logoutOfFacebook() {
        if (loginModel.isLoggedIntoFacebook()) {
            LoginManager.getInstance().logOut();
            logoutToLanding();
        }
    }

    private void logoutToLanding() {
        user.setFirstPref(false);
        Intent intent = new Intent(PreferencesController.this, LandingController.class);
        startActivity(intent);
        finish();
    }

    private void logoutOfGoogle() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if (loginModel.isLoggedIntoGoogle()) {
            final GoogleApiClient googleApiClient = loginModel.getGoogleApiClient();
            googleApiClient.connect();
            googleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(@Nullable Bundle bundle) {
                    if (googleApiClient.isConnected()) {
                        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                logoutToLanding();
                            }
                        });
                    }
                }

                @Override
                public void onConnectionSuspended(int i) {
                    //Log.d(TAG, "Google API Client Connection Suspended");
                }
            });
        }
    }

    private void toDietPreferences(Button diet) {
        /**
         * Diet Button will redirect the user to the dietary restrictions page
         */
        diet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PreferencesController.this, DietRestrictionController.class);
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
                        if(!LandingController.isGuest) {
                            // save preferences
                            UserModel.getInstance().saveToDatabaseAsync();
                            System.out.println("SAVED TO DB FROM PREFERENCES");
                        }
                        // switch to food finder screen
                        if (!user.firstPrefSet()) {
                            user.setFirstPref(true);
                            Intent returnIntent = new Intent(PreferencesController.this, FoodFinderController.class);
                            startActivity(returnIntent);
                        } else if (user.getChangedPrefs()) {
                            Intent returnIntent = new Intent(PreferencesController.this, FoodFinderController.class);
                            setResult(244, returnIntent);
                            user.setChangedPrefs(false);
                            finish();
                        } else {
                            finish();
                        }
                    } catch (Exception e) {
                        startActivity(new Intent(PreferencesController.this, ErrorController.class));
                        finish();
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        System.exit(0);
    }
}