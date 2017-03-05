package com.irs.main.model;

import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

/**
 * Created by paulosliu on 3/2/17.
 */

public class LoginModel {
    private static GoogleSignInOptions gso;
    private static CallbackManager cbmanager;

    public LoginModel() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("58151517395-7u4o0o77s2ff8dtbvio1v2tab2snf116.apps.googleusercontent.com")
                .requestEmail()
                .build();

        cbmanager = CallbackManager.Factory.create();

    }

    public GoogleSignInOptions getGoogleSignInOptions() {
        return gso;
    }

    public CallbackManager getFBManager(){
        return cbmanager;
    }
}
