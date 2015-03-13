package com.thoughtcrafters.homie.domain.appliances.properties;

public class ApplianceProperty<T> {
    private final String name;
    private final T value;
    private final PropertyType type;

    private String description = "";
    private boolean editable = false;
    private String unit = "";

    public ApplianceProperty(String name, T value, PropertyType type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    protected ApplianceProperty(String name,
                                T value,
                                PropertyType type,
                                String description,
                                boolean editable,
                                String unit) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.description = description;
        this.editable = editable;
        this.unit = unit;
    }

    public String name() {
        return name;
    }

    public T value() {
        return value;
    }

    public PropertyType type() {
        return type;
    }

    public String description() {
        return description;
    }

    public boolean editable() {
        return editable;
    }

    public String unit() {
        return unit;
    }

    public ApplianceProperty<T> describedAs(String description) {
        return new ApplianceProperty<T>(name, value, type, description, editable, unit);
    }

    public ApplianceProperty<T> whichIsEditable() {
        return new ApplianceProperty<T>(name, value, type, description, true, unit);
    }

    public ApplianceProperty<T> withValuesInUnit(String unit) {
        return new ApplianceProperty<T>(name, value, type, description, editable, unit);
    }
}
