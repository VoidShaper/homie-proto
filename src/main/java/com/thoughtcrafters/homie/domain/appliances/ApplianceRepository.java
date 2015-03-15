package com.thoughtcrafters.homie.domain.appliances;

import java.util.List;

public interface ApplianceRepository {
    Appliance getBy(ApplianceId applianceId);

    Appliance createFrom(ApplianceCreation applianceCreation);

    void save(Appliance appliance);

    List<Appliance> getAll();
}
