package com.thoughtcrafters.homie.domain.appliances.properties;

public class PropertyUpdate<T> {
    private final String propertyName;
    private final T value;

    public PropertyUpdate(String propertyName, T value) {
        this.propertyName = propertyName;
        this.value = value;
    }

    public String name() {
        return propertyName;
    }

    public T value() {
        return value;
    }

    public String valueAsString() {
        return String.valueOf(value);
    }
}
