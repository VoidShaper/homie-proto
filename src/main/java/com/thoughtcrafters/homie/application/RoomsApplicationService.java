package com.thoughtcrafters.homie.application;

import com.thoughtcrafters.homie.domain.appliances.ApplianceId;
import com.thoughtcrafters.homie.domain.lights.Light;
import com.thoughtcrafters.homie.domain.lights.LightsRepository;
import com.thoughtcrafters.homie.domain.rooms.Room;
import com.thoughtcrafters.homie.domain.rooms.RoomId;
import com.thoughtcrafters.homie.domain.rooms.RoomNotFoundException;
import com.thoughtcrafters.homie.domain.rooms.RoomsRepository;

import java.util.List;
import java.util.Optional;

public class RoomsApplicationService {

    private final RoomsRepository roomsRepository;
    private final LightsRepository lightsRepository;

    public RoomsApplicationService(RoomsRepository roomsRepository,
                                   LightsRepository lightsRepository) {
        this.roomsRepository = roomsRepository;
        this.lightsRepository = lightsRepository;
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

    public List<Room> getAllRooms() {
        return roomsRepository.getAll();
    }

    public void addApplianceToRoom(ApplianceId applianceId, RoomId roomId) {
        Optional<Room> room = roomsRepository.getBy(roomId);
        Optional<Light> light = lightsRepository.getBy(applianceId);
        if(room.isPresent() && light.isPresent()) {
            room.get().addApplicance(light.get());
            roomsRepository.save(room.get());
            lightsRepository.save(light.get());
        }
    }

    public void removeApplianceFromRoom(ApplianceId applianceId, RoomId roomId) {
        Optional<Room> room = roomsRepository.getBy(roomId);
        Optional<Light> light = lightsRepository.getBy(applianceId);
        if(room.isPresent() && light.isPresent()) {
            room.get().removeAppliance(light.get());
            roomsRepository.save(room.get());
            lightsRepository.save(light.get());
        }
    }
}
