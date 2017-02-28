package com.example.jun.hambre_main.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.SslCertificate;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.jun.hambre_main.controller.FoodFinderController;
import com.example.jun.hambre_main.controller.RestaurantFinderController;
import com.example.jun.hambre_main.model.FoodModel;
import com.example.jun.hambre_main.OnSwipeTouchListener;
import com.example.jun.hambre_main.R;
import com.example.jun.yelp.BusinessModel;
import com.example.jun.yelp.YelpApi;

import java.io.IOException;
import java.net.MalformedURLException;
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

    private Thread getFoodThread = new Thread() {
        public void run() {
            System.err.println("Running bg food thread: " + android.os.Process.myTid());
            dbfm = controller.getFoodFromServer();

            // copy database foodmodels into gallery array
            System.arraycopy(dbfm, 0, gallery, 0, gallery.length);

            reloadImages = true;
        }
    };

    private boolean reloadImages = true;
    private Bitmap[] galleryImages;

    private Thread picturesThread = new Thread() {
        public void run() {
            System.err.println("Running bg thread: " + android.os.Process.myTid());
                try {
                    System.err.println("downloading gallery");
                    galleryImages = new Bitmap[gallery.length];
                    for (int i = 0; i < gallery.length; i++) {

                        // loop through gallery getting images for each image
                        URL url = new URL(server + gallery[i].getLink());
                        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        galleryImages[i] = bmp;
                        mainView.setImageBitmap(bmp);
                    }
                    System.err.println("FINISHED");
                    reloadImages = false;
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("I don't want to mess up my blade");
                }
        }
    };

    private class DownloadPicturesTask extends AsyncTask<URL, Integer, Bitmap[]> {
        @Override
        protected Bitmap[] doInBackground(URL... params) {
            System.out.println("Running bg thread: " + android.os.Process.myTid());
            Bitmap[] images = new Bitmap[params.length];
            for (int i = 0; i < params.length; i++) {
                Bitmap bmp;
                try {
                    bmp = BitmapFactory.decodeStream(params[i].openConnection().getInputStream());
                } catch (IOException e) {
                    bmp = null;
                    e.printStackTrace();
                }

                images[i] = bmp;
            }
            return images;
        }

        @Override
        protected void onPostExecute(Bitmap[] result) {
            System.out.println("post execute running");
            galleryImages = result;
            mainView.setImageBitmap(result[index]);
            reloadImages = false;
        }

    }

    private void updateImage() {
        System.err.println("updating image");
        System.err.println("Running ui thread: " + android.os.Process.myTid());
        if (reloadImages) {
            int w = 500;
            int h = 500; // or whatever sizes you need
            Bitmap.Config config = Bitmap.Config.ARGB_8888;
            mainView.setImageBitmap(Bitmap.createBitmap(w, h, config));

            URL[] urls = new URL[gallery.length];
            for (int i = 0; i < gallery.length; i++) {
                // loop through gallery getting images for each image
                try {
                    URL url = new URL(server + gallery[i].getLink());
                    urls[i] = url;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
            new DownloadPicturesTask().execute(urls);
        } else {
            mainView.setImageBitmap(galleryImages[index]);
        }
        System.err.println("finished updating image");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_food_finder);
        bundle = getIntent().getExtras();

        try {
            gallery = FoodModel.toFoodModel(bundle.getParcelableArray("model"));
            api = YelpApi.getInstance();
            mainView = (ImageView)findViewById(R.id.image);

            updateImage();
            //time consuming
            URL url = new URL(server + gallery[index].getLink());
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            mainView.setImageBitmap(bmp);


            mainView.setOnTouchListener(new OnSwipeTouchListener(FoodFinderView.this) {
                public void onSwipeLeft() {
                    mainView.startAnimation(animLeave);
                    index++;
                    if(index == gallery.length){
                        getFoodThread.run();
                        index = 0; //(index + 1);
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

                    // Bad idea: response is loaded in slow network operation on separate thread
                    // this is a race condition, except the thread will almost always lose and this will almost
                    // always fail.
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
                        //URL url = new URL(server + gallery[index++].getLink());
                        //Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                        // TODO: possible error here
                        //while (galleryImages == null) {
                           // mainView.setImageBitmap(galleryImages[index]);
                        //}
                        // index++;
                        updateImage();
                    } catch (Exception e) {
                        e.printStackTrace();
                        startActivity(new Intent(FoodFinderView.this, error.class));
                    }
                    mainView.startAnimation(animEnter);
                }
            });
        }catch(Exception e){
            System.err.println("Ayy LMAO");
            startActivity(new Intent(FoodFinderView.this, error.class));
            e.printStackTrace();
        }
    }
}