package com.thoughtcrafters.homie.infrastructure.persistence;

import com.google.common.collect.FluentIterable;
import com.thoughtcrafters.homie.domain.rooms.*;

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
    public Room createFrom(String name, Shape shape) {
        checkNotNull(name);

        RoomId roomId = new RoomId(UUID.randomUUID());
        Room room = new Room(roomId, name, shape);
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
        // TODO this copy is very shallow - but does the job for now
        return new Room(room.id(), room.name(), room.shape(), room.appliances());
    }

    public void clearAll() {
        rooms.clear();
    }
}
