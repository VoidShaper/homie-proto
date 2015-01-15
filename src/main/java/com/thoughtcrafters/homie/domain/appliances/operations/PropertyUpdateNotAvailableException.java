package com.thoughtcrafters.homie.domain.appliances.operations;

import com.thoughtcrafters.homie.domain.appliances.ApplianceId;

public class PropertyUpdateNotAvailableException extends RuntimeException {
    public PropertyUpdateNotAvailableException(ApplianceId applianceId, String propertyName, String propertyValue) {
        super(String.format("Appliance %s does not support changing of the property %s to %s",
                            applianceId, propertyName, propertyValue));
    }
}
