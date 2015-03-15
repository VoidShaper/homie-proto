package com.thoughtcrafters.homie.domain.appliances;

public abstract class ApplianceCreation {
    private final String name;
    private final ApplianceType type;

    public ApplianceCreation(String name, ApplianceType type) {
        this.name = name;
        this.type = type;
    }

    public String name() {
        return name;
    }

    public ApplianceType type() {
        return type;
    }
}
