package com.irs.server;

/**
 * Represent a User model
 */

public class DBUserModel {
    private int user_id;
    private String email;
    private String api_key;
    private String oauth_provider;
    private int vegetarian;
    private int vegan;
    private int kosher;
    private int gluten_free;
    private int distance;
    private int by_rating;
    private int by_distance;
    private int rating;
    private int number_submitted;
    private int admin;

    // TODO: make 0/1 "ints" into booleans
    public int user_id() {
       return user_id;
    }

    public String email() {
        return email;
    }

    public String api_key() {
        return api_key;
    }

    public String oauth_provider() {
        return oauth_provider;
    }

    public int vegetarian() {
        return vegetarian;
    }

    public int vegan() {
        return vegan;
    }

    public int kosher() {
        return kosher;
    }

    public int gluten_free() {
        return gluten_free;
    }

    public int distance() {
        return distance;
    }

    public int by_rating() {
        return by_rating;
    }

    public int by_distance() {
        return by_distance;
    }

    public int rating() {
        return rating;
    }

    public int number_submitted() {
        return number_submitted;
    }

    public int admin() {
        return admin;
    }
}
