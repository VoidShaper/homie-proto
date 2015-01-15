package com.thoughtcrafters.homie.domain.appliances;

import com.thoughtcrafters.homie.domain.appliances.operations.OperationExecution;

public class NoMatchingOperationForExecutionException extends RuntimeException {
    public NoMatchingOperationForExecutionException(ApplianceId id, OperationExecution execution) {
        super(String.format("There is no operation on a device %s that matched %s",
                            id, execution));
    }
}
