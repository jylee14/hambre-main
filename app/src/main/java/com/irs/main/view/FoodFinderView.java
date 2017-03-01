package com.irs.main.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.irs.main.controller.FoodFinderController;
import com.irs.main.controller.RestaurantFinderController;
import com.irs.main.model.FoodModel;
import com.irs.main.OnSwipeTouchListener;
import com.irs.main.R;
import com.irs.main.presenter.Error;
import com.irs.yelp.BusinessModel;
import com.irs.yelp.YelpApi;

import java.net.URL;
import java.util.HashMap;

public class FoodFinderView extends AppCompatActivity{
    private ImageView mainView;
    private int index = 0;
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
            mainView = (ImageView)findViewById(R.id.image);

            //time consuming
            URL url = new URL(server + gallery[index++].getLink());
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            mainView.setImageBitmap(bmp);

            mainView.setOnTouchListener(new OnSwipeTouchListener(FoodFinderView.this) {
                public void onSwipeLeft() {
                    mainView.startAnimation(animLeave);
                    if(index == gallery.length){
                        thread1.run();
                        index = (index + 1);
                    }
                }

                public void onSwipeRight() {
                    culture = gallery[index].getCulture();
                    //Log.v(LOG_TAG, "culture: " + culture);

                    new Thread(new Runnable(){
                        public void run() {
                            try {
                                HashMap<String, String> params = new HashMap<String, String>();
                                params.put("location", "9450%20Gilman%20Dr.%20La%20Jolla%20CA%2092092");
                                params.put("categories", "food");
                                params.put("term", culture);
                                params.put("sort", "" + PreferencesView.byRating);
                                params.put("radius", "" + PreferencesView.radius * 1600);
                                params.put("limit", "" + limit);

                                System.out.println(culture);
                                response = RestaurantFinderController.findRestaurants(params);
                            }catch (Exception e){
                                //.........?
                            }
                        }
                    }).start();

                    Intent i = new Intent(FoodFinderView.this, SelectRestaurantView.class);
                    i.putExtra("model", response);
                    startActivity(i);
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
                        URL url = new URL(server + gallery[index++].getLink());
                        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        mainView.setImageBitmap(bmp);
                    } catch (Exception e) {
                        e.printStackTrace();
                        startActivity(new Intent(FoodFinderView.this, Error.class));
                    }
                    mainView.startAnimation(animEnter);
                }
            });
        }catch(Exception e){
            System.err.println("Ayy LMAO");
            startActivity(new Intent(FoodFinderView.this, Error.class));
            e.printStackTrace();
        }
    }
}