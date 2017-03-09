package com.irs.server;

import com.irs.main.DietType;
import com.irs.yelp.SortType;

/**
 * Represent a User model
 */

public class DBUserDto {
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

    public boolean vegetarian() {
        return (vegetarian == 1);
    }

    public boolean vegan() {
        return (vegan == 1);
    }

    public boolean kosher() {
        return (kosher == 1);
    }

    public boolean gluten_free() {
        return (gluten_free == 1);
    }

    public int distance() {
        return distance;
    }

    public boolean by_rating() {
        return (by_rating == 1);
    }

    public boolean by_distance() {
        return (by_distance == 1);
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

    public DietType getDietType() {
        if (vegetarian()) {
            return DietType.Vegetarian;
        } else if (vegan()) {
            return DietType.Vegan;
        } else if (kosher()) {
            return DietType.Kosher;
        } else if (gluten_free()) {
            return DietType.GlutenFree;
        }
        return DietType.None;
    }

    public SortType getSortType() {
        if (by_distance()) {
            return SortType.distance;
        } else {
            return SortType.rating;
        }
    }
}
