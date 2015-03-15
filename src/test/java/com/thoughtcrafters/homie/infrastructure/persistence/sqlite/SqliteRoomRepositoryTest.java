package com.thoughtcrafters.homie.infrastructure.persistence.sqlite;

import com.thoughtcrafters.homie.HomieApplication;
import com.thoughtcrafters.homie.HomieConfiguration;
import com.thoughtcrafters.homie.domain.appliances.ApplianceId;
import com.thoughtcrafters.homie.domain.appliances.lights.Light;
import com.thoughtcrafters.homie.domain.rooms.Point;
import com.thoughtcrafters.homie.domain.rooms.Room;
import com.thoughtcrafters.homie.domain.rooms.RoomId;
import com.thoughtcrafters.homie.domain.rooms.RoomNotFoundException;
import com.thoughtcrafters.homie.domain.rooms.Shape;
import io.dropwizard.testing.junit.ConfigOverride;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.assertj.core.data.MapEntry;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

public class SqliteRoomRepositoryTest {

    public static String dbTestFile = "homieTest.db";

    @ClassRule
    public static final DropwizardAppRule<HomieConfiguration> app =
            new DropwizardAppRule<>(HomieApplication.class, "homie.yml",
                                    ConfigOverride.config("dbPath", dbTestFile));

    private DBI jdbiConnection = SqliteConnectionFactory.jdbiConnectionTo(dbTestFile);

    private SqliteRoomRepository sqliteRoomRepository = new SqliteRoomRepository(jdbiConnection);

    @Before
    public void setUp() throws Exception {
        new SqliteDbRebuildCommand().rebuildDb(jdbiConnection);
    }

    @After
    public void tearDown() throws Exception {
        new File(dbTestFile).delete();
    }

    @Test
    public void returnsRoomWithIdWhenCreatesOne() throws Exception {
        // when
        Room room =
                sqliteRoomRepository.createFrom("Living Room", rectangle20x20());

        // then
        assertThat(room)
                .describedAs("Room returned from creation method")
                .isNotNull()
                .isEqualTo(new Room(room.id(),
                                    "Living Room",
                                    rectangle20x20()));

        assertThat(room.id()).isNotNull();
    }

    @Test
    public void createsARoomCorrectly() throws Exception {
        // given
        Shape shape = rectangle20x20();

        // when
        Room room =
                sqliteRoomRepository.createFrom("Small Room", shape);

        // then
        assertThat(dbResultFor("room"))
                .hasSize(1);

        assertThat(dbResultFor("room").get(0))
                .containsOnly(
                        MapEntry.entry("room_id", room.id().uuid().toString()),
                        MapEntry.entry("name", "Small Room")
                );

        List<Map<String, Object>> shapePointsResult = dbResultFor("shape_point");
        String roomId = room.id().uuid().toString();
        assertShapeResultContainsShape(shapePointsResult, shape, roomId);
    }

    @Test
    public void getsARoomById() {
        // given
        RoomId roomId = new RoomId(UUID.randomUUID());
        Room alreadyCreatedRoom = new Room(roomId, "aRoom", rectangle20x20());
        creationWasCalledFor(alreadyCreatedRoom);

        // when
        Room room = sqliteRoomRepository.getBy(roomId);

        // then
        assertThat(room).describedAs("Previously saved room")
                        .isNotNull()
                        .isEqualTo(alreadyCreatedRoom);
    }

    @Test
    public void getsARoomWithApplianceById() {
        // given
        RoomId roomId = new RoomId(UUID.randomUUID());
        Room alreadyCreatedRoom = new Room(roomId, "aRoom", rectangle20x20());
        alreadyCreatedRoom.place(new Light(new ApplianceId(UUID.randomUUID()),
                                           "appLight",
                                           Optional.<RoomId>empty(),
                                           false))
                          .at(new Point(5.5, 23));
        creationWasCalledFor(alreadyCreatedRoom);

        // when
        Room room = sqliteRoomRepository.getBy(roomId);

        // then
        assertThat(room).describedAs("Previously saved room")
                        .isNotNull()
                        .isEqualTo(alreadyCreatedRoom);
    }

    @Test
    public void throwsRoomNotFoundExceptionWhenNoApplianceFound() throws Exception {
        // given
        RoomId roomId = new RoomId(UUID.randomUUID());

        // when
        try {
            sqliteRoomRepository.getBy(roomId);
            fail("Expected RoomNotFoundException to be thrown");
        } catch (RoomNotFoundException e) {
            // then
            assertThat(e.roomId()).isEqualTo(roomId);
        }
    }

