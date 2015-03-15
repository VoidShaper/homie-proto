package com.thoughtcrafters.homie.domain.appliances.properties;

public class IntegerProperty extends ApplianceProperty<Integer> {

    private final int min;
    private final int max;

    public IntegerProperty(String name, int value) {
        super(name, value, PropertyType.INTEGER);
        this.min = Integer.MIN_VALUE;
        this.max = Integer.MAX_VALUE;
    }

    private IntegerProperty(String name,
                            Integer value,
                            PropertyType type,
                            String description,
                            boolean editable,
                            String unit,
                            int min,
                            int max) {
        super(name, value, type, description, editable, unit);
        this.min = min;
        this.max = max;
    }

    public int min() {
        return min;
    }

    public int max() {
        return max;
    }

    public IntegerProperty describedAs(String description) {
        return new IntegerProperty(name(), value(), type(), description, editable(), unit(), min, max);
    }

    public IntegerProperty whichIsEditable() {
        return new IntegerProperty(name(), value(), type(), description(), true, unit(), min, max);
    }

    public IntegerProperty withValuesInUnit(String unit) {
        return new IntegerProperty(name(), value(), type(), description(), editable(), unit, min, max);
    }

    public IntegerProperty withMinimalValue(int min) {
        return new IntegerProperty(name(), value(), type(), description(), editable(), unit(), min, max);
    }

    public IntegerProperty withMaximumValue(int max) {
        return new IntegerProperty(name(), value(), type(), description(), editable(), unit(), min, max);
    }
}
