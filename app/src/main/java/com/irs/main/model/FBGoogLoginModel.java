package com.irs.main.model;

import android.os.StrictMode;
import android.support.annotation.NonNull;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.irs.server.AuthDto;
import com.irs.server.ServerApi;

public class FBGoogLoginModel {
    private static GoogleSignInOptions gso;
    private static GoogleApiClient  googleApiClient;
    private static CallbackManager cbmanager;

    private static boolean loggedIntoFacebook = false;
    private static boolean loggedIntoGoogle = false;

    public static GoogleSignInOptions getGoogleSignInOptions() {
        if (gso == null) {
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("58151517395-7u4o0o77s2ff8dtbvio1v2tab2snf116.apps.googleusercontent.com")
                    .requestEmail()
                    .build();
        }
        return gso;
    }

    public static void setGoogleApiClient(GoogleApiClient client){
        googleApiClient = client;
    }

    public static CallbackManager getFBManager() {
        if (cbmanager == null) {
            cbmanager = CallbackManager.Factory.create();
        }
        return cbmanager;
    }

    public static boolean loggedIn() {
        // if facebook is logged in
        if(AccessToken.getCurrentAccessToken() != null){
            useFacebookToLogin();
            return true;
        }

        // else return whether google was logged in
        return googleLoggedIn();

    }

    /**
     * Gets whether the user logged in via google previously
     *
     * "Adapted" from
     * http://stackoverflow.com/questions/35195673/check-whether-the-user-is-already-logged-in-using-auth-googlesigninapi
     * @return Whether the user logged in via google previously
     */
    private static boolean googleLoggedIn() {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if(opr.isDone()){
            useGoogleToLogin(opr.get());
            return true;
        }
        return false;
    }

    public static void useFacebookToLogin(){
        // TODO: 3/8/17 ASYNC?
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        AuthDto response =
                ServerApi.getInstance().authServer(AccessToken.getCurrentAccessToken());
        System.out.println("GOT DATA FROM SERVER: " + response.user());

        UserModel.getInstance().loginAccount(response.user().api_key());
        System.out.println("LOGGED IN TO SERVER");

        loggedIntoFacebook = true;
    }

    public static void useGoogleToLogin(GoogleSignInResult result){
        // TODO: 3/8/17 ASYNC?
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        GoogleSignInAccount acct = result.getSignInAccount();
        System.out.println("SIGNED IN GOOGLE: " + acct.getEmail());

        AuthDto response = ServerApi.getInstance().authServer(acct);
        System.out.println("GOT DATA FROM SERVER: " + response.user());

        UserModel.getInstance().loginAccount(response.user().api_key());
        System.out.println("LOGGED IN TO SERVER");

        loggedIntoGoogle = true;
    }

    public static void logout(){
        // logout of facebook
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if(loggedIntoFacebook) {
            LoginManager.getInstance().logOut();
        }

        // logout of Google
        if(loggedIntoGoogle) {
            Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {

                        }
                    });
        }
    }

}
