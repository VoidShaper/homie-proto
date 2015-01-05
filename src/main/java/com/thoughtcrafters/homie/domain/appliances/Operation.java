package com.thoughtcrafters.homie.domain.appliances;

public enum Operation {
    TURN_ON("turn on", "on"),
    TURN_OFF("turn off", "off");

    private final String description;
    private final String path;

    Operation(String description, String path) {
        this.description = description;
        this.path = path;
    }

    public String description() {
        return description;
    }

    public String path() {
        return path;
    }
}
