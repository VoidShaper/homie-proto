package com.thoughtcrafters.homie.domain.rooms;

import java.util.Optional;

public interface RoomsRepository {
    Optional<Room> getBy(RoomId applianceId);

    Room createFrom(String name);
}
