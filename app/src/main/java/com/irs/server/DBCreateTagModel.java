package com.irs.server;

/**
 * Created by bryle on 3/1/2017.
 */

public class DBCreateTagModel {
    private boolean error;
    private int id;
    private DBTagModel tag;

    public boolean error() {
        return error;
    }

    public int id() {
        return id;
    }

    public DBTagModel tag() {
        return tag;
    }

}
