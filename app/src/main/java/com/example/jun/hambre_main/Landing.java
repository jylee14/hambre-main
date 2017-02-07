package com.example.jun.hambre_main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Landing extends AppCompatActivity {
    private Button guest;       //continue as guest
    private Button goog;        //google login
    private Button fb;          //FB login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

    }

    private void login(LoginType type){
        Intent i = new Intent(Landing.this, Login.class);
        Bundle bundle = new Bundle();
        bundle.putString("type", type.toString());
        startActivity(i);
    }
}
