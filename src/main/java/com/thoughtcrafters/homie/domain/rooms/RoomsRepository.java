package com.thoughtcrafters.homie.domain.rooms;

import java.util.List;
import java.util.Optional;

public interface RoomsRepository {
    Optional<Room> getBy(RoomId applianceId);

    Room createFrom(String name);

    void save(Room room);

    List<Room> getAll();
}
