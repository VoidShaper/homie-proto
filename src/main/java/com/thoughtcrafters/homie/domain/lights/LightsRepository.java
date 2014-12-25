package com.thoughtcrafters.homie.domain.lights;

import com.thoughtcrafters.homie.domain.appliances.ApplianceId;
import com.thoughtcrafters.homie.domain.behaviours.SwitchState;

import java.util.List;
import java.util.Optional;

public interface LightsRepository {
    Optional<Light> getBy(ApplianceId applianceId);

    Light createFrom(String name, SwitchState initialState);

    void save(Light light);

    List<Light> getAll();
}
