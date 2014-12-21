package com.thoughtcrafters.homie.infrastructure.http;

import com.thoughtcrafters.homie.domain.behaviours.SwitchState;
import com.thoughtcrafters.homie.domain.lights.Light;

public class LightResponse {

    private String name;
    private SwitchState switchState;

    private LightResponse(String name, SwitchState switchState) {
        this.name = name;
        this.switchState = switchState;
    }

    public static LightResponse from(Light light) {
        return new LightResponse(light.name(), light.switchState());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SwitchState getSwitchState() {
        return switchState;
    }

    public void setSwitchState(SwitchState switchState) {
        this.switchState = switchState;
    }
}
