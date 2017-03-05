package com.irs.server;

/**
 * Represents Preferences Response from the user
 */
public class PreferencesDTO {
    private boolean error;
    private String error_msg;
    private DBUserDTO user;

    public boolean error() {
        return error;
    }

    public String error_msg() {
        return error_msg;
    }

    public DBUserDTO user() {
        return user;
    }
}
