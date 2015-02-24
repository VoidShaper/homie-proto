package com.thoughtcrafters.homie.domain.rooms;

import static java.lang.String.format;

public class RoomNotFoundException extends RuntimeException {
    private final RoomId roomId;

    public RoomNotFoundException(RoomId roomId) {
        super(format("Room with id %s not found.", roomId));
        this.roomId = roomId;
    }

    public RoomId roomId() {
        return this.roomId;
    }
}
