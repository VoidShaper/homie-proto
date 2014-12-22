package com.thoughtcrafters.homie.domain.rooms;

import com.thoughtcrafters.homie.domain.DomainException;
import com.thoughtcrafters.homie.domain.appliances.ApplianceId;

import static java.lang.String.format;

public class ApplianceNotInTheRoomException extends DomainException {
    public ApplianceNotInTheRoomException(RoomId roomId, ApplianceId applianceId) {
        super(format("Operation Unsuccessful. Appliance %s does not belong to room %s.",
                applianceId.uuid(),
                roomId.uuid()));
    }
}
