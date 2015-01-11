package com.thoughtcrafters.homie.domain.appliances;

import com.thoughtcrafters.homie.domain.appliances.Appliance;
import com.thoughtcrafters.homie.domain.appliances.ApplianceId;
import com.thoughtcrafters.homie.domain.appliances.ApplianceType;

import java.util.List;

public interface ApplianceRepository {
    Appliance getBy(ApplianceId applianceId);

    Appliance createFrom(ApplianceType applianceType, String name);

    void save(Appliance appliance);

    List<Appliance> getAll();
}
