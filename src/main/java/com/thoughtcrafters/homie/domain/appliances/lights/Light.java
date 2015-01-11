package com.thoughtcrafters.homie.domain.appliances.lights;

import com.google.common.collect.ImmutableSet;
import com.thoughtcrafters.homie.domain.appliances.Appliance;
import com.thoughtcrafters.homie.domain.appliances.ApplianceType;
import com.thoughtcrafters.homie.domain.appliances.ApplianceId;
import com.thoughtcrafters.homie.domain.appliances.operations.Operation;
import com.thoughtcrafters.homie.domain.appliances.operations.PropertyType;
import com.thoughtcrafters.homie.domain.appliances.operations.PropertyUpdate;
import com.thoughtcrafters.homie.domain.behaviours.SwitchState;
import com.thoughtcrafters.homie.domain.behaviours.Switchable;
import com.thoughtcrafters.homie.domain.rooms.RoomId;

import java.util.Optional;
import java.util.Set;

import static com.google.common.collect.Lists.newArrayList;

public class Light extends Appliance implements  Switchable {

    private SwitchState switchState;

    public Light(ApplianceId id,
                 String name,
                 Optional<RoomId> roomId,
                 SwitchState switchState) {
        super(id, name, roomId);
        this.switchState = switchState;
    }

    public SwitchState switchState() {
        return switchState;
    }

    @Override
    public ApplianceType type() {
        return ApplianceType.LIGHT;
    }

    @Override
    public Set<Operation> operations() {
        return ImmutableSet.of(new PropertyUpdate(id,
                                                  "turn on or off",
                                                  "switchState",
                                                  PropertyType.ENUM,
                                                  Optional.of(newArrayList("ON", "OFF"))));
    }

    @Override
    public void turnOn() {
        if(switchState == SwitchState.OFF) {
            switchState = SwitchState.ON;
        }
    }

    @Override
    public void turnOff() {
        if(switchState == SwitchState.ON) {
            switchState = SwitchState.OFF;
        }
    }

    @Override
    public SwitchState state() {
        return this.switchState;
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
