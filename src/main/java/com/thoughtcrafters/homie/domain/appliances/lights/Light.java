package com.thoughtcrafters.homie.domain.appliances.lights;

import com.thoughtcrafters.homie.domain.appliances.Appliance;
import com.thoughtcrafters.homie.domain.appliances.ApplianceId;
import com.thoughtcrafters.homie.domain.appliances.ApplianceType;
import com.thoughtcrafters.homie.domain.appliances.operations.Operation;
import com.thoughtcrafters.homie.domain.appliances.operations.PropertyType;
import com.thoughtcrafters.homie.domain.appliances.operations.PropertyUpdateDefinition;
import com.thoughtcrafters.homie.domain.appliances.operations.PropertyUpdateNotAvailableException;
import com.thoughtcrafters.homie.domain.behaviours.SwitchState;
import com.thoughtcrafters.homie.domain.rooms.RoomId;

import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;

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
        operations.add(new Operation(
                new PropertyUpdateDefinition(id,
                                             "turn on or off",
                                             "switchState",
                                             PropertyType.ENUM,
                                             Optional.of(newArrayList("ON", "OFF"))),
                execution -> {
                    if (execution.propertyName().equals("switchState") &&
                            (execution.value().equals("ON"))
                            || execution.value().equals("OFF")) {
                        this.switchState = SwitchState.valueOf(execution.value());
                        return;
                    }
                    throw new PropertyUpdateNotAvailableException(id, execution.propertyName(), execution.value());
                }));
    }

    public SwitchState switchState() {
        return switchState;
    }

    @Override
    public ApplianceType type() {
        return ApplianceType.LIGHT;
    }

    public SwitchState state() {
        return this.switchState;
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
        if (switchState != light.switchState) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id().hashCode();
        result = 31 * result + name().hashCode();
        result = 31 * result + switchState.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Light{" +
                "id=" + id() +
                ", name='" + name() + '\'' +
                ", switchState=" + switchState +
                '}';
    }
}
