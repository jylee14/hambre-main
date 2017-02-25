package com.example.jun.hambre_main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jun.server.DBFoodModel;

public class Preferences extends AppCompatActivity {
    private Button diet;
    private SeekBar rad;
    private TextView maxRad;
    private RadioGroup pref;
    private RadioButton rate;
    private RadioButton dist;
    private Button cont;


    //1=Distance, 2=Highest Rated
    protected static int byRating = 2;  //for ordering later. Rating by default
    protected static int radius = 1;   //minimum radius is 1 mile
    protected int meters = 1600;   //yelpAPI wants meters
    private final int meterToMile = 1600;   //conversion factor


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        diet = (Button)findViewById(R.id.DPref);
        rad = (SeekBar) findViewById(R.id.radius);
        maxRad = (TextView)findViewById(R.id.currMax);
        pref = (RadioGroup)findViewById(R.id.sorting);
        rate = (RadioButton)findViewById(R.id.rate);
        dist  = (RadioButton)findViewById(R.id.dist);
        cont = (Button)findViewById(R.id.cont);

        pref.check(R.id.rate);  //rate by default

        rad.setProgress(radius);
        maxRad.setText("" + radius + " mi");

        /**
         * Diet Button will redirect the user to the dietary restrictions page
         */
        diet.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(Preferences.this, DietRestriction.class);
                Bundle bundle = getIntent().getExtras();
                if(bundle != null)
                    i.putExtras(bundle);

                startActivity(i);
            }
        });

        /**
         * preferences of the user. Will test to see if it propagates
         */
        rate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                byRating = 2;
            }
        });
        dist.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                byRating = 1;
            }
        });

        /**
         * change the preference of the user.
         * Need to find a way to propagate the value here throughout the app
         */
        rad.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radius = seekBar.getProgress() ;
                maxRad.setText(radius + " mi");
                meters = radius * meterToMile;
            }
        });

        /**
         * move the user onto next page
         */
        cont.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(radius == 0){
                    Toast.makeText(Preferences.this, "Radius cannot be 0 miles", Toast.LENGTH_SHORT).show();
                }else {
                    FoodFinderController controller = new FoodFinderController();
                    FoodModel[] dbfm = controller.getFoodFromServer();

                    Intent i = new Intent(Preferences.this, FoodFinderView.class);
                    i.putExtra("model", dbfm);
                    startActivity(i);
                }
            }
        });
    }
}
