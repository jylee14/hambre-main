package com.irs.server;

/**
 * Authorization response from backend
 */
public class AuthDTO {
    private boolean error;
    private AuthUserDTO user;

    public boolean error() {
        return error;
    }

    public AuthUserDTO user() {
        return user;
    }
}

