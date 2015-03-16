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
    private Optional<Integer> brightness;

    public Light(ApplianceId id, String name, Optional<RoomId> roomId, boolean dimmable) {
        this(id, name, roomId, SwitchState.OFF, dimmable ? Optional.of(0) : Optional.<Integer>empty());
    }

    public Light(ApplianceId id,
                 String name,
                 Optional<RoomId> roomId,
                 SwitchState switchState,
                 Optional<Integer> brightness) {
        super(id, name, roomId);
        this.switchState = switchState;
        properties.add(new EnumProperty("switchState", switchState, Arrays.asList(SwitchState.values()))
                               .describedAs("If the light is on or off.")
                               .whichIsEditable());
        this.brightness = brightness;
        if (this.brightness.isPresent()) {
            properties.add(new IntegerProperty("brightness", brightness.orElse(0))
                                   .describedAs("How bright the light is.")
                                   .withMinimalValue(0)
                                   .withMaximumValue(100)
                                   .whichIsEditable());
        }
    }

    public SwitchState switchState() {
        return switchState;
    }

    public Optional<Integer> brightness() {
        return brightness;
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
        return new Light(id, name, roomId, switchState, brightness);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Light light = (Light) o;

        if (!id().equals(light.id())) return false;
        if (!name().equals(light.name())) return false;
        if (!roomId().equals(light.roomId())) return false;
        if (!brightness.equals(light.brightness)) return false;
        if (switchState != light.switchState) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id().hashCode();
        result = 31 * result + name().hashCode();
        result = 31 * result + roomId().hashCode();
        result = 31 * result + switchState.hashCode();
        result = 31 * result + brightness.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Light{" +
                "id=" + id() +
                ", name='" + name() + '\'' +
                ", switchState=" + switchState +
                (brightness.isPresent() ? ", brightness=" + brightness.get() : "") +
                (roomId().isPresent() ? ", roomId=" + roomId().get() : "") +
                "}";
    }
}
