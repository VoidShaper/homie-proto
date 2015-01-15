package com.thoughtcrafters.homie.domain.appliances.operations;

import com.thoughtcrafters.homie.domain.appliances.ApplianceId;

import java.util.List;
import java.util.Optional;

public class PropertyUpdateDefinition extends OperationDefinition {
    private final PropertyType propertyType;
    private final Optional<List<String>> enumValues;

    public PropertyUpdateDefinition(ApplianceId applianceId,
                                    String description,
                                    String property,
                                    PropertyType propertyType,
                                    Optional<List<String>> enumValues) {
        super(OperationType.UPDATE, applianceId, description, property);
        this.propertyType = propertyType;
        this.enumValues = enumValues;
    }

    public PropertyType propertyType() {
        return propertyType;
    }

    public Optional<List<String>> enumValues() {
        return enumValues;
    }
}
