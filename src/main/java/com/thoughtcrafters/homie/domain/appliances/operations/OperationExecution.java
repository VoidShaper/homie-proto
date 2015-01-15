package com.thoughtcrafters.homie.domain.appliances.operations;

public class OperationExecution {
    private final OperationType type;
    private final String propertyName;
    private final String operationValue;

    public OperationExecution(OperationType type, String propertyName, String operationValue) {
        this.type = type;
        this.propertyName = propertyName;
        this.operationValue = operationValue;
    }

    public OperationType type() {
        return type;
    }

    public String propertyName() {
        return propertyName;
    }

    public String value() {
        return operationValue;
    }
}
