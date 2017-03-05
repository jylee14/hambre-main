package com.irs.server;

/**
 * Model which represents a food_id tag_id pair
 */
public class DBFoodTagModel {
    private int food_id;
    private int tag_id;

    public int food_id() {
        return food_id;
    }

    public int tag_id() {
        return tag_id;
    }
}
