package com.thoughtcrafters.homie.infrastructure.http;

import com.thoughtcrafters.homie.domain.behaviours.SwitchState;
import com.thoughtcrafters.homie.domain.lights.Light;
import com.thoughtcrafters.homie.domain.rooms.RoomId;

import java.util.UUID;

public class LightResponse {

    private String name;
    private SwitchState switchState;
    private UUID roomId;

    private LightResponse(String name, SwitchState switchState, UUID roomId) {
        this.name = name;
        this.switchState = switchState;
        this.roomId = roomId;
    }

    public static LightResponse from(Light light) {
        return new LightResponse(light.name(),
                                 light.switchState(),
                                 light.roomId().isPresent() ?
                                         light.roomId().get().uuid() : null);
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

    public UUID getRoomId() {
        return roomId;
    }

    public void setRoomId(UUID roomId) {
        this.roomId = roomId;
    }
}
