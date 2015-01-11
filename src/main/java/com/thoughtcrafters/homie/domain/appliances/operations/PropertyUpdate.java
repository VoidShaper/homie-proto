package com.thoughtcrafters.homie.domain.appliances.operations;

import com.thoughtcrafters.homie.domain.appliances.ApplianceId;

import java.util.List;
import java.util.Optional;

public class PropertyUpdate extends Operation {
    private final ApplianceId applianceId;
    private final String property;
    private final PropertyType propertyType;
    private final Optional<List<String>> enumValues;

    public PropertyUpdate(ApplianceId applianceId,
                          String description,
                          String property,
                          PropertyType propertyType,
                          Optional<List<String>> enumValues) {
        super(description);
        this.applianceId = applianceId;
        this.property = property;
        this.propertyType = propertyType;
        this.enumValues = enumValues;
    }

    public ApplianceId applianceId() {
        return applianceId;
    }

    public String property() {
        return property;
    }

    public PropertyType propertyType() {
        return propertyType;
    }

    public Optional<List<String>> enumValues() {
        return enumValues;
    }
}
