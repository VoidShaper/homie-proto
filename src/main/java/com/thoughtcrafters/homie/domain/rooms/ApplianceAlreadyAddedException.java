package com.thoughtcrafters.homie.domain.rooms;

import com.thoughtcrafters.homie.domain.DomainException;
import com.thoughtcrafters.homie.domain.ApplianceId;

import static java.lang.String.format;

public class ApplianceAlreadyAddedException extends DomainException {
    public ApplianceAlreadyAddedException(RoomId roomId, ApplianceId applianceId) {
        super(format("Operation Unsuccessful. Appliance %s does not belong to room %s.",
                applianceId.uuid(),
                roomId.uuid()));
    }
}
