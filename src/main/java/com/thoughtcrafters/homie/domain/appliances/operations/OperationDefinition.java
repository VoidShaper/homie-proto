package com.thoughtcrafters.homie.domain.appliances.operations;

import com.thoughtcrafters.homie.domain.appliances.ApplianceId;

public class OperationDefinition {
    private final OperationType operationType;
    protected final String property;
    private final ApplianceId applianceId;
    private final String description;

    public OperationDefinition(OperationType operationType,
                               ApplianceId applianceId,
                               String description,
                               String property) {
        this.operationType = operationType;
        this.applianceId = applianceId;
        this.description = description;
        this.property = property;
    }

    public ApplianceId applianceId() {
        return applianceId;
    }

    public String property() {
        return property;
    }

    public String description() {
        return description;
    }

    public OperationType type() {
        return operationType;
    }
}
