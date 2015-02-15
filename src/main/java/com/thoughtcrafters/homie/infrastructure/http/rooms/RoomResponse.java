package com.thoughtcrafters.homie.infrastructure.http.rooms;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.thoughtcrafters.homie.domain.appliances.ApplianceId;
import com.thoughtcrafters.homie.domain.rooms.Point;
import com.thoughtcrafters.homie.domain.rooms.Room;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class RoomResponse {
    private UUID id;
    private String name;
    private Map<UUID, Map<String, PointBody>> appliances;
    private List<PointBody> shape;

    private RoomResponse(UUID id, String name, Map<UUID, Map<String, PointBody>> appliances, List<PointBody> shape) {
        this.id = id;
        this.name = name;
        this.appliances = appliances;
        this.shape = shape;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Map<UUID, Map<String, PointBody>> getAppliances() {
        return appliances;
    }

    public List<PointBody> getShape() {
        return shape;
    }

    public static RoomResponse withoutIdFrom(Room room) {
        return new RoomResponse(
                null,
                room.name(),
                map(room.appliances()),
                pointBodyFrom(room));
    }

    public static RoomResponse withIdFrom(Room room) {
        return new RoomResponse(
                room.id().uuid(),
                room.name(),
                map(room.appliances()),
                pointBodyFrom(room));
    }

    private static Map<UUID, Map<String, PointBody>> map(Map<ApplianceId, Point> appliances1) {
        return appliances1.entrySet().stream().collect(
                Collectors.toMap(e -> e.getKey().uuid(),
                                 e -> ImmutableMap.of("point", PointBody.from(e.getValue()))));
    }

    private static ImmutableList<PointBody> pointBodyFrom(Room room) {
        return FluentIterable.from(room.shape().outline()).transform(PointBody::from).toList();
    }
}
