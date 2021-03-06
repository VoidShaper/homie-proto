package com.thoughtcrafters.homie.infrastructure.persistence.sqlite;

import com.thoughtcrafters.homie.HomieApplication;
import com.thoughtcrafters.homie.HomieConfiguration;
import com.thoughtcrafters.homie.domain.appliances.Appliance;
import com.thoughtcrafters.homie.domain.appliances.ApplianceId;
import com.thoughtcrafters.homie.domain.appliances.ApplianceNotFoundException;
import com.thoughtcrafters.homie.domain.appliances.lights.Light;
import com.thoughtcrafters.homie.domain.appliances.lights.LightCreation;
import com.thoughtcrafters.homie.domain.behaviours.SwitchState;
import com.thoughtcrafters.homie.domain.rooms.RoomId;
import io.dropwizard.testing.junit.ConfigOverride;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.assertj.core.data.MapEntry;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class SqliteApplianceRepositoryTest {

    public static String dbTestFile = "homieTest.db";

    @ClassRule
    public static final DropwizardAppRule<HomieConfiguration> app =
            new DropwizardAppRule<>(HomieApplication.class, "homie.yml",
                                    ConfigOverride.config("dbPath", dbTestFile));

    private DBI jdbiConnection = SqliteConnectionFactory.jdbiConnectionTo(dbTestFile);
    private SqliteApplianceRepository sqliteApplianceRepository = new SqliteApplianceRepository(jdbiConnection);

    @Before
    public void setUp() throws Exception {
        new SqliteDbRebuildCommand().rebuildDb(jdbiConnection);
    }

    @After
    public void tearDown() throws Exception {
        new File(dbTestFile).delete();
    }

    @Test
    public void returnsLightWithIdWhenCreatesOne() throws Exception {
        // when
        Appliance appliance = sqliteApplianceRepository.createFrom(new LightCreation("light123", false));

        // then
        assertThat(appliance)
                .isEqualToIgnoringGivenFields(new Light(new ApplianceId(UUID.randomUUID()),
                                                        "light123",
                                                        Optional.<RoomId>empty(),
                                                        false),
                                              "id");
        assertThat(appliance.id()).isNotNull();
    }

    @Test
    public void returnsDimmableLightWithIdWhenCreatesOne() throws Exception {
        // when
        Appliance appliance = sqliteApplianceRepository.createFrom(new LightCreation("light123", true));

        // then
        assertThat(appliance)
                .isEqualToIgnoringGivenFields(new Light(new ApplianceId(UUID.randomUUID()),
                                                        "light123",
                                                        Optional.<RoomId>empty(),
                                                        true),
                                              "id");
        assertThat(appliance.id()).isNotNull();
    }

    @Test
    public void persistsLight() throws Exception {
        // when
        Appliance appliance =
                sqliteApplianceRepository.createFrom(new LightCreation("my light 123", false));

        // then
        assertThat(dbResultFor("appliance"))
                .hasSize(1);

        assertThat(dbResultFor("appliance").get(0)).containsOnly(
                MapEntry.entry("appliance_id", appliance.id().uuid().toString()),
                MapEntry.entry("name", "my light 123"),
                MapEntry.entry("appliance_type", "LIGHT"),
                MapEntry.entry("room_id", null));

        assertThat(dbResultFor("light")).hasSize(1);
        assertThat(dbResultFor("light").get(0))
                .containsOnly(
                        MapEntry.entry("appliance_id", appliance.id().uuid().toString()),
                        MapEntry.entry("switch_state", "OFF"),
                        MapEntry.entry("brightness", null)
                );
    }

    @Test
    public void persistsDimmableLight() throws Exception {
        // when
        Appliance appliance =
                sqliteApplianceRepository.createFrom(new LightCreation("my light 123", true));

        // then
        assertThat(dbResultFor("appliance"))
                .hasSize(1);

        assertThat(dbResultFor("appliance").get(0)).containsOnly(
                MapEntry.entry("appliance_id", appliance.id().uuid().toString()),
                MapEntry.entry("name", "my light 123"),
                MapEntry.entry("appliance_type", "LIGHT"),
                MapEntry.entry("room_id", null));

        assertThat(dbResultFor("light")).hasSize(1);
        assertThat(dbResultFor("light").get(0))
                .containsOnly(
                        MapEntry.entry("appliance_id", appliance.id().uuid().toString()),
                        MapEntry.entry("switch_state", "OFF"),
                        MapEntry.entry("brightness", 0)
                );
    }

    @Test
    public void getsALightById() throws Exception {
        // given
        ApplianceId applianceId = new ApplianceId(UUID.randomUUID());
        Light aLight = new Light(applianceId, "aLight", Optional.<RoomId>empty(), false);
        creationWasCalledFor(aLight);

        // when
        Appliance appliance = sqliteApplianceRepository.getBy(applianceId);

        // then
        assertThat(appliance).isNotNull().isEqualTo(aLight);
    }

    @Test
    public void getsADimmableLightById() throws Exception {
        // given
        ApplianceId applianceId = new ApplianceId(UUID.randomUUID());
        Light aLight = new Light(applianceId, "aLight", Optional.<RoomId>empty(), true);
        creationWasCalledFor(aLight);

        // when
        Appliance appliance = sqliteApplianceRepository.getBy(applianceId);

        // then
        assertThat(appliance).isNotNull().isEqualTo(aLight);
    }

    @Test
    public void throwsApplianceNotFoundExceptionWhenNoApplianceFound() throws Exception {
        // given
        ApplianceId applianceId = new ApplianceId(UUID.randomUUID());

        // when
        try {
            sqliteApplianceRepository.getBy(applianceId);
            fail("Expected ApplianceNotFoundException to be thrown");
        } catch (ApplianceNotFoundException e) {
            // then
            assertThat(e.appliance()).isEqualTo(applianceId);
        }
    }

    @Test
    public void getsAnApplianceInARoomById() throws Exception {
        // given
        ApplianceId applianceId = new ApplianceId(UUID.randomUUID());
        Light aLight =
                new Light(applianceId,
                          "aLight",
                          Optional.of(new RoomId(UUID.randomUUID())),
                          SwitchState.ON,
                          Optional.<Integer>empty());
        creationWasCalledFor(aLight);

        // when
        Appliance appliance = sqliteApplianceRepository.getBy(applianceId);

        // then
        assertThat(appliance).isNotNull().isEqualTo(aLight);
    }

    @Test
    public void getsAllAppliancesInARoom() throws Exception {
        // given
        Light aLight1 =
                new Light(new ApplianceId(UUID.randomUUID()),
                          "aLight",
                          Optional.of(new RoomId(UUID.randomUUID())),
                          SwitchState.ON,
                          Optional.<Integer>empty());
        Light aLight2 =
                new Light(new ApplianceId(UUID.randomUUID()),
                          "anotherLight",
                          Optional.empty(),
                          SwitchState.OFF,
                          Optional.of(50));
        creationWasCalledFor(aLight1);
        creationWasCalledFor(aLight2);

        // when
        List<Appliance> appliances = sqliteApplianceRepository.getAll();

        // then
        assertThat(appliances).isNotNull()
                              .containsOnly(aLight1, aLight2);
    }

    @Test
    public void savesAModifiedAppliance() throws Exception {
        // given
        Light aLight =
                new Light(new ApplianceId(UUID.randomUUID()),
                          "aLightApplianceName",
                          Optional.empty(),
                          SwitchState.OFF,
                          Optional.<Integer>empty());

        creationWasCalledFor(aLight);

        // when
        RoomId roomId = new RoomId(UUID.randomUUID());
        aLight.placeInTheRoomWith(roomId);
        Whitebox.setInternalState(aLight, "switchState", SwitchState.ON);
        Whitebox.setInternalState(aLight, "brightness", Optional.of(79));
        sqliteApplianceRepository.save(aLight);

        // then
        assertThat(dbResultFor("appliance")).hasSize(1);
        assertThat(dbResultFor("appliance").get(0)).containsOnly(
                MapEntry.entry("appliance_id", aLight.id().uuid().toString()),
                MapEntry.entry("name", "aLightApplianceName"),
                MapEntry.entry("appliance_type", "LIGHT"),
                MapEntry.entry("room_id", roomId.uuid().toString()));

        assertThat(dbResultFor("light"))
                .hasSize(1);
        assertThat(dbResultFor("light").get(0))
                .containsOnly(
                        MapEntry.entry("appliance_id", aLight.id().uuid().toString()),
                        MapEntry.entry("switch_state", "ON"),
                        MapEntry.entry("brightness", 79)
                );

    }

    public List<Map<String, Object>> dbResultFor(String tableName) {
        Handle handle = jdbiConnection.open();
        List<Map<String, Object>> appliances = handle.createQuery("select * from " + tableName).list();
        handle.close();
        return appliances;
    }

    public void creationWasCalledFor(Light light) {
        Handle handle = jdbiConnection.open();
        handle.execute("insert into appliance(appliance_id, name, appliance_type, room_id) values (?, ?, ?, ?)",
                       light.id().uuid().toString(), light.name(), light.type(),
                       light.roomId().isPresent() ?
                               light.roomId().get().uuid().toString() : null);

        handle.execute("insert into light(appliance_id, switch_state, brightness) values (?, ?, ?)",
                       light.id().uuid().toString(),
                       light.switchState(),
                       light.brightness().isPresent() ? light.brightness().get() : null);
        handle.close();
    }
}