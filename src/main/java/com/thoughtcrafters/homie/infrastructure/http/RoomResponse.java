package com.thoughtcrafters.homie.infrastructure.http;

import com.thoughtcrafters.homie.domain.appliances.ApplianceId;
import com.thoughtcrafters.homie.domain.rooms.Room;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RoomResponse {
    private UUID id;
    private String name;
    private List<UUID> appliances;

    private RoomResponse(UUID id, String name, List<UUID> appliances) {
        this.id = id;
        this.name = name;
        this.appliances = appliances;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<UUID> getAppliances() {
        return appliances;
    }
    
    public static RoomResponse withoutIdFrom(Room room) {
        return new RoomResponse(
                null,
                room.name(),
                room.appliances().stream().map(ApplianceId::uuid).collect(Collectors.toList()));
    }

    public static RoomResponse withIdFrom(Room room) {
        return new RoomResponse(
                room.id().uuid(),
                room.name(),
                room.appliances().stream().map(ApplianceId::uuid).collect(Collectors.toList()));
    }
}
