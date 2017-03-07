package com.irs.main.controller;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.irs.main.R;
import com.irs.main.model.FoodDto;
import com.irs.main.model.UserModel;
import com.irs.yelp.SortType;

public class PreferencesController
        extends AppCompatActivity
        implements Runnable, android.location.LocationListener{

    private final FoodFinderController controller = new FoodFinderController();
    private final int LOCATION_REQUEST_CODE = 101;
    private final int MAX_TRIES = 2;
    private TextView maxRad;
    private FoodDto[] dbfm;
    private UserModel user = UserModel.getInstance();
    public LocationManager mLocationManager;
    protected static Location loc;

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

        askGPS();
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
        distanceBar(rad);
        toNextPage(cont);
        toDietPreferences(diet);
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
                        run();
                        // save preferences
                        UserModel.getInstance().saveToDatabaseAsync();
                        System.out.println("SAVED TO DB FROM PREFERENCES");

                        // switch to food finder screen
                        Intent i = new Intent(PreferencesController.this, FoodFinderController.class);
                        i.putExtra("model", dbfm);
                        startActivity(i);
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

    public void run() {
        dbfm = controller.getFoodFromServer();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void askGPS() {
        getLocation();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void getLocation() {
        int LOCATION_REFRESH_TIME = 1000;
        int LOCATION_REFRESH_DISTANCE = 5;

        int tries = 0;
        while(tries < MAX_TRIES) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                try {
                    mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    loc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, this);
                } catch (NullPointerException e) {
                    startActivity(new Intent(PreferencesController.this, Error.class));
                    e.printStackTrace();
                } finally {
                    break;
                }
            } else {
                ActivityCompat.requestPermissions(PreferencesController.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                tries++;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
                return ;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        loc = location;
    }

    @Override
    public void onProviderDisabled(String provider) {
        //Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        //Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //Log.d("Latitude", "status");
    }
}
