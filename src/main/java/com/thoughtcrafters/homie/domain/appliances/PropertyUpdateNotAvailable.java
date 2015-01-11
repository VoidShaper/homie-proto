package com.thoughtcrafters.homie.domain.appliances;

public class PropertyUpdateNotAvailable extends RuntimeException {
    public PropertyUpdateNotAvailable(ApplianceId applianceId, String propertyName, String propertyValue) {
        super(String.format("Appliance %s does not support changing of the property %s to %s",
                            applianceId, propertyName, propertyValue));
    }
}
