package com.thoughtcrafters.homie.application;

import com.thoughtcrafters.homie.domain.rooms.RoomId;

public interface RoomTask {
    void performTaskOn(RoomId roomId);
}
