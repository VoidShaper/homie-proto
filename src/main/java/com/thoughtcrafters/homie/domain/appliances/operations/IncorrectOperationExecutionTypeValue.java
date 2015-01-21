package com.thoughtcrafters.homie.domain.appliances.operations;

public class IncorrectOperationExecutionTypeValue extends RuntimeException {
    public IncorrectOperationExecutionTypeValue(Class<String> stringClass) {
        super(String.format("Incorrect type of the value taken from the Operation Execution: %s",
                            stringClass.getSimpleName()));
    }
}
