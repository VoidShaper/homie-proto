package com.thoughtcrafters.homie.domain.appliances;

import static java.lang.String.format;

public class ApplianceNotFoundException extends RuntimeException {
    private final ApplianceId applianceId;

    public ApplianceNotFoundException(ApplianceId applianceId, ApplianceType type) {
        super(format("Appliance of type %s with id %s not found.",
                     type, applianceId));
        this.applianceId = applianceId;
    }

    public ApplianceId appliance() {
        return applianceId;
    }
}
