package com.thoughtcrafters.homie.infrastructure.persistence;

import com.google.common.collect.FluentIterable;
import com.thoughtcrafters.homie.domain.appliances.Appliance;
import com.thoughtcrafters.homie.domain.appliances.ApplianceNotFoundException;
import com.thoughtcrafters.homie.domain.appliances.ApplianceType;
import com.thoughtcrafters.homie.domain.appliances.lights.Light;
import com.thoughtcrafters.homie.domain.appliances.ApplianceId;
import com.thoughtcrafters.homie.domain.appliances.ApplianceRepository;
import com.thoughtcrafters.homie.domain.rooms.RoomId;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

public class HashMapApplianceRepository implements ApplianceRepository {

    private Map<ApplianceId, Appliance> appliances = new HashMap<>();

    @Override
    public Appliance getBy(ApplianceId applianceId) {
        if(appliances.containsKey(applianceId)) {
            return appliances.get(applianceId).copy();
        }
        throw new ApplianceNotFoundException(applianceId, ApplianceType.LIGHT);
    }

    @Override
    public Appliance createFrom(ApplianceType applianceType, String name) {
        checkNotNull(name);
        ApplianceId applianceId = new ApplianceId(UUID.randomUUID());
        Appliance appliance = null;
        switch (applianceType) {
            case LIGHT:
                appliance = new Light(applianceId, name, Optional.<RoomId>empty());
                break;
            default:
                throw new RuntimeException("Canot create appliance of type" + applianceType);
        }
        appliances.put(applianceId, appliance);
        return appliance.copy();
    }

    @Override
    public void save(Appliance appliance) {
        checkNotNull(appliance);
        if(!appliances.containsKey(appliance.id())) {
            throw new ApplianceNotFoundException(appliance.id(), ApplianceType.LIGHT);
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
