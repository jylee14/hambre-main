package com.irs.main.controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.irs.main.R;
import com.irs.main.model.LoginModel;
import com.irs.server.ServerApi;

public class LandingController extends AppCompatActivity {
    private SignInButton goog;        //google login
    private Button guestButton;
    private GoogleApiClient mGoogleApiClient;
    private LoginModel loginModel;

    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginModel = new LoginModel();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_landing);

        facebookLogin();
        googleLogin();
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

    private void googleLogin() {
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        goog = (SignInButton) findViewById(R.id.Google);
        GoogleSignInOptions gso = loginModel.getGoogleSignInOptions();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        // An unresolvable Error has occurred and Google APIs (including Sign-In) will not
                        // be available.
                        Log.d(TAG, "onConnectionFailed:" + connectionResult);
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        goog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, 9001);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 9001) {
            // TODO: 3/2/17 IT BREAKS HERE (result.isSuccess() returns false)
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                ServerApi.getInstance().authServer(acct);
            } else {
                Log.d(TAG, "Google sign in failed." + result.getStatus().getStatusCode());
            }
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
    private void facebookLogin() {
        LoginButton fbButton = (LoginButton) findViewById(R.id.Facebook);
        fbButton.registerCallback(loginModel.getFBManager(), new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                ServerApi.getInstance().authServer(AccessToken.getCurrentAccessToken());
                startActivity(new Intent(LandingController.this, PreferencesController.class));
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
}
