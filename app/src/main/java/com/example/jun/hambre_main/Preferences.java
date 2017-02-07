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

public class Preferences extends AppCompatActivity {
    private Button diet;
    private SeekBar rad;
    private TextView maxRad;
    private RadioGroup pref;
    private RadioButton rate;
    private RadioButton dist;
    private Button cont;


    protected static boolean byRating = true;  //for ordering later. Rating by default
    protected int radius = 1;   //minimum radius is 1 mile, propagated throughout project
    private int maxTaps = 5;    //we're not going to let the user tap on any button more than 5 times

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
                byRating = true;
            }
        });
        dist.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                byRating = false;
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
                radius = seekBar.getProgress();
                maxRad.setText(radius + " mi");
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
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("byRating", byRating);
                    bundle.putInt("radius", radius);
                    bundle.putString("diet", "None");
                    if(maxTaps > 0) {
                        Toast.makeText(Preferences.this, "Server is still dead; sorry", Toast.LENGTH_SHORT).show();
                        maxTaps--;
                    }
                }
            }
        });
    }
}
