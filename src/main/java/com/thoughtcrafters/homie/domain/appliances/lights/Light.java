package com.thoughtcrafters.homie.domain.appliances.lights;

import com.thoughtcrafters.homie.domain.appliances.Appliance;
import com.thoughtcrafters.homie.domain.appliances.ApplianceId;
import com.thoughtcrafters.homie.domain.appliances.ApplianceState;
import com.thoughtcrafters.homie.domain.appliances.ApplianceType;
import com.thoughtcrafters.homie.domain.appliances.properties.PropertyUpdateNotAvailableException;
import com.thoughtcrafters.homie.domain.appliances.properties.EnumProperty;
import com.thoughtcrafters.homie.domain.appliances.properties.PropertyUpdate;
import com.thoughtcrafters.homie.domain.behaviours.SwitchState;
import com.thoughtcrafters.homie.domain.rooms.RoomId;

import java.util.Arrays;
import java.util.Optional;

public class Light extends Appliance {

    private SwitchState switchState;

    public Light(ApplianceId id, String name, Optional<RoomId> roomId) {
        this(id, name, roomId, SwitchState.OFF);
    }

    public Light(ApplianceId id,
                 String name,
                 Optional<RoomId> roomId,
                 SwitchState switchState) {
        super(id, name, roomId);
        this.switchState = switchState;
        properties.add(new EnumProperty("switchState", switchState, Arrays.asList(SwitchState.values()))
                               .describedAs("If the light is on or off.")
                               .whichIsEditable());
    }

    public SwitchState switchState() {
        return switchState;
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
        return new Light(id, name, roomId, switchState);
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

        return true;
    }

    @Override
    public int hashCode() {
        int result = id().hashCode();
        result = 31 * result + name().hashCode();
        result = 31 * result + roomId().hashCode();
        result = 31 * result + switchState.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Light{" +
                "id=" + id() +
                ", name='" + name() + '\'' +
                ", switchState=" + switchState +
                (roomId().isPresent() ? ", roomId=" + roomId().get() : "") +
                "}";
    }
}
