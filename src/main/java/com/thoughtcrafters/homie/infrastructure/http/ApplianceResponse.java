package com.thoughtcrafters.homie.infrastructure.http;

import com.google.common.collect.FluentIterable;
import com.thoughtcrafters.homie.domain.appliances.Appliance;
import com.thoughtcrafters.homie.domain.appliances.ApplianceId;
import com.thoughtcrafters.homie.domain.appliances.ApplianceType;
import com.thoughtcrafters.homie.domain.appliances.Operation;
import com.thoughtcrafters.homie.domain.behaviours.SwitchState;
import com.thoughtcrafters.homie.domain.lights.Light;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ApplianceResponse {

    private UUID id;
    private String name;
    private ApplianceType type;
    private SwitchState switchState;
    private UUID roomId;
    private List<OperationBody> operations;

    private ApplianceResponse(UUID id,
                              String name,
                              ApplianceType type,
                              SwitchState switchState,
                              UUID roomId,
                              List<OperationBody> operations) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.switchState = switchState;
        this.roomId = roomId;
        this.operations = operations;
    }

    public static ApplianceResponse withoutIdFrom(Light light) {
        return new ApplianceResponse(null,
                                     light.name(),
                                     light.type(),
                                     light.switchState(),
                                     light.roomId().isPresent() ?
                                             light.roomId().get().uuid() : null,
                                     operationBodiesFrom(light.id(), light.operations()));
    }

    public static ApplianceResponse withIdFrom(Light light) {
        return new ApplianceResponse(light.id().uuid(),
                                     light.name(),
                                     light.type(),
                                     light.switchState(),
                                     light.roomId().isPresent() ?
                                             light.roomId().get().uuid() : null,
                                     operationBodiesFrom(light.id(), light.operations()));
    }

    private static List<OperationBody> operationBodiesFrom(ApplianceId applianceId,
                                                           Set<Operation> operations) {
        return FluentIterable.from(operations)
                             .transform(x -> OperationBody.from(applianceId, x))
                             .toList();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ApplianceType getType() {
        return type;
    }

    public void setType(ApplianceType type) {
        this.type = type;
    }

    public SwitchState getSwitchState() {
        return switchState;
    }

    public void setSwitchState(SwitchState switchState) {
        this.switchState = switchState;
    }

    public UUID getRoomId() {
        return roomId;
    }

    public List<OperationBody> getOperations() {
        return operations;
    }

    public void setOperations(List<OperationBody> operations) {
        this.operations = operations;
    }

    public void setRoomId(UUID roomId) {
        this.roomId = roomId;
    }
}
