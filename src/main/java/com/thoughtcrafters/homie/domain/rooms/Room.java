package com.thoughtcrafters.homie.domain.rooms;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.thoughtcrafters.homie.domain.appliances.Appliance;
import com.thoughtcrafters.homie.domain.appliances.ApplianceId;
import sun.security.provider.SHA;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class Room {
    private final RoomId id;
    private final String name;
    private final Shape shape;
    private ImmutableSet<ApplianceId> appliances;

    public Room(RoomId id, String name, Shape shape, Set<ApplianceId> appliances) {
        this.id = id;
        this.name = name;
        this.shape = shape;
        this.appliances = ImmutableSet.copyOf(appliances);
    }

    public RoomId id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Shape shape() {
        return shape;
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
