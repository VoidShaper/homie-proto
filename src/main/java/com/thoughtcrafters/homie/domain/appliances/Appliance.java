package com.thoughtcrafters.homie.domain.appliances;

import com.google.common.collect.ImmutableSet;
import com.thoughtcrafters.homie.domain.appliances.properties.ApplianceProperty;
import com.thoughtcrafters.homie.domain.appliances.properties.PropertyUpdate;
import com.thoughtcrafters.homie.domain.rooms.RoomId;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class Appliance {
    protected final ApplianceId id;
    protected final String name;
    protected Optional<RoomId> roomId;
    protected Set<ApplianceProperty> properties = new HashSet<>();

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

    public Optional<RoomId> roomId() {
        return roomId;
    }

    public Set<ApplianceProperty> properties() {
        return ImmutableSet.copyOf(properties);
    }

    public abstract void updatePropertyWith(PropertyUpdate propertyUpdate);

    public abstract ApplianceState state();

    public abstract Appliance copy();
}
