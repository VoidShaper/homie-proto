package com.thoughtcrafters.homie.domain.rooms;

import java.util.UUID;

public class RoomId {
    private final UUID uuid;

    public RoomId(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID uuid() {
        return uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoomId roomId = (RoomId) o;

        if (!uuid.equals(roomId.uuid)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String toString() {
        return "RoomId{" +
                "uuid=" + uuid +
                '}';
    }
}
