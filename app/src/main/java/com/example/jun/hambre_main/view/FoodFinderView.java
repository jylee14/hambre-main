package com.example.jun.hambre_main.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.jun.hambre_main.controller.FoodFinderController;
import com.example.jun.hambre_main.controller.RestaurantFinderController;
import com.example.jun.hambre_main.model.FoodModel;
import com.example.jun.hambre_main.OnSwipeTouchListener;
import com.example.jun.hambre_main.R;
import com.example.jun.yelp.BusinessModel;
import com.example.jun.yelp.YelpApi;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class FoodFinderView extends AppCompatActivity implements Runnable{
    private ImageView mainView;
    private static int index;
    private int rad = 1600; //min is 1 mile
    private final int limit = 20;   //term limit is set to 20 arbitrarily for now

    private YelpApi api;
    private Bundle bundle;
    private String culture;
    private FoodModel[] gallery;
    private BusinessModel[] response;
    private Animation animEnter, animLeave;
    private final String LOG_TAG = getClass().getSimpleName(); //for log
    private final String server = "http://159.203.246.214/irs/";

    private FoodModel[] dbfm;
    FoodFinderController controller = new FoodFinderController();

    private Thread thread1 = new Thread() {
        public void run() {
            dbfm = controller.getFoodFromServer();
            System.arraycopy(dbfm, 0, gallery, 0, gallery.length);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_food_finder);
        bundle = getIntent().getExtras();

        try {
            gallery = FoodModel.toFoodModel(bundle.getParcelableArray("model"));
            api = YelpApi.getInstance();

            animEnter = AnimationUtils.loadAnimation(this, R.anim.animation_enter);
            animLeave = AnimationUtils.loadAnimation(this, R.anim.animation_leave);

            animLeave.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    try {
                        URL url = new URL(server + gallery[index].getLink());
                        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        mainView.setImageBitmap(bmp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    index++;
                    mainView.startAnimation(animEnter);
                }
            });
            index = 1;

            mainView = (ImageView)findViewById(R.id.image);
            URL url = new URL(server + gallery[index++].getLink());
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            mainView.setImageBitmap(bmp);

            mainView.setOnTouchListener(new OnSwipeTouchListener(FoodFinderView.this) {
                public void onSwipeLeft() {
                    if (index < gallery.length) {
                        mainView.startAnimation(animLeave);
                    }else{  //replace stuff
                        mainView.startAnimation(animLeave);
                        thread1.run();
                        index = 0;
                    }
                }

                public void onSwipeRight() {
                    culture = gallery[index-1].getCulture();
                    //Log.v(LOG_TAG, "culture: " + culture);
                    run();

                    Intent i = new Intent(FoodFinderView.this, SelectRestaurantView.class);
                    i.putExtra("model", response);
                    startActivity(i);
                }
            });
        }catch(Exception e){
            System.err.println("Ayy LMAO");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("location", "9450%20Gilman%20Dr.%20La%20Jolla%20CA%2092092");
        params.put("categories", "food");
        params.put("term", culture);
        params.put("sort", "" + PreferencesView.byRating);
        params.put("radius", "" + rad);
        params.put("limit", "" + limit);

        response = RestaurantFinderController.findRestaurants(params);
    }
}