package com.thoughtcrafters.homie.domain;

import static java.lang.String.format;

public class LightNotFoundException extends RuntimeException {
    private ApplianceId lightId;

    public LightNotFoundException(ApplianceId lightId) {
        super(format("Light with id %s not found.", lightId));
        this.lightId = lightId;
    }

    public ApplianceId lightId() {
        return lightId;
    }
}
