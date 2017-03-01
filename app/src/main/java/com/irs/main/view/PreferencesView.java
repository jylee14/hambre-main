package com.irs.main.view;

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

import com.irs.main.R;
import com.irs.main.controller.FoodFinderController;
import com.irs.main.model.FoodModel;
import com.irs.main.presenter.DietRestriction;
import com.irs.main.presenter.Error;

public class PreferencesView extends AppCompatActivity implements Runnable {
    private Button diet;
    private SeekBar rad;
    private TextView maxRad;
    private RadioGroup pref;
    private RadioButton rate;
    private RadioButton dist;
    private Button cont;

    private FoodModel[] dbfm;
    FoodFinderController controller = new FoodFinderController();

    //1=Distance, 2=Highest Rated
    public static int byRating = 2;  //for ordering later. Rating by default
    public static int radius = 1;   //minimum radius is 1 mile

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
                Intent i = new Intent(PreferencesView.this, DietRestriction.class);
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
            }
        });

        /**
         * move the user onto next page
         */
        cont.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(radius == 0){
                    Toast.makeText(PreferencesView.this, "Radius cannot be 0 miles", Toast.LENGTH_SHORT).show();
                }else {
                    try {
                        run();

                        Intent i = new Intent(PreferencesView.this, FoodFinderController.class);
                        i.putExtra("model", dbfm);
                        startActivity(i);
                    }catch(Exception e){
                        startActivity(new Intent(PreferencesView.this, Error.class));
                    }
                }
            }
        });
    }

    public void run(){
        dbfm = controller.getFoodFromServer();
    }
}
