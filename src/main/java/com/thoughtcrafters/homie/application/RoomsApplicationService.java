package com.thoughtcrafters.homie.application;

import com.thoughtcrafters.homie.domain.appliances.ApplianceId;
import com.thoughtcrafters.homie.domain.lights.Light;
import com.thoughtcrafters.homie.domain.lights.LightsRepository;
import com.thoughtcrafters.homie.domain.rooms.*;

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
        return roomsRepository.getBy(id);
    }

    public Room createRoomWith(String name, List<Point> outline) {
        return roomsRepository.createFrom(name, new Shape(outline));
    }

    public List<Room> getAllRooms() {
        return roomsRepository.getAll();
    }

    // TODO implement error handling when light is already in another room
    // TODO error handling when either room or light is not found
    public void addApplianceToRoom(ApplianceId applianceId, RoomId roomId, Point point) {
        Room room = roomsRepository.getBy(roomId);
        Light light = lightsRepository.getBy(applianceId);
        room.place(light).at(point);
        roomsRepository.save(room);
        lightsRepository.save(light);
    }

    public void removeApplianceFromRoom(ApplianceId applianceId, RoomId roomId) {
        Room room = roomsRepository.getBy(roomId);
        Light light = lightsRepository.getBy(applianceId);
        room.remove(light);
        roomsRepository.save(room);
        lightsRepository.save(light);
    }
}
