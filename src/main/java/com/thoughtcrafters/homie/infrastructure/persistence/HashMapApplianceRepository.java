package com.thoughtcrafters.homie.infrastructure.persistence;

import com.google.common.collect.FluentIterable;
import com.thoughtcrafters.homie.domain.appliances.Appliance;
import com.thoughtcrafters.homie.domain.appliances.ApplianceCreation;
import com.thoughtcrafters.homie.domain.appliances.ApplianceId;
import com.thoughtcrafters.homie.domain.appliances.ApplianceNotFoundException;
import com.thoughtcrafters.homie.domain.appliances.ApplianceRepository;
import com.thoughtcrafters.homie.domain.appliances.lights.Light;
import com.thoughtcrafters.homie.domain.rooms.RoomId;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

public class HashMapApplianceRepository implements ApplianceRepository {

    private Map<ApplianceId, Appliance> appliances = new HashMap<>();

    @Override
    public Appliance getBy(ApplianceId applianceId) {
        if(appliances.containsKey(applianceId)) {
            return appliances.get(applianceId).copy();
        }
        throw new ApplianceNotFoundException(applianceId);
    }

    @Override
    public Appliance createFrom(ApplianceCreation creation) {
        ApplianceId applianceId = new ApplianceId(UUID.randomUUID());
        Appliance appliance;
        switch (creation.type()) {
            case LIGHT:
                appliance = new Light(applianceId, creation.name(), Optional.<RoomId>empty(), false);
                break;
            default:
                throw new RuntimeException("Cannot create appliance of type" + creation.type());
        }
        appliances.put(applianceId, appliance);
        return appliance.copy();
    }

    @Override
    public void save(Appliance appliance) {
        checkNotNull(appliance);
        if(!appliances.containsKey(appliance.id())) {
            throw new ApplianceNotFoundException(appliance.id());
        }
        appliances.put(appliance.id(), appliance.copy());
    }

    @Override
    public List<Appliance> getAll() {
        return FluentIterable.from(appliances.values())
                .transform(x -> x.copy())
                .toList();
    }

    public void clearAll() {
        appliances.clear();
    }
}
