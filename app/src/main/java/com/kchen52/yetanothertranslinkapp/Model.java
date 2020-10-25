package com.kchen52.yetanothertranslinkapp;

/**
 * Created by Kevin on 2016-09-11.
 */
public class Model {
    String name;
    int value;

    public Model(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }
    public int getValue() {
        return value;
    }
}

