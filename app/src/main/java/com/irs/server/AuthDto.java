package com.irs.server;

/**
 * Authorization response from backend
 */
public class AuthDto {
    private boolean error;
    private AuthUserDto user;

    public boolean error() {
        return error;
    }

    public AuthUserDto user() {
        return user;
    }
}

