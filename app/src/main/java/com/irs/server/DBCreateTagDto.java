package com.irs.server;

public class DBCreateTagDto {
    private boolean error;
    private int id;
    private DBTagDto tag;

    public boolean error() {
        return error;
    }

    public int id() {
        return id;
    }

    public DBTagDto tag() {
        return tag;
    }
}
