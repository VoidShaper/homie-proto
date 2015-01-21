package com.thoughtcrafters.homie.application;

import com.thoughtcrafters.homie.domain.appliances.ApplianceId;
import com.thoughtcrafters.homie.domain.rooms.RoomId;

public class RemoveApplianceFromTheRoomTask implements RoomTask {
    private RoomsApplicationService roomsApplicationService;
    private final ApplianceId applianceId;

    public RemoveApplianceFromTheRoomTask(RoomsApplicationService roomsApplicationService,
                                          ApplianceId applianceId) {
        this.roomsApplicationService = roomsApplicationService;
        this.applianceId = applianceId;
    }

    @Override
    public void performTaskOn(RoomId roomId) {
        roomsApplicationService.removeApplianceFromRoom(applianceId, roomId);
    }

}
