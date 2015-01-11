package com.thoughtcrafters.homie.application;

import com.thoughtcrafters.homie.domain.appliances.Appliance;
import com.thoughtcrafters.homie.domain.appliances.ApplianceType;
import com.thoughtcrafters.homie.domain.appliances.lights.Light;
import com.thoughtcrafters.homie.domain.appliances.ApplianceId;
import com.thoughtcrafters.homie.domain.appliances.ApplianceRepository;

import java.util.List;

public class AppliancesApplicationService {
    private final ApplianceRepository applianceRepository;

    public AppliancesApplicationService(ApplianceRepository applianceRepository) {
        this.applianceRepository = applianceRepository;
    }

    public Appliance getApplianceWith(ApplianceId id) {
        return applianceRepository.getBy(id);
    }

    public Appliance createApplianceFrom(ApplianceType applianceType, String name) {
        return applianceRepository.createFrom(applianceType, name);
    }

    public void turnOnTheLightWith(ApplianceId id) {
        perform(Light::turnOn).onLightWith(id);
    }

    public void turnOffTheLightWith(ApplianceId id) {
        perform(Light::turnOff).onLightWith(id);
    }

    private LightActionExecutor perform(LightAction lightAction) {
        return new LightActionExecutor(lightAction, applianceRepository);
    }

    public List<Appliance> getAllAppliances() {
        return applianceRepository.getAll();
    }

    public void replaceProperty(ApplianceId applianceId, String path, String value) {
        Appliance appliance = applianceRepository.getBy(applianceId);
        appliance.updateProperty(path, value);
        applianceRepository.save(appliance);
    }

    private static class LightActionExecutor {
        private final LightAction lightAction;
        private ApplianceRepository applianceRepository;

        private LightActionExecutor(LightAction lightAction, ApplianceRepository applianceRepository) {
            this.lightAction = lightAction;
            this.applianceRepository = applianceRepository;
        }

        public void onLightWith(ApplianceId applianceId) {
            Appliance appliance = applianceRepository.getBy(applianceId);
            lightAction.on((Light) appliance);
            applianceRepository.save(appliance);
        }
    }

    private interface LightAction {
        public void on(Light light);
    }
}
