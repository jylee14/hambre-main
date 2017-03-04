package com.irs.server;

import com.irs.main.model.UserModel;

/**
 * Represents Preferences Response from the user
 */
public class PreferencesModel {
    private boolean error;
    private String error_msg;
    private DBUserModel user;

    public boolean error() {
        return error;
    }

    public String error_msg() {
        return error_msg;
    }

    public DBUserModel user() {
        return user;
    }
}
