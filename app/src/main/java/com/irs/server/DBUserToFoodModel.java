package com.irs.server;

/**
 * Created by bryle on 3/1/2017.
 */

public class DBUserToFoodModel {
    private int user_id;
    private int food_id;





    public int food_id() {
        return food_id;
    }

    public int user_id(){return user_id;}


    @Override
    public String toString() {
        return "DBFoodModel{" +
                "food_id='" + food_id +
                ", user_id='" + user_id +
                '}';
    }

}
