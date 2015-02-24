package com.thoughtcrafters.homie.domain.appliances;

import static java.lang.String.format;

public class ApplianceNotFoundException extends RuntimeException {
    private final ApplianceId applianceId;

    public ApplianceNotFoundException(ApplianceId applianceId) {
        super(format("Appliance of with id %s not found.", applianceId));
        this.applianceId = applianceId;
    }

    public ApplianceId appliance() {
        return applianceId;
    }
}
