package com.thoughtcrafters.homie.application;

import com.thoughtcrafters.homie.domain.appliances.Appliance;

import java.util.Optional;

public class FullAppliance {
    private final Appliance appliance;
    private final Optional<String> roomName;

    private FullAppliance(Appliance appliance, Optional<String> roomName) {
        this.appliance = appliance;
        this.roomName = roomName;
    }

    public Appliance appliance() {
        return appliance;
    }

    public Optional<String> roomName() {
        return roomName;
    }

    public FullAppliance withRoomName(String roomName) {
        return new FullAppliance(appliance, Optional.of(roomName));
    }

    public static FullAppliance richApplianceFor(Appliance appliance) {
        return  new FullAppliance(appliance, Optional.empty());
    }
}
