package com.thoughtcrafters.homie.domain.appliances.properties;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class EnumProperty extends ApplianceProperty<Enum> {

    private final List<Enum> availableValues;

    public EnumProperty(String name, Enum value, List<Enum> availableValues) {
        super(name, value, PropertyType.ENUM);
        this.availableValues = ImmutableList.copyOf(availableValues);
    }

    private EnumProperty(String name,
                         Enum value,
                         PropertyType type,
                         String description,
                         boolean editable,
                         String unit,
                         List<Enum> availableValues) {
        super(name, value, type, description, editable, unit);
        this.availableValues = availableValues;
    }

    public List<Enum> availableValues() {
        return availableValues;
    }

    public EnumProperty describedAs(String description) {
        return new EnumProperty(name(), value(), type(), description, editable(), unit(), availableValues);
    }

    public EnumProperty whichIsEditable() {
        return new EnumProperty(name(), value(), type(), description(), true, unit(), availableValues);
    }

    public EnumProperty withValuesInUnit(String unit) {
        return new EnumProperty(name(), value(), type(), description(), editable(), unit, availableValues);
    }
}
