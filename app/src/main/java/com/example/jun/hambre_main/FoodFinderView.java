package com.example.jun.hambre_main;

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

import com.example.jun.server.DBFoodModel;
import com.example.jun.server.ServerApi;
import com.example.jun.yelp.YelpApi;

import java.net.URL;
import java.util.ArrayList;

public class FoodFinderView extends AppCompatActivity {
    private ImageView mainView;
    private static int index;
    private int rad = 1600; //min is 1 mile
    private final int limit = 20;   //term limit is set to 20 arbitrarily for now

    private YelpApi api;
    private Bundle bundle;
    private FoodModel[] gallery;
    private Animation animEnter, animLeave;
    private final String LOG_TAG = getClass().getSimpleName(); //for log
    private final String server = "http://159.203.246.214/irs/";

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
            URL url = new URL(server + gallery[index].getLink());
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            mainView.setImageBitmap(bmp);

            mainView.setOnTouchListener(new OnSwipeTouchListener(FoodFinderView.this) {
                public void onSwipeLeft() {
                    if (index < gallery.length) {
                        mainView.startAnimation(animLeave);
                    } else //TODO change this eventually
                        Toast.makeText(getApplication().getBaseContext(), "out of pics", Toast.LENGTH_SHORT).show();
                }

                public void onSwipeRight() {
                    String culture = gallery[index - 1].getCulture();
                    Log.v(LOG_TAG, "culture: " + culture);

                    //building params to pass as individual strings
                    ArrayList<String> param = new ArrayList<>();

                    param.add("location");
                    param.add("9450%20Gilman%20Dr.%20La%20Jolla%20CA%2092092");
                    param.add("categories");
                    param.add("food");
                    param.add("term");
                    param.add(culture);
                    param.add("sort");
                    param.add("" + Preferences.byRating);
                    param.add("radius");
                    param.add("" + rad);
                    param.add("limit");
                    param.add("" + limit);

                    Intent i = new Intent(FoodFinderView.this, SelectRestaurantController.class);
                    i.putStringArrayListExtra("param", param);
                    startActivity(i);
                }
            });
        }catch(Exception e){
            System.err.println("Ayy LMAO");
            e.printStackTrace();
        }
    }
}