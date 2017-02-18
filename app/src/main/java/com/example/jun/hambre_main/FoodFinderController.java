package com.example.jun.hambre_main;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.jun.hambre_main.OnSwipeTouchListener;
import com.example.jun.yelp.BusinessModel;
import com.example.jun.yelp.BusinessResponseModel;
import com.example.jun.yelp.YelpApi;

import java.util.ArrayList;

public class FoodFinderController extends AppCompatActivity {
    private Button infoButton;
    private ImageView mainView;
    private static int index;
    private FoodModel [] gallery;
    private Bundle bundle;
    private int rad = 1600; //min is 1 mile
    private final String LOG_TAG = getClass().getSimpleName(); //for log
    private final int limit = 20;   //term limit is set to 20 arbitrarily for now
    private YelpApi api;
    private Animation animEnter, animLeave;
    //private int [] resID; //for looping through pics until db is set up

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_food_finder);

        bundle = getIntent().getExtras();
        rad = bundle.getInt("radius");

        api = YelpApi.getInstance();

        //dummy content until db is hooked up
        gallery = new FoodModel[4];
        gallery[0] = new FoodModel("tacos", "mexican", R.drawable.mexican);
        gallery[1] = new FoodModel("curry", "indian", R.drawable.indian);
        gallery[2] = new FoodModel("pad thai", "thai", R.drawable.thai);
        gallery[3] = new FoodModel("chow mein", "chinese", R.drawable.chinese);

        animEnter = AnimationUtils.loadAnimation(this, R.anim.animation_enter);
        animLeave = AnimationUtils.loadAnimation(this, R.anim.animation_leave);

        animLeave.setAnimationListener(new Animation.AnimationListener(){
            public void onAnimationStart(Animation animation) {}
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationEnd(Animation animation){
                try{
                    mainView.setImageResource(gallery[index].getTempLink());
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                index++;
                mainView.startAnimation(animEnter);
            }
        });
        animEnter.setAnimationListener(new Animation.AnimationListener(){
            public void onAnimationStart(Animation animation) {}
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationEnd(Animation animation) {

            }
        });

        index = 1;
        mainView = (ImageView)findViewById(R.id.image);
        mainView.setImageResource(gallery[0].getTempLink());
        mainView.setOnTouchListener(new OnSwipeTouchListener(FoodFinderController.this) {
            public void onSwipeLeft(){
                if(index < 4) {
                    //mainView.setImageResource(gallery[index].getTempLink());
                    //index++;
                    mainView.startAnimation(animLeave);
                }
                else //TODO change this eventually
                    Toast.makeText(getApplication().getBaseContext(), "out of pics", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeRight(){
                String culture = gallery[index-1].getCulture();
                Log.v(LOG_TAG, "culture: " + culture);

                //building params to pass as individual strings
                ArrayList<String> param = new ArrayList<>();

                param.add("location");      param.add("9450%20Gilman%20Dr.%20La%20Jolla%20CA%2092092");
                param.add("categories");    param.add("food");
                param.add("term");          param.add(culture);
                param.add("sort");          param.add("" + Preferences.byRating);
                param.add("radius");        param.add("" + rad);
                param.add("limit");         param.add("" + limit);

                Intent i = new Intent(FoodFinderController.this, SelectRestaurantController.class);
                i.putStringArrayListExtra("param", param);
                startActivity(i);
            }
        });
    }
}