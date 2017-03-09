package com.irs.main.controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
    private SignInButton goog;        //google login
    private Button guestButton;
    private GoogleApiClient mGoogleApiClient;
    private FBGoogLoginModel loginModel;

    private static final String TAG = "LoginActivity";
    private static final int GOOG_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginModel = FBGoogLoginModel.getInstance();

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

        // check if we are already logged in, if yes, start in foodFinderController
        if(loginModel.loggedInPreviously()){
            startActivity(new Intent(LandingController.this, FoodFinderController.class));
            return;
        }

        setContentView(R.layout.activity_landing);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        facebookLoginButton();
        googleLoginButton();
        guestLogin();
    }

    private void guestLogin() {
        guestButton = (Button) findViewById(R.id.Guest);
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
        goog = (SignInButton) findViewById(R.id.Google);

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
                loginModel.useFacebookToLogin();

                startActivity(new Intent(LandingController.this, PreferencesController.class));
                System.out.println("Facebook Login Success!");
            }

            @Override
            public void onCancel() {}

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "Facebook sign in failed." + error.getMessage());
            }
        });
    }

    private void googleLoginResult(Intent data) {
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if (result.isSuccess()) {
            loginModel.useGoogleToLogin(result);
            startActivity(new Intent(LandingController.this, PreferencesController.class));
        } else {
            Log.d(TAG, "Google sign in failed." + result.getStatus().getStatusCode());
        }
    }
}