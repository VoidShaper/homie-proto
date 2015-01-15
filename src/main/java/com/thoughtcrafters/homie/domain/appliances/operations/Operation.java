package com.thoughtcrafters.homie.domain.appliances.operations;

public class Operation {
    private final OperationDefinition definition;
    private final OperationHandler handler;

    public Operation(OperationDefinition definition, OperationHandler handler) {
        this.definition = definition;
        this.handler = handler;
    }

    public void perform(OperationExecution operationExecution) {
        handler.perform(operationExecution);
    }

    public OperationDefinition definition() {
        return definition;
    }

    public String propertyName() {
        return definition.property();
    }

    public boolean matches(OperationExecution execution) {
        return execution.propertyName().equals(propertyName())
            && execution.type().equals(definition.type());
    }
}