    @Test
    public void getsAllPersistedRooms() throws Exception {
        // given
        Room alreadyCreatedRoom = new Room(new RoomId(UUID.randomUUID()), "roomOne", rectangle20x20());
        Room anotherCreatedRoom = new Room(new RoomId(UUID.randomUUID()), "roomTwo", polyline5points());
        Room thirdRoomWithLight = new Room(new RoomId(UUID.randomUUID()), "roomTwo", polyline5points());
        thirdRoomWithLight.place(new Light(new ApplianceId(UUID.randomUUID()), "were light", Optional.<RoomId>empty(), false))
                .at(new Point(3.3, 9.982));
        creationWasCalledFor(alreadyCreatedRoom);
        creationWasCalledFor(anotherCreatedRoom);
        creationWasCalledFor(thirdRoomWithLight);

        // when
        List<Room> rooms = sqliteRoomRepository.getAll();

        // then
        assertThat(rooms).describedAs("persisted rooms").isNotNull()
                         .containsOnly(alreadyCreatedRoom, anotherCreatedRoom, thirdRoomWithLight);

    }

    @Test
    public void savesAModifiedRoom() throws Exception {
        // given
        Room initiallyCreatedRoom = new Room(new RoomId(UUID.randomUUID()), "exampleRoom", polyline5points());
        creationWasCalledFor(initiallyCreatedRoom);
        Light lightInARoom = new Light(new ApplianceId(UUID.randomUUID()), "lightInARoom", Optional.<RoomId>empty(), false);
        Point lightInARoomPlace = new Point(7.5, 7.5);
        initiallyCreatedRoom.place(lightInARoom).at(lightInARoomPlace);
        Light anotherLightInARoom = new Light(new ApplianceId(UUID.randomUUID()), "lightInARoom",
                                              Optional.<RoomId>empty(), false);
        Point anotherLightInARoomPlace = new Point(15, 25.3);
        initiallyCreatedRoom.place(anotherLightInARoom).at(anotherLightInARoomPlace);

        // when
        sqliteRoomRepository.save(initiallyCreatedRoom);

        // then
        assertThat(dbResultFor("room"))
                .hasSize(1);

        assertThat(dbResultFor("room").get(0))
                .containsOnly(
                        MapEntry.entry("room_id", initiallyCreatedRoom.id().uuid().toString()),
                        MapEntry.entry("name", "exampleRoom")
                );

        List<Map<String, Object>> shapePointsResult = dbResultFor("shape_point");
        String roomId = initiallyCreatedRoom.id().uuid().toString();
        assertShapeResultContainsShape(shapePointsResult, initiallyCreatedRoom.shape(), roomId);

        assertThat(dbResultFor("room_appliance")).hasSize(2);

        assertThat(dbResultFor("room_appliance").get(0))
                .containsOnly(
                        MapEntry.entry("room_appliance_id", 1),
                        MapEntry.entry("appliance_id", lightInARoom.id().uuid().toString()),
                        MapEntry.entry("room_id", roomId),
                        MapEntry.entry("x", lightInARoomPlace.x()),
                        MapEntry.entry("y", lightInARoomPlace.y())
                );

        assertThat(dbResultFor("room_appliance").get(1))
                .containsOnly(
                        MapEntry.entry("room_appliance_id", 2),
                        MapEntry.entry("appliance_id", anotherLightInARoom.id().uuid().toString()),
                        MapEntry.entry("room_id", roomId),
                        MapEntry.entry("x", anotherLightInARoomPlace.x()),
                        MapEntry.entry("y", anotherLightInARoomPlace.y())
                );

    }

