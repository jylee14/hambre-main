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
import com.irs.main.model.FBGoogLoginModel;
import com.irs.main.model.FoodDto;
import com.irs.main.model.OnSwipeTouchListener;
import com.irs.main.model.RestaurantDataModel;
import com.irs.main.model.UserModel;
import com.irs.server.DBFoodDto;
import com.irs.server.ServerApi;
import com.irs.yelp.BusinessDto;
import com.irs.yelp.YelpApi;
import com.squareup.picasso.Picasso;

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



    private class GetFoodFromServer extends AsyncTask<FoodDto[], Integer, FoodDto[]> {

        @Override
        protected FoodDto[] doInBackground(FoodDto[]... params) {
            ServerApi api = ServerApi.getInstance();
            DBFoodDto[] DBFoodDtos = api.getFood();
            for (int i = 0; i < DBFoodDtos.length; i++) {
                try {
                    DBFoodDto tempDB = DBFoodDtos[i];
                    FoodDto temp = new FoodDto(tempDB.name(), tempDB.getCulture(), tempDB.getTag(), "" + tempDB.path());
                    params[0][i] = temp;
                } catch (Exception e) {
                    System.err.println("D'OH");
                    e.printStackTrace();
                }
            }
            return params[0];
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gallery = new FoodDto[10];

        setContentView(R.layout.activity_food_finder);


        new GetFoodFromServer().execute(gallery);
        initButtons();
        swipeAnimation();
        askGPS();
    }

    private void initButtons() {
        uploadButton = (Button) findViewById(R.id.btn_upload);
        uploadButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FBGoogLoginModel.getInstance().isLoggedIn()) {
                    Intent i = new Intent(FoodFinderController.this,
                            UploadPhotoController.class);
                    startActivity(i);
                } else {
                    Toast.makeText(FoodFinderController.this,
                            "You need to be logged in", Toast.LENGTH_SHORT).show();
                }
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

        leftRightButtonClick();
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
                swipeLeftUpdate();
            }

            public void onSwipeRight() {
                swipeRightUpdate();
            }
        });
    }

    public void swipeLeftUpdate() {
        mainView.startAnimation(animLeave);
        index++;
        if (index == gallery.length) {
            new GetFoodFromServer().execute(gallery);
            index = 0; //(index + 1);
        }
    }

    public void swipeRightUpdate() {
        culture = gallery[index].getCulture();

        Location mloc = loc;
        if (mloc == null) {
            Toast.makeText(context, "LOL LOCATION", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(context, "Our team of eggsperts are looking for restaurants near you", Toast.LENGTH_SHORT).show();
        UserModel.getInstance().setLongitude(mloc.getLongitude());
        UserModel.getInstance().setLatitude(mloc.getLatitude());

        new LoadRestaurantsTask().execute(gallery[index]);
    }

    private void leftRightButtonClick(){
        Button LeftButton = (Button)findViewById(R.id.button_no);
        LeftButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                swipeLeftUpdate();
            }
        });

        Button RightButton = (Button)findViewById(R.id.button_yes);
        RightButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                swipeRightUpdate();
            }
        });
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
        boolean networkPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

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
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}