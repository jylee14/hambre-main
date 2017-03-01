package com.irs.yelp;

/**
 * Model representing access token object Yelp returns on access token request
 */
public class AccessTokenModel {
    private String access_token;
    private String token_type;
    private int expires_in;

    /**
     * Access token we need to authorize any API call
     * @return
     */
    public String access_token() {
        return access_token;
    }

    /**
     * Token type (always Bearer)
     * @return
     */
    public String token_type() {
        return token_type;
    }

    /**
     * Expiration time of token
     * @return
     */
    public int expires_in() {
        return expires_in;
    }



}
