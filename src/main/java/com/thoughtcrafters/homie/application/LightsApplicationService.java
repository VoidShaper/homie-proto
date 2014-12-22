package com.thoughtcrafters.homie.application;

import com.thoughtcrafters.homie.domain.LightNotFoundException;
import com.thoughtcrafters.homie.domain.behaviours.SwitchState;
import com.thoughtcrafters.homie.domain.lights.Light;
import com.thoughtcrafters.homie.domain.ApplianceId;
import com.thoughtcrafters.homie.domain.lights.LightsRepository;

import java.util.Optional;

public class LightsApplicationService {
    private final LightsRepository lightsRepository;

    public LightsApplicationService(LightsRepository lightsRepository) {
        this.lightsRepository = lightsRepository;
    }

    public Light getTheLightWith(ApplianceId id) {
        Optional<Light> light = lightsRepository.getBy(id);
        if(light.isPresent()) {
            return light.get();
        }
        throw new LightNotFoundException(id);
    }

    public Light createLightFrom(String name, SwitchState initialState) {
        return lightsRepository.createFrom(name, initialState);
    }

    public void turnOnTheLightWith(ApplianceId id) {
        perform(Light::turnOn).onLightWith(id);
    }

    public void turnOffTheLightWith(ApplianceId id) {
        perform(Light::turnOff).onLightWith(id);
    }

    private LightActionExecutor perform(LightAction lightAction) {
        return new LightActionExecutor(lightAction, lightsRepository);
    }

    private static class LightActionExecutor {
        private final LightAction lightAction;
        private LightsRepository lightsRepository;

        private LightActionExecutor(LightAction lightAction, LightsRepository lightsRepository) {
            this.lightAction = lightAction;
            this.lightsRepository = lightsRepository;
        }

        public void onLightWith(ApplianceId applianceId) {
            Optional<Light> light = lightsRepository.getBy(applianceId);
            if(light.isPresent()) {
                lightAction.on(light.get());
                lightsRepository.save(light.get());
            }
            throw new LightNotFoundException(applianceId);
        }
    }

    private interface LightAction {
        public void on(Light light);
    }
}
