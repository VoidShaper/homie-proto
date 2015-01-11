package com.thoughtcrafters.homie.application;

import com.thoughtcrafters.homie.domain.appliances.Appliance;
import com.thoughtcrafters.homie.domain.appliances.ApplianceId;
import com.thoughtcrafters.homie.domain.appliances.ApplianceRepository;
import com.thoughtcrafters.homie.domain.rooms.*;

import java.util.List;

public class RoomsApplicationService {

    private final RoomsRepository roomsRepository;
    private final ApplianceRepository applianceRepository;

    public RoomsApplicationService(RoomsRepository roomsRepository,
                                   ApplianceRepository applianceRepository) {
        this.roomsRepository = roomsRepository;
        this.applianceRepository = applianceRepository;
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
        Appliance appliance = applianceRepository.getBy(applianceId);
        room.place(appliance).at(point);
        roomsRepository.save(room);
        applianceRepository.save(appliance);
    }

    public void removeApplianceFromRoom(ApplianceId applianceId, RoomId roomId) {
        Room room = roomsRepository.getBy(roomId);
        Appliance appliance = applianceRepository.getBy(applianceId);
        room.remove(appliance);
        roomsRepository.save(room);
        applianceRepository.save(appliance);
    }
}
