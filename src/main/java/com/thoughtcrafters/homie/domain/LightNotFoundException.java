package com.thoughtcrafters.homie.domain;

import java.util.UUID;

import static java.lang.String.format;

public class LightNotFoundException extends RuntimeException {
    private UUID lightId;

    public LightNotFoundException(UUID lightId) {
        super(format("Light with id %s not found.", lightId));
        this.lightId = lightId;
    }

    public UUID lightId() {
        return lightId;
    }
}
