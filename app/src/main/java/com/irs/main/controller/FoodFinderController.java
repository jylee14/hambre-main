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

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutionException;

import static android.content.Context.LOCATION_SERVICE;

public class FoodFinderController extends FragmentActivity implements android.location.LocationListener {
    private final String server = "http://159.203.246.214/irs/";
    private final Context context = this;

    private static Deque<FoodDto> gallery = new ConcurrentLinkedDeque<>();

    private ImageView mainView;

    private Location loc;
    private String culture;

    private Animation animEnter, animLeave;
    private UserModel user = UserModel.getInstance();

    private class GetFoodFromServer extends AsyncTask<Deque<FoodDto>, Integer, Deque<FoodDto>> {
        @Override
        protected Deque<FoodDto> doInBackground(Deque<FoodDto>... params) {
            ServerApi api = ServerApi.getInstance();
            DBFoodDto[] DBFoodDtos = api.getFood();
            if(user.getIsGuest())
                DBFoodDtos = api.getFoodByParams(user.getDietType());
            else
                DBFoodDtos = api.getFoodByUser(user.getApiKey());

            if (DBFoodDtos == null) {
                return null;
            }

            for (int i = 0; i < DBFoodDtos.length; i++) {
                try {
                    DBFoodDto tempDB = DBFoodDtos[i];
                    FoodDto temp = new FoodDto(tempDB.name(), tempDB.getCulture(), tempDB.getTag(), "" + tempDB.path());
                    params[0].push(temp);
                } catch (Exception e) {
                    System.err.println("D'OH");
                    e.printStackTrace();
                }
            }

            return params[0];
        }

        @Override
        protected void onPostExecute(Deque<FoodDto> foodDtos) {
            if (foodDtos == null) {
                // WIFI ERROR
                System.err.println("wifi error");
                Toast.makeText(FoodFinderController.this, "Could not connect to network!", Toast.LENGTH_SHORT).show();
            }
            //Picasso.with(context).load(server + gallery.peek().getLink()).into(mainView);
            //gallery.pop();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_finder);
        try {
            new GetFoodFromServer().execute(gallery).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        initButtons();
        swipeAnimation();
        askGPS();
    }

    private void initButtons() {
        Button uploadButton = (Button) findViewById(R.id.btn_upload);
        uploadButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FBGoogLoginModel.getInstance().isLoggedIn()) {
                    Intent i = new Intent(FoodFinderController.this, UploadPhotoController.class);
                    startActivity(i);
                } else {
                    Toast.makeText(FoodFinderController.this, "You need to be logged in", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button settingsButton = (Button) findViewById(R.id.btn_settings);
        settingsButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FoodFinderController.this, PreferencesController.class);
                startActivityForResult(i, 2301);
            }
        });

        leftRightButtonClick();
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

    private class LoadRestaurantsTask extends AsyncTask<FoodDto, Integer, BusinessDto[]> {
        @SafeVarargs
        @Override
        protected final BusinessDto[] doInBackground(FoodDto... params) {
            System.out.println("LOADING RESTAURANTS IN BACKGROUND");
            BusinessDto[] response = null;
            FoodDto food = params[0];
            try {
                System.out.println(culture);
                int METERS_PER_MILE = 1600;
                int LIMIT = 20;
                response = RestaurantDataModel.getRestaurants(
                        user.getLatitude(),
                        user.getLongitude(),
                        food.getTag(), food.getCulture(),
                        user.getSortType(),
                        user.getMaxDist() * METERS_PER_MILE,
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
            mainView = (ImageView) findViewById(R.id.image);
            FoodDto curr = gallery.peek();
            Picasso.with(context).load(server + curr.getLink()).into(mainView);

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
                        gallery.peek();
                        Picasso.with(context).load(server + gallery.peek().getLink()).into(mainView);
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

    private void swipeLeftUpdate() {
        if(!gallery.isEmpty()){
            gallery.remove();
        }
        if(gallery.isEmpty()) {
            try {
                new GetFoodFromServer().execute(gallery).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        mainView.startAnimation(animLeave);

    }

    private void swipeRightUpdate() {
        culture = gallery.peek().getCulture();

        Location mloc = loc;
        if (mloc == null) {
            Toast.makeText(context,
                    "couldn't get your location!!\nPlease enable location in settings",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(context, "Our team of eggsperts are looking for restaurants near you", Toast.LENGTH_SHORT).show();
        UserModel.getInstance().setLongitude(mloc.getLongitude());
        UserModel.getInstance().setLatitude(mloc.getLatitude());
        new LoadRestaurantsTask().execute(gallery.poll());
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Location Stuff
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void askGPS() {
        getLocation();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getLocation() {
        int LOCATION_REFRESH_TIME = 1000;
        int LOCATION_REFRESH_DISTANCE = 5;

        boolean GPSPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean networkPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (!GPSPermission && !networkPermission)
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);

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
                System.err.println("location is still null");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        final int LOCATION_REQUEST_CODE = 101;
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(getIntent());
                    finish();
                } else {
                    Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data){
        if(resultCode == 244){
            System.out.println("returned from preferences");
            gallery.clear();
            try {
                new GetFoodFromServer().execute(gallery).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            FoodDto curr = gallery.peek();
            Picasso.with(context).load(server + curr.getLink()).into(mainView);
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
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}