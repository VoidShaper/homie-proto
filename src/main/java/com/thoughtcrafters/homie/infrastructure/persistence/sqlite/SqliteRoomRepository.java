package com.thoughtcrafters.homie.infrastructure.persistence.sqlite;

import com.google.common.collect.ImmutableList;
import com.thoughtcrafters.homie.domain.appliances.ApplianceId;
import com.thoughtcrafters.homie.domain.rooms.Point;
import com.thoughtcrafters.homie.domain.rooms.Room;
import com.thoughtcrafters.homie.domain.rooms.RoomId;
import com.thoughtcrafters.homie.domain.rooms.RoomNotFoundException;
import com.thoughtcrafters.homie.domain.rooms.RoomsRepository;
import com.thoughtcrafters.homie.domain.rooms.Shape;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SqliteRoomRepository implements RoomsRepository {
    private DBI sqliteDbi;

    public SqliteRoomRepository(DBI sqliteDbi) {
        this.sqliteDbi = sqliteDbi;
    }

    @Override
    public Room getBy(RoomId roomId) {
        try (Handle handle = sqliteDbi.open()) {
            List<Map<String, Object>> roomResults = handle
                    .select("select * from room where room_id = ?", roomId.uuid());
            if (roomResults.isEmpty()) {
                throw new RoomNotFoundException(roomId);
            }
            Map<String, Object> roomResult = roomResults.get(0);

            return roomFromResult(handle, roomResult);
        }
    }

    @Override
    public Room createFrom(String name, Shape shape) {

        RoomId roomId = new RoomId(UUID.randomUUID());

        try (Handle handle = sqliteDbi.open()) {
            handle.execute("insert into room(room_id, name) values (?, ?)",
                           roomId.uuid(), name);
            for (int i = 0; i < shape.outline().size(); ++i) {
                Point point = shape.outline().get(i);
                handle.execute("insert into shape_point(room_id, position, x, y) values (?, ?, ?, ?)",
                               roomId.uuid(), i, point.x(), point.y());
            }
        }
        return new Room(roomId, name, shape);
    }

    @Override
    public void save(Room room) {
        try (Handle handle = sqliteDbi.open()) {
            handle.execute("delete from room_appliance where room_id = ?", room.id().uuid());
            for(Map.Entry<ApplianceId, Point> placedAppliance : room.appliances().entrySet()) {
                handle.execute("insert into room_appliance(room_id, appliance_id, x, y) values (?, ?, ?, ?)",
                               room.id().uuid(),
                               placedAppliance.getKey().uuid(),
                               placedAppliance.getValue().x(),
                               placedAppliance.getValue().y());
            }
        }
    }

    @Override
    public List<Room> getAll() {
        ImmutableList.Builder<Room> roomsBuilder = ImmutableList.builder();

        try (Handle handle = sqliteDbi.open()) {
            List<Map<String, Object>> roomResults = handle
                    .select("select * from room");

            for (Map<String, Object> roomResult : roomResults) {
                roomsBuilder.add(roomFromResult(handle, roomResult));
            }
        }
        return roomsBuilder.build();
    }

    private Room roomFromResult(Handle handle, Map<String, Object> roomResult) {
        String roomIdResult = (String) roomResult.get("room_id");

        List<Map<String, Object>> pointResults = handle
                .select("select * from shape_point where room_id = ?", roomIdResult);

        Point[] points = new Point[pointResults.size()];

        for (Map<String, Object> pointResult : pointResults) {
            Integer position = (int) pointResult.get("position");
            Double x = (double) pointResult.get("x");
            Double y = (double) pointResult.get("y");
            points[position] = new Point(x, y);
        }


        List<Map<String, Object>> roomApplianceResults = handle
                .select("select * from room_appliance where room_id = ?", roomIdResult);
        Map<ApplianceId, Point> roomAppliances = new HashMap<>();

        for (Map<String, Object> roomApplianceResult : roomApplianceResults) {
            String  applianceId = (String) roomApplianceResult.get("appliance_id");
            Double x = (double) roomApplianceResult.get("x");
            Double y = (double) roomApplianceResult.get("y");
            roomAppliances.put(new ApplianceId(UUID.fromString(applianceId)), new Point(x, y));
        }

        return new Room(new RoomId(UUID.fromString(roomIdResult)),
                        roomResult.get("name").toString(),
                        new Shape(Arrays.asList(points)),
                        roomAppliances);
    }
}
