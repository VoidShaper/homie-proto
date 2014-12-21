package com.thoughtcrafters.homie.infrastructure.persistence;

import com.thoughtcrafters.homie.domain.behaviours.SwitchState;
import com.thoughtcrafters.homie.domain.lights.Light;
import com.thoughtcrafters.homie.domain.lights.LightId;
import com.thoughtcrafters.homie.domain.lights.LightsRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class HashMapLightsRepository implements LightsRepository {

    private Map<LightId, Light> lights = new HashMap<LightId, Light>();

    @Override
    public Optional<Light> getBy(LightId lightId) {
        return lights.containsKey(lightId) ?
                Optional.of(copyOf(lights.get(lightId)))
                : Optional.<Light>empty();
    }

    @Override
    public Light createFrom(String name, SwitchState initialState) {
        LightId lightId = new LightId(UUID.randomUUID());
        Light light = new Light(lightId, name, initialState);
        lights.put(lightId, light);
        return copyOf(light);
    }

    private Light copyOf(Light light) {
        return new Light(light.id(), light.name(), light.switchState());
    }
}
