package com.example.jun.hambre_main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.jun.hambre_main.OnSwipeTouchListener;
import com.example.jun.yelp.BusinessModel;
import com.example.jun.yelp.BusinessResponseModel;
import com.example.jun.yelp.YelpApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class FoodFinderController extends AppCompatActivity {
    private Button nextButton, infoButton, selectButton;
    private ImageView mainView;
    private int index;
    private FoodModel [] gallery;
    private Bundle bundle;
    private int rad = 1600; //min is 1 mile
    private final String LOG_TAG = getClass().getSimpleName(); //for log

    private YelpApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        index = 1;
        mainView = (ImageView)findViewById(R.id.image);
        mainView.setImageResource(gallery[0].getTempLink());
        mainView.setOnTouchListener(new OnSwipeTouchListener(FoodFinderController.this) {
            public void onSwipeLeft(){
                if(index < 4) {
                    mainView.setImageResource(gallery[index].getTempLink());
                    index++;
                }
                else //TODO change this eventually
                    Toast.makeText(getApplication().getBaseContext(), "out of pics", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeRight(){
                String culture = gallery[index-1].getCulture();
                Log.v(LOG_TAG, "culture: " + culture);

                //building params to pass as individual strings
                ArrayList<String> param = new ArrayList<>();
                param.add("location");
                param.add("9450 Gilman Dr. La Jolla CA, 92092");
                param.add("categories");
                param.add("food");
                param.add("term");
                param.add(culture);
                param.add("sort");
                param.add("" + Preferences.byRating);
                param.add("radius");
                param.add("" + rad);

                Intent i = new Intent(FoodFinderController.this, SelectRestaurantController.class);
                i.putStringArrayListExtra("param", param);
                startActivity(i);
            }
        });

        nextButton = (Button) findViewById(R.id.btn_next);
        nextButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                if(index < 4) {
                    mainView.setImageResource(gallery[index].getTempLink());
                    index++;
                }
                else //TODO change this eventually
                    Toast.makeText(getApplication().getBaseContext(),
                            "out of pics", Toast.LENGTH_SHORT).show();
            }
        });
        selectButton = (Button)findViewById(R.id.btn_select);
        selectButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){

                String culture = gallery[index-1].getCulture();
                Log.v(LOG_TAG, "culture: " + culture);
                YelpApi api = YelpApi.getInstance();

                // build params
                HashMap<String, String> params = new HashMap<>();
                params.put("location", "9450 Gilman Dr. La Jolla CA, 92092");
                params.put("categories", "food");
                params.put("term", culture);
                params.put("sort", "" + Preferences.byRating);
                params.put("radius_filter", "" + rad);

                BusinessResponseModel businessResponse = api.businessSearch(params);
                //TODO have this open SelectRestaurantController.java and display a list
                //TODO of the restaurants returned

                ArrayList<BusinessModel> businesses = new ArrayList<>(Arrays.asList(businessResponse.businesses()));
                Intent i = new Intent(FoodFinderController.this, SelectRestaurantController.class);
                i.putExtra("businessResponseObject", businessResponse);
                startActivity(i);
            }
        });
    }

}
