package com.thoughtcrafters.homie.domain.appliances.operations;

public class Operation {
    private final String description;

    public Operation(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
