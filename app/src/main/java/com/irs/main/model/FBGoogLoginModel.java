package com.irs.main.model;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;

import static android.content.ContentValues.TAG;

public class FBGoogLoginModel {
    private static GoogleSignInOptions gso;
    private static CallbackManager cbmanager;

    public static GoogleSignInOptions getGoogleSignInOptions() {
        if (gso == null) {
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("58151517395-7u4o0o77s2ff8dtbvio1v2tab2snf116.apps.googleusercontent.com")
                    .requestEmail()
                    .build();
        }
        return gso;
    }

    public static CallbackManager getFBManager() {
        if (cbmanager == null) {
            cbmanager = CallbackManager.Factory.create();
        }
        return cbmanager;
    }

    public static boolean loggedIn(GoogleApiClient mGoogleApiClient) {
        boolean fb = AccessToken.getCurrentAccessToken() != null;

        return fb | googleLoggedIn(mGoogleApiClient);
    }

    /**
     * Gets whether the user logged in via google previously
     *
     * "Adapted" from
     * http://stackoverflow.com/questions/35195673/check-whether-the-user-is-already-logged-in-using-auth-googlesigninapi
     * @param mGoogleApiClient something from google
     * @return Whether the user logged in via google previously
     */
    private static boolean googleLoggedIn(GoogleApiClient mGoogleApiClient) {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if(opr.isDone()){
            GoogleSignInResult result = opr.get();
            result.getSignInAccount();
            return true;
        }
        return false;
    }

}
