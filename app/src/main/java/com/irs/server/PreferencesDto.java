package com.irs.server;

/**
 * Represents Preferences Response from the user
 */
public class PreferencesDto {
    private boolean error;
    private String error_msg;
    private DBUserDto user;

    public boolean error() {
        return error;
    }

    public String error_msg() {
        return error_msg;
    }

    public DBUserDto user() {
        return user;
    }
}
