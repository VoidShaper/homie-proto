package com.thoughtcrafters.homie.infrastructure.http;

import com.thoughtcrafters.homie.domain.appliances.ApplianceType;
import com.thoughtcrafters.homie.domain.behaviours.SwitchState;
import com.thoughtcrafters.homie.domain.lights.Light;

import java.util.UUID;

public class ApplianceResponse {

    private UUID id;
    private String name;
    private ApplianceType type;
    private SwitchState switchState;
    private UUID roomId;

    private ApplianceResponse(UUID id,
                              String name,
                              ApplianceType type,
                              SwitchState switchState,
                              UUID roomId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.switchState = switchState;
        this.roomId = roomId;
    }

    public static ApplianceResponse withoutIdFrom(Light light) {
        return new ApplianceResponse(null,
                                     light.name(),
                                     light.type(),
                                     light.switchState(),
                                     light.roomId().isPresent() ?
                                             light.roomId().get().uuid() : null);
    }

    public static ApplianceResponse withIdFrom(Light light) {
        return new ApplianceResponse(light.id().uuid(),
                                     light.name(),
                                     light.type(),
                                     light.switchState(),
                                     light.roomId().isPresent() ?
                                             light.roomId().get().uuid() : null);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ApplianceType getType() {
        return type;
    }

    public void setType(ApplianceType type) {
        this.type = type;
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
