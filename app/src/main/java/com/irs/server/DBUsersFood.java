package com.irs.server;

public class DBUsersFood {
    private int user_id;
    private int food_id;
    private int liked;
    private int unliked;


    public int user_id(){return user_id;}

    public int food_id(){return food_id;}

    public int liked(){return liked;}

    public int unliked(){return unliked;}

    public String toString() {
        return "DBUsersFood{" +
                "user_id='" + user_id +
                ", food_id='" + food_id +
                ", liked=" + liked +
                ", unliked=" + unliked +
                '}';
    }

}
