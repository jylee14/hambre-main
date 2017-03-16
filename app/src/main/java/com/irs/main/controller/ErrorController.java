package com.irs.main.controller;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.irs.main.R;


// Error page that is displayed when there is no internet connection.
public class ErrorController extends FragmentActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.error);
    }
}
