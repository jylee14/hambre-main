package com.irs.server;

public class DBSetPreferencesModel {
    private String error;
    private String uid;
    private String user;
    private int vegetarian;
    private int vegan;
    private int kosher;
    private int gluten_free;


    public String error(){return error;}
    public String uid(){return uid;}
    public String user(){return user;}
    
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


    @Override
    public String toString() {
        return "DBFoodModel{" +
                "error='" + error + '\'' +
                ", uid='" + uid + '\'' +
                ", user='" + user + '\'' +
                ", vegetarian=" + vegetarian +
                ", vegan=" + vegan +
                ", kosher=" + kosher +
                ", gluten_free=" + gluten_free +
                '}';
    }

}
