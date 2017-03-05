package com.irs.server;

public class DBLinkTagToFoodDto {
    private String error;
    private int food_id;
    private int tag_id;

    public String error(){return error;}
    public int food_id(){return food_id;}
    public int tag_id(){return tag_id;}


    public String toString() {
        return "DBFoodDto{" +
                "error='" + error + '\'' +
                ", tag_id='" + tag_id + '\'' +
                ", food_id='" + food_id + '\'' +
                '}';
    }
}
