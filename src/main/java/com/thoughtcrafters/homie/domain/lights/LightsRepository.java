package com.thoughtcrafters.homie.domain.lights;

import com.thoughtcrafters.homie.domain.behaviours.SwitchState;

import java.util.Optional;

public interface LightsRepository {
    Optional<Light> getBy(LightId lightId);

    Light createFrom(String name, SwitchState initialState);
}
