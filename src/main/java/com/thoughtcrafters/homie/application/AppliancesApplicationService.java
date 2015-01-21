package com.thoughtcrafters.homie.application;

import com.thoughtcrafters.homie.domain.appliances.Appliance;
import com.thoughtcrafters.homie.domain.appliances.ApplianceType;
import com.thoughtcrafters.homie.domain.appliances.ApplianceId;
import com.thoughtcrafters.homie.domain.appliances.ApplianceRepository;
import com.thoughtcrafters.homie.domain.appliances.operations.OperationExecution;

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

    public List<Appliance> getAllAppliances() {
        return applianceRepository.getAll();
    }

    public void performOperation(ApplianceId applianceId, OperationExecution operationExecution) {
        Appliance appliance = applianceRepository.getBy(applianceId);
        appliance.perform(operationExecution);
        applianceRepository.save(appliance);
    }
}