package com.thoughtcrafters.homie.domain.rooms;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.thoughtcrafters.homie.domain.Appliance;
import com.thoughtcrafters.homie.domain.ApplianceId;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class Room {
    private final RoomId id;
    private final String name;
    private ImmutableSet<ApplianceId> appliances;

    public Room(RoomId id, String name, Set<ApplianceId> appliances) {
        this.id = id;
        this.name = name;
        this.appliances = ImmutableSet.copyOf(appliances);
    }

    public RoomId id() {
        return id;
    }

    public String name() {
        return name;
    }


    public Set<ApplianceId> appliances() {
        return appliances;
    }

    public void addApplicance(Appliance appliance) {
        checkNotNull(appliance);
        if(appliances.contains(appliance.id())) {
            throw new ApplianceAlreadyAddedException(id, appliance.id());
        }

        appliance.placeInTheRoomWith(id);
        appliances = ImmutableSet.<ApplianceId>builder()
                .addAll(appliances)
                .add(appliance.id())
                .build();
    }

    public void removeAppliance(Appliance appliance) {
        checkNotNull(appliance);
        if(!appliances.contains(appliance.id())) {
            throw new ApplianceNotInTheRoomException(id, appliance.id());
        }

        appliance.removeFromTheRoom();
        appliances = Sets.difference(appliances, ImmutableSet.of(appliance.id()))
                .immutableCopy();
    }
}
