package com.irs.main.model;

import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

/**
 * Created by paulosliu on 3/2/17.
 */

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
        if(cbmanager == null) {
            cbmanager = CallbackManager.Factory.create();
        }
        return cbmanager;
    }
}
