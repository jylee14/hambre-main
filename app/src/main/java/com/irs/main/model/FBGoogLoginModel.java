package com.irs.main.model;

import android.os.StrictMode;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.irs.main.controller.LandingController;
import com.irs.server.AuthDto;
import com.irs.server.ServerApi;

public class FBGoogLoginModel {
    private static FBGoogLoginModel instance;
    private GoogleSignInOptions gso;
    private GoogleApiClient googleApiClient;
    private CallbackManager cbmanager;
    private LandingController landing;

    private boolean loggedIntoFacebook;
    private boolean loggedIntoGoogle;

    private FBGoogLoginModel() {
        loggedIntoFacebook = false;
        loggedIntoGoogle = false;
    }

    public static FBGoogLoginModel getInstance() {
        if (instance == null) {
            instance = new FBGoogLoginModel();
        }
        return instance;
    }

    public boolean isLoggedIntoFacebook() {
        return loggedIntoFacebook;
    }

    public boolean isLoggedIntoGoogle() {
        return loggedIntoGoogle;
    }

    public boolean isLoggedIn() {
        return loggedIntoFacebook || loggedIntoGoogle;
    }

    public GoogleSignInOptions getGoogleSignInOptions() {
        if (gso == null) {
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("58151517395-7u4o0o77s2ff8dtbvio1v2tab2snf116.apps.googleusercontent.com")
                    .requestEmail()
                    .build();
        }
        return gso;
    }

    public void setGoogleApiClient(GoogleApiClient client) {
        googleApiClient = client;
    }

    public void setLanding(LandingController lc) {
        landing = lc;
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    public CallbackManager getFBManager() {
        if (cbmanager == null) {
            cbmanager = CallbackManager.Factory.create();
        }
        return cbmanager;
    }

    /**
     * Returns if the user was logged in previously. Also logs in to our server
     * if the user had logged in
     *
     * @return if the user was logged in previously, null if we could not login
     */
    public Boolean loggedInPreviously() {
        // if facebook is logged in
        if (AccessToken.getCurrentAccessToken() != null) {
            boolean success = useFacebookToLogin();
            if (!success) {
                // can't login because there is no connection
                return null;
                // System.exit(-1);
            }
            return true;
        }

        // else return whether google was logged in
        return googleLoggedIn();

    }

    /**
     * Gets whether the user logged in via google previously
     * <p>
     * "Adapted" from
     * http://stackoverflow.com/questions/35195673/check-whether-the-user-is-already-logged-in-using-auth-googlesigninapi
     *
     * @return Whether the user logged in via google previously
     */
    private boolean googleLoggedIn() {
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()) {
            useGoogleToLogin(opr.get());
            loggedIntoGoogle = true;
            loggedIntoFacebook = false;
            return true;
        }
        return false;
    }

    /**
     * Login with facebook
     * @return false if login failed
     */
    public boolean useFacebookToLogin() {
        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy);
        // Strict mode forces this code to run synchronously. That way when view is switched, all this data is loaded


        try {
            AuthDto response = ServerApi.getInstance().authServer(AccessToken.getCurrentAccessToken());
            System.out.println("GOT DATA FROM SERVER: " + response.user());

            UserModel.getInstance().loginAccount(response.user().api_key());
            System.out.println("LOGGED IN TO SERVER");

            loggedIntoFacebook = true;
            return true;
        } catch (NullPointerException e){
            System.err.println("something was null");
            e.printStackTrace();
            return false;
            //System.exit(-1);
        }
    }

    public void useGoogleToLogin(GoogleSignInResult result) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            GoogleSignInAccount acct = result.getSignInAccount();
            System.out.println("SIGNED IN GOOGLE: " + acct.getEmail());

            AuthDto response = ServerApi.getInstance().authServer(acct);
            System.out.println("GOT DATA FROM SERVER: " + response.user());

            UserModel.getInstance().loginAccount(response.user().api_key());
            System.out.println("LOGGED IN TO SERVER");

            loggedIntoGoogle = true;
        } catch (NullPointerException e){
            System.err.println("one of the fields was null");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Changes the values when the user logs out
     */
    public void loggedOut() {
        loggedIntoFacebook = false;
        loggedIntoGoogle = false;

        landing.changeLoginScreenBeforeLogin();
    }
}
