package com.thoughtcrafters.homie.application;

import com.thoughtcrafters.homie.domain.appliances.ApplianceId;
import com.thoughtcrafters.homie.domain.rooms.Point;
import com.thoughtcrafters.homie.domain.rooms.RoomId;

public class PlaceApplianceInTheRoomTask implements RoomTask {
    private RoomsApplicationService roomsApplicationService;
    private final ApplianceId applianceId;
    private final Point point;

    public PlaceApplianceInTheRoomTask(RoomsApplicationService roomsApplicationService,
                                       ApplianceId applianceId,
                                       Point point) {
        this.roomsApplicationService = roomsApplicationService;
        this.applianceId = applianceId;
        this.point = point;
    }

    @Override
    public void performTaskOn(RoomId roomId) {
        roomsApplicationService.addApplianceToRoom(applianceId, roomId, point);
    }

}
