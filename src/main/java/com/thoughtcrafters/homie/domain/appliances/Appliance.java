package com.thoughtcrafters.homie.domain.appliances;

import com.thoughtcrafters.homie.domain.appliances.operations.Operation;
import com.thoughtcrafters.homie.domain.appliances.operations.PropertyUpdateNotAvailable;
import com.thoughtcrafters.homie.domain.rooms.RoomId;

import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class Appliance {
    protected final ApplianceId id;
    protected final String name;
    protected Optional<RoomId> roomId;

    public Appliance(ApplianceId id, String name, Optional<RoomId> roomId) {
        this.id = id;
        this.name = name;
        this.roomId = roomId;
    }

    public ApplianceId id() {
        return id;
    }

    public String name() {
        return name;
    }

    public void placeInTheRoomWith(RoomId roomId) {
        checkNotNull(roomId);
        this.roomId = Optional.of(roomId);
    }

    public void removeFromTheRoom() {
        this.roomId = Optional.empty();
    }

    public abstract ApplianceType type();

    public abstract Set<Operation> operations();

    public Optional<RoomId> roomId() {
        return roomId;
    }

    public <T> void updateProperty(String propertyName, T propertyValue) {
        throw new PropertyUpdateNotAvailable(id, propertyName, propertyValue.toString());
    }

    public abstract Appliance copy();
}
