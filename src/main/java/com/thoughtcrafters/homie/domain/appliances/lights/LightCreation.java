package com.thoughtcrafters.homie.domain.appliances.lights;

import com.thoughtcrafters.homie.domain.appliances.ApplianceCreation;
import com.thoughtcrafters.homie.domain.appliances.ApplianceType;

public class LightCreation extends ApplianceCreation {

    private final boolean dimmable;

    public LightCreation(String name, boolean dimmable) {
        super(name, ApplianceType.LIGHT);
        this.dimmable = dimmable;
    }

    public boolean dimmable() {
        return dimmable;
    }
}
