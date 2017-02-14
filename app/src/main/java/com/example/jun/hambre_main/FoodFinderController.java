package com.example.jun.hambre_main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.jun.yelp.BusinessResponseModel;
import com.example.jun.yelp.YelpApi;

import java.util.HashMap;

/**
 * Created by jeff on 2/13/17.
 */

public class FoodFinderController extends AppCompatActivity {
    private Button nextButton, infoButton, selectButton;
    ImageView mainView;
    //int gallery [];
    int index;
    FoodModel [] gallery;
    private Bundle bundle;
    private int rad = 1600; //min is 1 mile
    private final String LOG_TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_finder);

        bundle = getIntent().getExtras();
        rad = bundle.getInt("radius");
        //dummy content until db is hooked up
        gallery = new FoodModel[4];
        gallery[0] = new FoodModel("tacos", "mexican", R.drawable.mex);
        gallery[1] = new FoodModel("curry", "indian", R.drawable.indian);
        gallery[2] = new FoodModel("pad thai", "thai", R.drawable.thai);
        gallery[3] = new FoodModel("chow mein", "chinese", R.drawable.chinese);
        
        index = 1;
        mainView = (ImageView)findViewById(R.id.image);
        mainView.setImageResource(gallery[0].getTempLink());
        nextButton = (Button) findViewById(R.id.btn_next);
        nextButton.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v){

                if(index < 4) {
                    mainView.setImageResource(gallery[index].getTempLink());
                    index++;
                }
                else
                    Toast.makeText(getApplication().getBaseContext(),
                            "out of pics", Toast.LENGTH_SHORT).show();
            }
        });
        selectButton = (Button)findViewById(R.id.btn_select);
        selectButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){

                String culture = gallery[index-1].getCulture();
                //System.err.println("culture: " + culture);
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
            }
        });
    }

}
