package com.thoughtcrafters.homie.domain.appliances.lights;

import com.thoughtcrafters.homie.domain.appliances.Appliance;
import com.thoughtcrafters.homie.domain.appliances.ApplianceId;
import com.thoughtcrafters.homie.domain.appliances.ApplianceState;
import com.thoughtcrafters.homie.domain.appliances.ApplianceType;
import com.thoughtcrafters.homie.domain.appliances.properties.IntegerProperty;
import com.thoughtcrafters.homie.domain.appliances.properties.PropertyUpdateNotAvailableException;
import com.thoughtcrafters.homie.domain.appliances.properties.EnumProperty;
import com.thoughtcrafters.homie.domain.appliances.properties.PropertyUpdate;
import com.thoughtcrafters.homie.domain.behaviours.SwitchState;
import com.thoughtcrafters.homie.domain.rooms.RoomId;

import java.util.Arrays;
import java.util.Optional;

public class Light extends Appliance {

    private SwitchState switchState;
    private int brightness;
    private boolean dimmable;

    public Light(ApplianceId id, String name, Optional<RoomId> roomId, boolean dimmable) {
        this(id, name, roomId, SwitchState.OFF, dimmable);
    }

    public Light(ApplianceId id,
                 String name,
                 Optional<RoomId> roomId,
                 SwitchState switchState,
                 boolean dimmable) {
        super(id, name, roomId);
        this.switchState = switchState;
        this.dimmable = dimmable;
        properties.add(new EnumProperty("switchState", switchState, Arrays.asList(SwitchState.values()))
                               .describedAs("If the light is on or off.")
                               .whichIsEditable());
        if (dimmable) {
            this.brightness = 0;
            properties.add(new IntegerProperty("brightness", brightness)
                                   .describedAs("How bright the light is.")
                                   .withMinimalValue(0)
                                   .withMaximumValue(100)
                                   .whichIsEditable());
        }
    }

    public SwitchState switchState() {
        return switchState;
    }

    public boolean dimmable() {
        return dimmable;
    }

    @Override
    public ApplianceType type() {
        return ApplianceType.LIGHT;
    }

    @Override
    public void updatePropertyWith(PropertyUpdate propertyUpdate) {
        if (propertyUpdate.name().equals("switchState") &&
                (propertyUpdate.valueAsString().equals("ON"))
                || propertyUpdate.valueAsString().equals("OFF")) {

            this.switchState = SwitchState.valueOf(propertyUpdate.valueAsString());
            return;
        }
        throw new PropertyUpdateNotAvailableException(id,
                                                      propertyUpdate.name(),
                                                      propertyUpdate.valueAsString());
    }

    @Override
    public ApplianceState state() {
        return switchState == SwitchState.OFF ? ApplianceState.IDLE : ApplianceState.WORKING;
    }

    @Override
    public Appliance copy() {
        return new Light(id, name, roomId, switchState, dimmable);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Light light = (Light) o;

        if (!id().equals(light.id())) return false;
        if (!name().equals(light.name())) return false;
        if (!roomId().equals(light.roomId())) return false;
        if (switchState != light.switchState) return false;
        if (dimmable != light.dimmable) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id().hashCode();
        result = 31 * result + name().hashCode();
        result = 31 * result + roomId().hashCode();
        result = 31 * result + switchState.hashCode();
        result = 31 * result + (dimmable ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Light{" +
                "id=" + id() +
                ", name='" + name() + '\'' +
                ", switchState=" + switchState +
                ", dimmable=" + dimmable +
                (roomId().isPresent() ? ", roomId=" + roomId().get() : "") +
                "}";
    }
}
