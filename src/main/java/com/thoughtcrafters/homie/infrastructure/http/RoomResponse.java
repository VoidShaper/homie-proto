package com.thoughtcrafters.homie.infrastructure.http;

import com.thoughtcrafters.homie.domain.appliances.ApplianceId;
import com.thoughtcrafters.homie.domain.rooms.Room;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RoomResponse {
    public String name;
    public List<UUID> appliances;

    private RoomResponse(String name, List<UUID> appliances) {
        this.name = name;
        this.appliances = appliances;
    }

    public String getName() {
        return name;
    }

    public List<UUID> getAppliances() {
        return appliances;
    }
    
    public static RoomResponse from(Room room) {
        return new RoomResponse(room.name(),
                room.appliances().stream().map(ApplianceId::uuid).collect(Collectors.toList()));
    }
}
