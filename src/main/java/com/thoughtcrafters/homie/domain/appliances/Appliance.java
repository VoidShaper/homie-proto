package com.thoughtcrafters.homie.domain.appliances;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.thoughtcrafters.homie.domain.appliances.operations.Operation;
import com.thoughtcrafters.homie.domain.appliances.operations.OperationDefinition;
import com.thoughtcrafters.homie.domain.appliances.operations.OperationExecution;
import com.thoughtcrafters.homie.domain.rooms.RoomId;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class Appliance {
    protected final ApplianceId id;
    protected final String name;
    protected Optional<RoomId> roomId;
    protected Set<Operation> operations;

    public Appliance(ApplianceId id, String name, Optional<RoomId> roomId) {
        this.id = id;
        this.name = name;
        this.roomId = roomId;
        this.operations = new HashSet<>();
    }

    public ApplianceId id() {
        return id;
    }

    public String name() {
        return name;
    }

    public void placeInTheRoomWith(RoomId roomId) {
        checkNotNull(roomId);
        this.roomId = Optional.of(roomId);
    }

    public void removeFromTheRoom() {
        this.roomId = Optional.empty();
    }

    public abstract ApplianceType type();

    public Optional<RoomId> roomId() {
        return roomId;
    }

    public Set<OperationDefinition> definedOperations() {
        return ImmutableSet.copyOf(FluentIterable.from(operations)
                                                 .transform(Operation::definition)
                                                 .toSet());
    }

    public void perform(OperationExecution operationExecution) {
        operationMatching(operationExecution).perform(operationExecution);
    }

    private Operation operationMatching(OperationExecution execution) {
        for(Operation operation : operations) {
            if(operation.matches(execution)) {
                return operation;
            }
        }
        throw new NoMatchingOperationForExecutionException(id, execution);
    }

    public abstract ApplianceState state();

    public abstract Appliance copy();
}
