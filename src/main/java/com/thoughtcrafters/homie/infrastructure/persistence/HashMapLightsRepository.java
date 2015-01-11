package com.thoughtcrafters.homie.infrastructure.persistence;

import com.google.common.collect.FluentIterable;
import com.thoughtcrafters.homie.domain.appliances.Appliance;
import com.thoughtcrafters.homie.domain.appliances.ApplianceNotFoundException;
import com.thoughtcrafters.homie.domain.appliances.ApplianceType;
import com.thoughtcrafters.homie.domain.behaviours.SwitchState;
import com.thoughtcrafters.homie.domain.appliances.lights.Light;
import com.thoughtcrafters.homie.domain.appliances.ApplianceId;
import com.thoughtcrafters.homie.domain.appliances.lights.LightsRepository;
import com.thoughtcrafters.homie.domain.rooms.RoomId;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

public class HashMapLightsRepository implements LightsRepository {

    private Map<ApplianceId, Light> lights = new HashMap<>();

    @Override
    public Light getBy(ApplianceId applianceId) {
        if(lights.containsKey(applianceId)) {
            return (Light) copyOf(lights.get(applianceId));
        }
        throw new ApplianceNotFoundException(applianceId, ApplianceType.LIGHT);
    }

    @Override
    public Light createFrom(String name, SwitchState initialState) {
        checkNotNull(name);
        checkNotNull(initialState);
        ApplianceId applianceId = new ApplianceId(UUID.randomUUID());
        Light light = new Light(applianceId, name, Optional.<RoomId>empty(), initialState);
        lights.put(applianceId, light);
        return (Light) copyOf(light);
    }

    @Override
    public void save(Light light) {
        checkNotNull(light);
        if(!lights.containsKey(light.id())) {
            throw new ApplianceNotFoundException(light.id(), ApplianceType.LIGHT);
        }
        lights.put(light.id(), (Light) copyOf(light));
    }

    @Override
    public List<Light> getAll() {
        return FluentIterable.from(lights.values())
                .transform(this::copyOf)
                .toList();
    }

    public void clearAll() {
        lights.clear();
    }

    private Light copyOf(Light light) {
        return new Light(light.id(), light.name(), light.roomId(), light.switchState());
    }
}
