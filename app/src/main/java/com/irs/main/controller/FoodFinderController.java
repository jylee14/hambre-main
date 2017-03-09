package com.irs.main.controller;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.irs.main.R;
import com.irs.main.model.FoodDto;
import com.irs.main.model.OnSwipeTouchListener;
import com.irs.main.model.RestaurantDataModel;
import com.irs.main.model.UserModel;
import com.irs.server.DBFoodDto;
import com.irs.server.ServerApi;
import com.irs.yelp.BusinessDto;
import com.irs.yelp.YelpApi;

import com.squareup.picasso.Picasso;

import java.lang.*;

public class FoodFinderController extends FragmentActivity implements android.location.LocationListener {
    private final int LIMIT = 20;
    private final int MAX_TRIES = 2;
    private final int METERS_PER_MILE = 1600;
    private final int LOCATION_REQUEST_CODE = 101;
    private final String server = "http://159.203.246.214/irs/";
    private final Context context = this;

    private ImageView mainView;
    private Button uploadButton;
    private Button settingsButton;
    private int index = 0;

    private YelpApi api;
    private Location loc;
    private Bundle bundle;
    private String culture;
    private FoodDto[] gallery;
    private Animation animEnter, animLeave;
    private UserModel user = UserModel.getInstance();


    private static FoodDto[] dbfm = new FoodDto[20];

    private final Thread getFoodThread = new Thread() {
        public void run() {
            System.err.println("Running bg food thread: " + android.os.Process.myTid());
            dbfm = getFoodFromServer();
            System.arraycopy(dbfm, 0, gallery, 0, gallery.length);
        }
    };

    @Override
    public void onBackPressed() {
        System.gc();
        System.exit(0);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_food_finder);

        gallery = getFoodFromServer();
        initButtons();
        swipeAnimation();
        askGPS();
    }

    private void initButtons() {
        uploadButton = (Button) findViewById(R.id.btn_upload);
        uploadButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FoodFinderController.this,
                        UploadPhotoController.class);
                startActivity(i);
            }
        });

        settingsButton = (Button) findViewById(R.id.btn_settings);
        settingsButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FoodFinderController.this,
                        PreferencesController.class);
                startActivity(i);
            }
        });
    }

    private class LoadRestaurantsTask extends AsyncTask<FoodDto, Integer, BusinessDto[]> {
        @SafeVarargs
        @Override
        protected final BusinessDto[] doInBackground(FoodDto... params) {
            System.out.println("LOADING RESTAURANTS IN BACKGROUND");
            BusinessDto[] response = null;
            FoodDto food = params[0];
            try {
                System.out.println(culture);
                // TODO: set gps based location in first param
                response = RestaurantDataModel.getRestaurants(
                        food.getTag(), food.getCulture(),
                        UserModel.getInstance().getSortType(),
                        UserModel.getInstance().getMaxDist() * METERS_PER_MILE,
                        LIMIT, false);
                System.out.println("Response: " + response[0].name());
            } catch (Exception e) {
                System.out.println("MAYBE I WASN'T TRYING HARD ENOUGH");
                e.printStackTrace();
            }
            System.out.println("finished loading restaurants");
            return response;
        }

        @Override
        protected void onPostExecute(BusinessDto[] result) {
            System.out.println("FINISHED LOADING RESTAURANTS IN BACKGROUND");
            Intent i = new Intent(FoodFinderController.this, SelectRestaurantController.class);
            i.putExtra("model", result);
            startActivity(i);
        }
    }

    private void swipeAnimation() {
        try {
            api = YelpApi.getInstance();
            mainView = (ImageView) findViewById(R.id.image);

            Picasso.with(context).load(server + gallery[index].getLink()).into(mainView);

            setSwipeTouchListener();

            animEnter = AnimationUtils.loadAnimation(this, R.anim.animation_enter);
            animLeave = AnimationUtils.loadAnimation(this, R.anim.animation_leave);
            animLeave.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    try {
                        Picasso.with(context).load(server + gallery[index].getLink()).into(mainView);
                    } catch (Exception e) {
                        e.printStackTrace();
                        startActivity(new Intent(FoodFinderController.this, ErrorController.class));
                    }
                    mainView.startAnimation(animEnter);
                }
            });
        } catch (Exception e) {
            System.err.println("Ayy LMAO");
            startActivity(new Intent(FoodFinderController.this, ErrorController.class));
            e.printStackTrace();
        }
    }

    private void setSwipeTouchListener() {
        mainView.setOnTouchListener(new OnSwipeTouchListener(FoodFinderController.this) {
            public void onSwipeLeft() {
                mainView.startAnimation(animLeave);
                index++;
                if (index == gallery.length) {
                    getFoodThread.run();
                    index = 0; //(index + 1);
                }
            }

            public void onSwipeRight() {
                culture = gallery[index].getCulture();

                Location mloc = loc;
                if (mloc == null) {
                    Toast.makeText(context, "LOL LOCATION", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(context, mloc.getLatitude() + ", " + mloc.getLongitude(), Toast.LENGTH_SHORT).show();
                RestaurantDataModel.setLongitude(mloc.getLongitude());
                RestaurantDataModel.setLatitude(mloc.getLatitude());

                new LoadRestaurantsTask().execute(gallery[index]);
            }
        });
    }

    public static FoodDto[] getFoodFromServer() {
        //connecting db to main
        ServerApi api = ServerApi.getInstance();
        DBFoodDto[] DBFoodDtos = api.getFood();
        FoodDto[] fromDB = new FoodDto[DBFoodDtos.length];
        for (int i = 0; i < DBFoodDtos.length; i++) {
            try {
                DBFoodDto tempDB = DBFoodDtos[i];
                FoodDto temp = new FoodDto(tempDB.name(), tempDB.name(), tempDB.getTag(), "" + tempDB.path());
                fromDB[i] = temp;
            } catch (Exception e) {
                System.err.println("D'OH");
                e.printStackTrace();
            }
        }
        return fromDB;
    }

    /**
     * Location Stuff
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void askGPS() {
        getLocation();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void getLocation() {
        int LOCATION_REFRESH_TIME = 1000;
        int LOCATION_REFRESH_DISTANCE = 5;

        int tries = 0;
        boolean GPSPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean networkPermission = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (!GPSPermission && !networkPermission)
            ActivityCompat.requestPermissions(FoodFinderController.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        if (GPSPermission || networkPermission) {
            try {
                LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                loc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, this);
                if (loc == null)
                    throw new Exception();
            } catch (NullPointerException e) {
                startActivity(new Intent(FoodFinderController.this, ErrorController.class));
            } catch (Exception e) {

            }
        } else
            Toast.makeText(context, "This app needs location permissions to perform", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
                return;
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