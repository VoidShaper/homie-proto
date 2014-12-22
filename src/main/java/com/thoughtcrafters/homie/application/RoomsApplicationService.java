package com.thoughtcrafters.homie.application;

import com.thoughtcrafters.homie.domain.rooms.Room;
import com.thoughtcrafters.homie.domain.rooms.RoomId;
import com.thoughtcrafters.homie.domain.rooms.RoomNotFoundException;
import com.thoughtcrafters.homie.domain.rooms.RoomsRepository;

import java.util.Optional;

public class RoomsApplicationService {

    private final RoomsRepository roomsRepository;

    public RoomsApplicationService(RoomsRepository roomsRepository) {
        this.roomsRepository = roomsRepository;
    }

    public Room getTheRoomWith(RoomId id) {
        Optional<Room> room = roomsRepository.getBy(id);
        if(room.isPresent()) {
            return room.get();
        }
        throw new RoomNotFoundException(id);
    }

    public Room createRoomWith(String name) {
        return roomsRepository.createFrom(name);
    }
}