    @Test
    public void updateAppliancesThroughSaveIfTheEntryAlreadyExists() throws Exception {
        // given
        Room initiallyCreatedRoom = new Room(new RoomId(UUID.randomUUID()), "exampleRoom", polyline5points());
        Light lightInARoom = new Light(new ApplianceId(UUID.randomUUID()), "lightInARoom", Optional.<RoomId>empty(), false);
        Point lightInARoomPlace = new Point(7.5, 7.5);
        initiallyCreatedRoom.place(lightInARoom).at(lightInARoomPlace);

        Light lightRemovedLater = new Light(new ApplianceId(UUID.randomUUID()), "lightRemovedLater",
                                            Optional.<RoomId>empty(), false);
        initiallyCreatedRoom.place(lightRemovedLater).at(new Point(15, 25.3));
        creationWasCalledFor(initiallyCreatedRoom);

        Light lightPlacedLater = new Light(new ApplianceId(UUID.randomUUID()), "lightInARoom",
                                           Optional.<RoomId>empty(), false);
        Point laterLightPlace = new Point(73, 28.98);

        // when
        initiallyCreatedRoom.remove(lightRemovedLater);
        initiallyCreatedRoom.place(lightPlacedLater).at(laterLightPlace);
        sqliteRoomRepository.save(initiallyCreatedRoom);

        // then
        assertThat(dbResultFor("room")).hasSize(1);

        assertThat(dbResultFor("room").get(0))
                .containsOnly(
                        MapEntry.entry("room_id", initiallyCreatedRoom.id().uuid().toString()),
                        MapEntry.entry("name", "exampleRoom")
                );

        List<Map<String, Object>> shapePointsResult = dbResultFor("shape_point");
        String roomId = initiallyCreatedRoom.id().uuid().toString();
        assertShapeResultContainsShape(shapePointsResult, initiallyCreatedRoom.shape(), roomId);

        assertThat(dbResultFor("room_appliance")).hasSize(2);

        assertThat(dbResultFor("room_appliance").get(0))
                .containsOnly(
                        MapEntry.entry("room_appliance_id", 1),
                        MapEntry.entry("appliance_id", lightInARoom.id().uuid().toString()),
                        MapEntry.entry("room_id", roomId),
                        MapEntry.entry("x", lightInARoomPlace.x()),
                        MapEntry.entry("y", lightInARoomPlace.y())
                );

        assertThat(dbResultFor("room_appliance").get(1))
                .containsOnly(
                        MapEntry.entry("room_appliance_id", 2),
                        MapEntry.entry("appliance_id", lightPlacedLater.id().uuid().toString()),
                        MapEntry.entry("room_id", roomId),
                        MapEntry.entry("x", laterLightPlace.x()),
                        MapEntry.entry("y", laterLightPlace.y())
                );

    }

    private void assertShapeResultContainsShape(List<Map<String, Object>> shapePointsResult, Shape shape,
                                                String roomId) {
        assertThat(shapePointsResult).hasSize(shape.outline().size());

        for (int i = 0; i < shape.outline().size(); ++i) {
            assertThat(shapePointsResult.get(i)).containsOnly(
                    pointResultFor(i, roomId, shape.outline().get(i))
            );
        }
    }

    private void creationWasCalledFor(Room room) {
        Handle handle = jdbiConnection.open();
        handle.execute("insert into room(room_id, name) values (?, ?)",
                       room.id().uuid(), room.name());
        for (int i = 0; i < room.shape().outline().size(); ++i) {
            Point point = room.shape().outline().get(i);
            handle.execute("insert into shape_point(room_id, position, x, y) values (?, ?, ?, ?)",
                           room.id().uuid(), i, point.x(), point.y());
        }
        for(Map.Entry<ApplianceId, Point> placedAppliance : room.appliances().entrySet()) {
            handle.execute("insert into room_appliance(room_id, appliance_id, x, y) values (?, ?, ?, ?)",
                           room.id().uuid(),
                           placedAppliance.getKey().uuid(),
                           placedAppliance.getValue().x(),
                           placedAppliance.getValue().y());
        }
        handle.close();
    }


    private MapEntry[] pointResultFor(int index, String roomId, Point point) {
        return new MapEntry[]{
                MapEntry.entry("shape_point_id", index + 1),
                MapEntry.entry("room_id", roomId),
                MapEntry.entry("position", index),
                MapEntry.entry("x", point.x()),
                MapEntry.entry("y", point.y())};
    }

    private Shape rectangle20x20() {
        return new Shape(newArrayList(new Point(0, 0),
                                      new Point(20, 0),
                                      new Point(20, 20),
                                      new Point(0, 20)));
    }

    private Shape polyline5points() {
        return new Shape(newArrayList(new Point(1.3, 8.3),
                                      new Point(15.6, 13.2),
                                      new Point(30, 23.9),
                                      new Point(40.8, 58),
                                      new Point(12.3, 25.6)));
    }

    public List<Map<String, Object>> dbResultFor(String tableName) {
        Handle handle = jdbiConnection.open();
        List<Map<String, Object>> appliances = handle.createQuery("select * from " + tableName).list();
        handle.close();
        return appliances;
    }
}