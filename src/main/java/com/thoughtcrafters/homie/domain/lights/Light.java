package com.thoughtcrafters.homie.domain.lights;

import com.thoughtcrafters.homie.domain.Appliance;
import com.thoughtcrafters.homie.domain.ApplianceType;
import com.thoughtcrafters.homie.domain.ApplianceId;
import com.thoughtcrafters.homie.domain.behaviours.SwitchState;
import com.thoughtcrafters.homie.domain.behaviours.Switchable;
import com.thoughtcrafters.homie.domain.rooms.RoomId;

import java.util.Optional;

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
