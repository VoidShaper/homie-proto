package com.thoughtcrafters.homie.domain.appliances.lights;

import com.thoughtcrafters.homie.domain.appliances.ApplianceId;
import com.thoughtcrafters.homie.domain.behaviours.SwitchState;

import java.util.List;

public interface LightsRepository {
    Light getBy(ApplianceId applianceId);

    Light createFrom(String name, SwitchState initialState);

    void save(Light light);

    List<Light> getAll();
}
