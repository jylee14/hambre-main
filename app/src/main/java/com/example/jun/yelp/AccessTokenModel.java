package com.example.jun.yelp;

/**
 * Created by Krikor on 2/9/2017.
 */

public class AccessTokenModel {
    private String access_token;
    private String token_type;
    private int expires_in;

    AccessTokenModel(){

    }

    public String access_token() {
        return access_token;
    }

    public String token_type() {
        return token_type;
    }

    public int expires_in() {
        return expires_in;
    }



}
