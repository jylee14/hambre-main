package com.irs.main.controller;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.irs.main.R;
import com.irs.main.model.FoodModel;
import com.irs.main.model.OnSwipeTouchListener;
import com.irs.main.model.RestaurantDataModel;
import com.irs.main.model.UserModel;
import com.irs.server.DBFoodModel;
import com.irs.server.ServerApi;
import com.irs.yelp.BusinessModel;
import com.irs.yelp.YelpApi;
import com.squareup.picasso.Picasso;

import java.lang.*;
import java.util.HashMap;

public class FoodFinderController extends FragmentActivity {

    private final Context context = this;

    private ImageView mainView;
    private int index = 0;
    private final int LIMIT = 20;   //term limit is set to 20 arbitrarily for now
    private final int METERS_PER_MILE = 1600;

    private YelpApi api;
    private Bundle bundle;
    private String culture;
    private FoodModel[] gallery;
    private Animation animEnter, animLeave;
    // --Commented out by Inspection (3/1/17, 1:02 PM):private final String LOG_TAG = getClass().getSimpleName(); //for log
    private final String server = "http://159.203.246.214/irs/";

    private FoodModel[] dbfm;
    private Button uploadButton;
    private UserModel user = UserModel.getInstance();

    private final Thread getFoodThread = new Thread() {
        public void run() {
            System.err.println("Running bg food thread: " + android.os.Process.myTid());
            dbfm = getFoodFromServer();

            // copy database foodmodels into gallery array
            System.arraycopy(dbfm, 0, gallery, 0, gallery.length);

            reloadImages = true;
        }
    };

    private boolean reloadImages = true;
    //private Bitmap[] galleryImages;

    private class LoadRestaurantsTask extends AsyncTask<FoodModel, Integer, BusinessModel[]> {
        @SafeVarargs
        @Override
        protected final BusinessModel[] doInBackground(FoodModel...  params) {
            System.out.println("LOADING RESTAURANTS IN BACKGROUND");
            BusinessModel[] response = null;
            FoodModel food = params[0];
            try {
                System.out.println(culture);
                // TODO: set gps based location in first param
                // then update the RestaurantDataModel.getRestaurants method
                response = RestaurantDataModel.getRestaurants(
                        "", food.getTag(), food.getCulture(),
                        UserModel.getInstance().getSortType(),
                        UserModel.getInstance().getMaxDist() * METERS_PER_MILE,
                        LIMIT, false);
                System.out.println("Response: " + response[0].name());
            } catch (Exception e) {
                System.out.println("MAYBE I WASN'T TRYING HARD ENOUGH");
                e.printStackTrace();
                //.........?
            }
            System.out.println("finished loading restaurants");
            return response;
        }

        @Override
        protected void onPostExecute(BusinessModel[] result) {
            System.out.println("FINISHED LOADING RESTAURANTS IN BACKGROUND");
            Intent i = new Intent(FoodFinderController.this, SelectRestaurantController.class);
            i.putExtra("model", result);
            startActivity(i);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_food_finder);

        uploadButton = (Button)findViewById(R.id.btn_upload);
        uploadButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(FoodFinderController.this,
                        UploadPhoto.class);
                startActivity(i);
            }
        });

        bundle = getIntent().getExtras();

        try {
            gallery = FoodModel.toFoodModel(bundle.getParcelableArray("model"));
            api = YelpApi.getInstance();
            mainView = (ImageView) findViewById(R.id.image);

            Picasso.with(context).load(server + gallery[index].getLink()).into(mainView);

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
                    String tag = gallery[index].getTag();

                    // Load Restaurants in the background
                    new LoadRestaurantsTask().execute(gallery[index]);

                }
            });

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
                        startActivity(new Intent(FoodFinderController.this, Error.class));
                    }
                    mainView.startAnimation(animEnter);
                }
            });
        } catch (Exception e) {
            System.err.println("Ayy LMAO");
            startActivity(new Intent(FoodFinderController.this, Error.class));
            e.printStackTrace();
        }
    }

    public FoodModel[] getFoodFromServer() {
        //connecting db to main
        ServerApi api = ServerApi.getInstance();
        DBFoodModel[] DBFoodModels = api.getFood();
        FoodModel[] fromDB = new FoodModel[DBFoodModels.length];
        for (int i = 0; i < DBFoodModels.length; i++) {
            try {
                DBFoodModel tempDB = DBFoodModels[i];
                FoodModel temp = new FoodModel(tempDB.name(), tempDB.name(), tempDB.getTag(), "" + tempDB.path());
                fromDB[i] = temp;
            } catch (Exception e) {
                System.err.println("D'OH");
                e.printStackTrace();
            }
        }
        return fromDB;
    }
}