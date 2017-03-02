package com.irs.server;

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
