package com.thoughtcrafters.homie.application;

import com.thoughtcrafters.homie.domain.appliances.Appliance;
import com.thoughtcrafters.homie.domain.appliances.ApplianceCreation;
import com.thoughtcrafters.homie.domain.appliances.ApplianceId;
import com.thoughtcrafters.homie.domain.appliances.ApplianceRepository;
import com.thoughtcrafters.homie.domain.appliances.properties.PropertyUpdate;
import com.thoughtcrafters.homie.domain.rooms.RoomsRepository;

import java.util.List;
import java.util.stream.Collectors;

import static com.thoughtcrafters.homie.application.FullAppliance.richApplianceFor;

public class AppliancesApplicationService {
    private final ApplianceRepository applianceRepository;
    private RoomsRepository roomsRepository;

    public AppliancesApplicationService(ApplianceRepository applianceRepository,
                                        RoomsRepository roomsRepository) {
        this.applianceRepository = applianceRepository;
        this.roomsRepository = roomsRepository;
    }

    public FullAppliance getApplianceWith(ApplianceId id) {
        Appliance appliance = applianceRepository.getBy(id);
        return fullApplianceFrom(appliance);
    }

    public Appliance createApplianceFrom(ApplianceCreation applianceCreation) {
        return applianceRepository.createFrom(applianceCreation);
    }

    public List<FullAppliance> getAllAppliances() {
        return applianceRepository.getAll().stream()
                                  .map(this::fullApplianceFrom)
                                  .collect(Collectors.<FullAppliance>toList());
    }

    public void updateProperty(ApplianceId applianceId, PropertyUpdate propertyUpdate) {
        Appliance appliance = applianceRepository.getBy(applianceId);
        appliance.updatePropertyWith(propertyUpdate);
        applianceRepository.save(appliance);
    }

    private FullAppliance fullApplianceFrom(Appliance appliance) {
        FullAppliance fullAppliance = richApplianceFor(appliance);
        if (fullAppliance.appliance().roomId().isPresent()) {
            String roomName = roomsRepository.getBy(fullAppliance.appliance().roomId().get()).name();
            fullAppliance = fullAppliance.withRoomName(roomName);
        }
        return fullAppliance;
    }
}
