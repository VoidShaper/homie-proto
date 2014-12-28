package com.thoughtcrafters.homie.infrastructure.persistence;

import com.google.common.collect.FluentIterable;
import com.thoughtcrafters.homie.domain.rooms.Room;
import com.thoughtcrafters.homie.domain.rooms.RoomId;
import com.thoughtcrafters.homie.domain.rooms.RoomNotFoundException;
import com.thoughtcrafters.homie.domain.rooms.RoomsRepository;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptySet;

public class HashMapRoomsRepository implements RoomsRepository {

    private Map<RoomId, Room> rooms = new HashMap<>();

    @Override
    public Optional<Room> getBy(RoomId applianceId) {
        return rooms.containsKey(applianceId) ?
                Optional.of(copyOf(rooms.get(applianceId)))
                : Optional.<Room>empty();
    }

    @Override
    public Room createFrom(String name) {
        checkNotNull(name);

        RoomId roomId = new RoomId(UUID.randomUUID());
        Room room = new Room(roomId, name, emptySet());
        rooms.put(roomId, room);
        return copyOf(room);
    }

    @Override
    public void save(Room room) {
        checkNotNull(room);
        if(!rooms.containsKey(room.id())) {
            throw new RoomNotFoundException(room.id());
        }
        rooms.put(room.id(), copyOf(room));
    }

    @Override
    public List<Room> getAll() {
        return FluentIterable.from(rooms.values())
                             .transform(this::copyOf)
                             .toList();
    }

    private Room copyOf(Room room) {
        return new Room(room.id(), room.name(), room.appliances());
    }

    public void clearAll() {
        rooms.clear();
    }
}
