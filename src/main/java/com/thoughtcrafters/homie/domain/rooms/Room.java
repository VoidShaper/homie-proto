package com.thoughtcrafters.homie.domain.rooms;

import com.google.common.collect.ImmutableMap;
import com.thoughtcrafters.homie.domain.appliances.Appliance;
import com.thoughtcrafters.homie.domain.appliances.ApplianceId;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class Room {
    private final RoomId id;
    private final String name;
    private final Shape shape;
    private Map<ApplianceId, Point> appliances;

    public Room(RoomId id, String name, Shape shape) {
        this(id, name, shape, new HashMap<>());
    }
    public Room(RoomId id, String name, Shape shape, Map<ApplianceId, Point> appliances) {
        this.id = id;
        this.name = name;
        this.shape = shape;
        this.appliances = new LinkedHashMap<>(appliances);
    }

    public RoomId id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Shape shape() {
        return shape;
    }

    public Map<ApplianceId, Point> appliances() {
        return ImmutableMap.copyOf(appliances);
    }

    public Placement place(Appliance appliance) {
        checkNotNull(appliance);
        if (appliances.containsKey(appliance.id())) {
            // we remove the old position
            appliances.remove(appliance.id());
        }
        return new Placement(appliance);
    }

    public void remove(Appliance appliance) {
        checkNotNull(appliance);
        if (!appliances.containsKey(appliance.id())) {
            throw new ApplianceNotInTheRoomException(id, appliance.id());
        }

        appliance.removeFromTheRoom();
        appliances.remove(appliance.id());
    }

    public class Placement {

        private Appliance appliance;

        protected Placement(Appliance appliance) {
            this.appliance = appliance;
        }

        public void at(Point point) {
            appliance.placeInTheRoomWith(id);
            appliances.put(appliance.id(), point);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Room room = (Room) o;

        if (!appliances.equals(room.appliances)) return false;
        if (!id.equals(room.id)) return false;
        if (!name.equals(room.name)) return false;
        if (!shape.equals(room.shape)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + shape.hashCode();
        result = 31 * result + appliances.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", shape=" + shape +
                ", appliances=" + appliances +
                '}';
    }
}
