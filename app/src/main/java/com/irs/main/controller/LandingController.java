package com.irs.main.controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
import com.irs.main.model.FBGoogLoginModel;
import com.irs.main.model.UserModel;
import com.irs.server.AuthDto;
import com.irs.server.ServerApi;

public class LandingController extends FragmentActivity {
    private SignInButton goog;        //google login
    private Button guestButton;
    private GoogleApiClient mGoogleApiClient;
    private CallbackManager cbmanager;

    private static final String TAG = "LoginActivity";
    private static final int GOOG_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_landing);

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
        GoogleSignInOptions gso = FBGoogLoginModel.getGoogleSignInOptions();

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
                startActivityForResult(signInIntent, GOOG_SIGN_IN);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 9001) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                System.out.println("SIGNED IN GOOGLE: " + acct.getEmail());
                AuthDto response = ServerApi.getInstance().authServer(acct);
                System.out.println("GOT DATA FROM SERVER: " + response.user());
                UserModel.getInstance().loginAccount(response.user().api_key());
                System.out.println("LOGGED IN TO SERVER");
            } else {
                Log.d(TAG, "Google signin failed." + result.getStatus().getStatusCode());
            }
        } else {
            cbmanager.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Did stuff from https://developers.facebook.com/docs/facebook-login/android/
     * This is the method that manages facebook login (obviously). Should connect
     * with our server on "onSuccess" and download user data and fail "onCancel"
     * or "onError".
     */
    private void facebookLoginButton() {
        cbmanager = CallbackManager.Factory.create();
        LoginButton fbButton = (LoginButton) findViewById(R.id.Facebook);
        fbButton.registerCallback(FBGoogLoginModel.getFBManager(), new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                AuthDto response =
                        ServerApi.getInstance().authServer(AccessToken.getCurrentAccessToken());
                System.out.println("GOT DATA FROM SERVER: " + response.user());

                UserModel.getInstance().loginAccount(response.user().api_key());
                System.out.println("LOGGED IN TO SERVER");

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

    private void googleLoginResult(Intent data) {
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            System.out.println("SIGNED IN GOOGLE: " + acct.getEmail());

            AuthDto response = ServerApi.getInstance().authServer(acct);
            System.out.println("GOT DATA FROM SERVER: " + response.user());

            UserModel.getInstance().loginAccount(response.user().api_key());
            System.out.println("LOGGED IN TO SERVER");

            startActivity(new Intent(LandingController.this, PreferencesController.class));
        } else {
            Log.d(TAG, "Google sign in failed." + result.getStatus().getStatusCode());
        }
    }
}