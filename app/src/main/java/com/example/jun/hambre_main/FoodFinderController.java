package com.example.jun.hambre_main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by jeff on 2/13/17.
 */

public class FoodFinderController extends AppCompatActivity {
    private Button nextButton, infoButton, selectButton;
    ImageView mainView;
    //int gallery [];
    int index;
    FoodModel [] gallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_finder);

        //dummy content until db is hooked up
        gallery = new FoodModel[4];
        gallery[0] = new FoodModel("tacos", "mexican", R.drawable.mex);
        gallery[1] = new FoodModel("curry", "indian", R.drawable.indian);
        gallery[2] = new FoodModel("pad thai", "thai", R.drawable.thai);
        gallery[3] = new FoodModel("chow mein", "chinese", R.drawable.chinese);

/*
        gallery = new int[3];
        gallery[0] = R.drawable.chinese;
        gallery[1] = R.drawable.thai;
        gallery[2] = R.drawable.indian;
        index = 0; */
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
                //String culture = mainView.getDrawable().getCurrent().toString();
                //System.err.println("Culture: " + culture);
            }
        });
    }

}
