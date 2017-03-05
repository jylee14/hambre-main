package com.irs.server;

public class DBCreateTagDTO {
    private boolean error;
    private int id;
    private DBTagDTO tag;

    public boolean error() {
        return error;
    }

    public int id() {
        return id;
    }

    public DBTagDTO tag() {
        return tag;
    }
}
