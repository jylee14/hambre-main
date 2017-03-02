package com.irs.server;

public class DBUserToFoodModel {
    private int user_id;
    private int food_id;
    private int liked;
    private int disliked;


    public int food_id() {
        return food_id;
    }

    public int user_id(){return user_id;}

    public int liked(){return liked;}

    public int disliked() {return disliked;}

    @Override
    public String toString() {
        return "DBFoodModel{" +
                "food_id='" + food_id +
                ", user_id='" + user_id +
                "liked='" + liked +
                ", disliked='" + disliked +
                '}';
    }
}
