package com.thoughtcrafters.homie.application;

import com.thoughtcrafters.homie.domain.LightNotFoundException;
import com.thoughtcrafters.homie.domain.behaviours.SwitchState;
import com.thoughtcrafters.homie.domain.lights.Light;
import com.thoughtcrafters.homie.domain.lights.LightId;
import com.thoughtcrafters.homie.domain.lights.LightsRepository;

import java.util.Optional;
import java.util.UUID;

public class LightsApplicationService {
    private final LightsRepository lightsRepository;

    public LightsApplicationService(LightsRepository lightsRepository) {
        this.lightsRepository = lightsRepository;
    }

    public Light getLightBy(UUID lightId) {
        Optional<Light> light = lightsRepository.getBy(new LightId(lightId));
        if(light.isPresent()) {
            return light.get();
        }
        throw new LightNotFoundException(lightId);
    }

    public Light createLight(String name, SwitchState initialState) {
        return lightsRepository.createFrom(name, initialState);
    }
}
