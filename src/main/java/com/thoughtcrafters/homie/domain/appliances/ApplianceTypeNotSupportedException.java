package com.thoughtcrafters.homie.domain.appliances;

public class ApplianceTypeNotSupportedException extends RuntimeException {
    public ApplianceTypeNotSupportedException(String message) {
        super(message);
    }
}
