package com.irs.server;

/**
 * Authorization response from backend
 */
public class AuthResponse {
    private boolean error;
    private AuthUserResponse user;

    public boolean error() {
        return error;
    }

    public AuthUserResponse user() {
        return user;
    }
}

