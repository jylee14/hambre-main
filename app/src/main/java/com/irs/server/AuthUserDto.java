package com.irs.server;

public class AuthUserDTO {
    private String email;
    private String oauth_provider;
    private String api_key;

    public String email() {
        return email;
    }

    public String oauth_provider() {
        return oauth_provider;
    }

    public String api_key() {
        return api_key;
    }
}
