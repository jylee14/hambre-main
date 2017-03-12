package com.irs.main.controller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.irs.main.R;
import com.irs.main.model.FBGoogLoginModel;

public class LandingController extends FragmentActivity {
    private GoogleApiClient mGoogleApiClient;
    private FBGoogLoginModel loginModel;

    private static final String TAG = "LoginActivity";
    private static final String NETWORK_LOGIN_ERROR = "Could not login! No network connection available";
    private static final int GOOG_SIGN_IN = 9001;

    private AlertDialog.Builder networkAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginModel = FBGoogLoginModel.getInstance();

        networkAlert = new AlertDialog.Builder(LandingController.this).setTitle("No Connection")
                        .setMessage("You need a network connection to use this app. Please connect to a network and try again")
                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                System.exit(0);
                            }
                        }).setCancelable(false);

        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork == null) { // not connected to the internet
            networkAlert.show();
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        // An unresolvable ErrorController has occurred and Google APIs (including Sign-In) will not
                        // be available.
                        Log.d(TAG, "onConnectionFailed:" + connectionResult);
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, loginModel.getGoogleSignInOptions())
                .build();

        loginModel.setGoogleApiClient(mGoogleApiClient);
        loginModel.setLanding(this);

        // check if we are already logged in, if yes, start in foodFinderController
        new CheckLoginAsync().execute();
        /* if (loginModel.loggedInPreviously()) {
            // this breaks because the activity is started before the login finishes.
            startActivity(new Intent(LandingController.this, FoodFinderController.class));
            changeLandingScreenAfterLogin();
            return;
        }

        // not logged in
        changeLoginScreenBeforeLogin(); */
    }

    public void changeLoginScreenBeforeLogin() {
        setContentView(R.layout.activity_landing_logged_out);

        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy);

        facebookLoginButton();
        googleLoginButton();
        guestLogin();
    }

    private void continueButton() {
        findViewById(R.id.button_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LandingController.this, PreferencesController.class));
            }
        });
    }

    private void guestLogin() {
        Button guestButton = (Button) findViewById(R.id.Guest);
        guestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LandingController.this, PreferencesController.class));
            }
        });
    }

    private void googleLoginButton() {
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        SignInButton goog = (SignInButton) findViewById(R.id.Google);

        goog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, GOOG_SIGN_IN);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOG_SIGN_IN) {
            googleLoginResult(data);
        } else {
            loginModel.getFBManager().onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Did stuff from https://developers.facebook.com/docs/facebook-login/android/
     * This is the method that manages facebook login (obviously). Should connect
     * with our server on "onSuccess" and download user data and fail "onCancel"
     * or "onError".
     */
    private void facebookLoginButton() {
        LoginButton fbButton = (LoginButton) findViewById(R.id.Facebook);
        fbButton.registerCallback(loginModel.getFBManager(), new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                new LoginToFacebookAsync().execute();

                System.out.println("Facebook Login Success!");
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "Facebook sign in failed." + error.getMessage());
            }
        });
    }

    private void changeLandingScreenAfterLogin() {
        setContentView(R.layout.activity_landing_logged_in);
        continueButton();
    }

    private void googleLoginResult(Intent data) {
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if (result.isSuccess()) {
            loginModel.useGoogleToLogin(result);

            startActivity(new Intent(LandingController.this, PreferencesController.class));
            changeLandingScreenAfterLogin();
        } else {
            Log.d(TAG, "Google sign in failed." + result.getStatus().getStatusCode());
        }
    }

    private class LoginToFacebookAsync extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean success = loginModel.useFacebookToLogin();
            return success;

        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                startActivity(new Intent(LandingController.this, PreferencesController.class));
                changeLandingScreenAfterLogin();
            }
        }
    }

    private class CheckLoginAsync extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            // Network operation
            Boolean loginCheck = loginModel.loggedInPreviously();
            return loginCheck;
        }

        @Override
        protected void onPostExecute(Boolean loginCheck) {
            // logged in
            if (loginCheck !=  null && loginCheck == true) {
                startActivity(new Intent(LandingController.this, FoodFinderController.class));
                changeLandingScreenAfterLogin();
                return;

            }
            changeLoginScreenBeforeLogin();
        }
    }

}