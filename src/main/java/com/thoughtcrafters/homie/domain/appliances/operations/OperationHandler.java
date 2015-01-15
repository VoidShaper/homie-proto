package com.thoughtcrafters.homie.domain.appliances.operations;

import com.sun.corba.se.spi.orb.Operation;

public interface OperationHandler {
    public void perform(OperationExecution operationExecution);
}
