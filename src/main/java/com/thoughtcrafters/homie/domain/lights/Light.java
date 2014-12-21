package com.thoughtcrafters.homie.domain.lights;

import com.thoughtcrafters.homie.domain.Appliance;
import com.thoughtcrafters.homie.domain.ApplianceType;
import com.thoughtcrafters.homie.domain.behaviours.SwitchState;
import com.thoughtcrafters.homie.domain.behaviours.Switchable;

public class Light implements Appliance, Switchable {

    private final LightId lightId;
    private final String name;
    private SwitchState switchState;


    public Light(LightId lightId, String name, SwitchState switchState) {
        this.lightId = lightId;
        this.name = name;
        this.switchState = switchState;
    }

    public LightId id() {
        return lightId;
    }

    public String name() {
        return name;
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

        if (!lightId.equals(light.lightId)) return false;
        if (!name.equals(light.name)) return false;
        if (switchState != light.switchState) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = lightId.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + switchState.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Light{" +
                "id=" + lightId +
                ", name='" + name + '\'' +
                ", switchState=" + switchState +
                '}';
    }
}
