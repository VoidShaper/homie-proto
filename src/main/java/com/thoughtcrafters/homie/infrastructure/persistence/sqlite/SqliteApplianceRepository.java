package com.thoughtcrafters.homie.infrastructure.persistence.sqlite;

import com.thoughtcrafters.homie.domain.appliances.Appliance;
import com.thoughtcrafters.homie.domain.appliances.ApplianceCreation;
import com.thoughtcrafters.homie.domain.appliances.ApplianceId;
import com.thoughtcrafters.homie.domain.appliances.ApplianceNotFoundException;
import com.thoughtcrafters.homie.domain.appliances.ApplianceRepository;
import com.thoughtcrafters.homie.domain.appliances.ApplianceType;
import com.thoughtcrafters.homie.domain.appliances.ApplianceTypeNotSupportedException;
import com.thoughtcrafters.homie.domain.appliances.lights.Light;
import com.thoughtcrafters.homie.domain.appliances.lights.LightCreation;
import com.thoughtcrafters.homie.domain.behaviours.SwitchState;
import com.thoughtcrafters.homie.domain.rooms.RoomId;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;

public class SqliteApplianceRepository implements ApplianceRepository {

    private DBI sqliteDbi;

    public SqliteApplianceRepository(DBI sqliteDbi) {
        this.sqliteDbi = sqliteDbi;
    }

    @Override
    public Appliance getBy(ApplianceId applianceId) {
        try (Handle handle = sqliteDbi.open()) {
            List<Map<String, Object>> results = handle
                    .select("select * from appliance where appliance_id = ?", applianceId.uuid());
            if (results.isEmpty()) {
                throw new ApplianceNotFoundException(applianceId);
            }
            Map<String, Object> applianceResult = results.get(0);
            return mapResultToAppliance(handle, applianceResult);
        }
    }

    @Override
    public Appliance createFrom(ApplianceCreation creation) {
        ApplianceId applianceId = new ApplianceId(UUID.randomUUID());
        try (Handle handle = sqliteDbi.open()) {
            handle.execute("insert into appliance(appliance_id, name, appliance_type, room_id) values (?, ?, ?, ?)",
                           applianceId.uuid().toString(), creation.name(), creation.type());
            switch (creation.type()) {
                case LIGHT:
                    LightCreation lightCreation = (LightCreation) creation;
                    handle.execute("insert into light(appliance_id, switch_state, brightness) values (?, ?, ?)",
                                   applianceId.uuid().toString(),
                                   SwitchState.OFF.toString(),
                                   lightCreation.dimmable() ? 0 : null);
                    return new Light(applianceId, creation.name(), Optional.<RoomId>empty(), lightCreation.dimmable());
            }
        }
        throw new ApplianceTypeNotSupportedException(
                format("Saving appliance type %s is not supported yet.", creation.type()));
    }

    @Override
    public void save(Appliance appliance) {
        try (Handle handle = sqliteDbi.open()) {
            handle.execute("update appliance set room_id = ? where appliance_id = ?",
                                  roomIdOrNull(appliance),
                                  appliance.id().uuid());
            switch (appliance.type()) {
                case LIGHT:
                    Light light = (Light) appliance;
                    handle.execute("update light set switch_state = ?, brightness = ? where appliance_id = ?",
                                   light.switchState(),
                                   light.brightness().isPresent() ? light.brightness().get() : null,
                                   appliance.id().uuid());
                    return;
                default:
                    throw new ApplianceTypeNotSupportedException(
                            format("Saving appliance type %s is not supported yet.", appliance.type()));
            }
        }
    }

    private UUID roomIdOrNull(Appliance appliance) {
        return appliance.roomId().isPresent() ? appliance.roomId().get().uuid() : null;
    }

    @Override
    public List<Appliance> getAll() {
        List<Appliance> appliances = newArrayList();
        try (Handle handle = sqliteDbi.open()) {
            List<Map<String, Object>> results = handle.select("select * from appliance");
            for (Map<String, Object> applianceResult : results) {
                appliances.add(mapResultToAppliance(handle, applianceResult));
            }
        }
        return appliances;
    }

    private Appliance mapResultToAppliance(Handle handle, Map<String, Object> applianceResult) {
        ApplianceType applianceType = ApplianceType.valueOf(applianceResult.get("appliance_type").toString());
        String applianceIdResult = (String) applianceResult.get("appliance_id");

        if (applianceType == ApplianceType.LIGHT) {
            Map<String, Object> lightResult = handle
                    .select("select * from light where appliance_id = ?", applianceIdResult)
                    .get(0);
            String roomIdValue = (String) applianceResult.get("room_id");
            RoomId roomId = roomIdValue == null ? null : new RoomId(UUID.fromString(roomIdValue));
            SwitchState switchState = SwitchState.valueOf((String) lightResult.get("switch_State"));

            Integer brightness = (Integer) lightResult.get("brightness");

            return new Light(new ApplianceId(UUID.fromString(applianceIdResult)),
                             (String) applianceResult.get("name"),
                             Optional.ofNullable(roomId),
                             switchState,
                             Optional.ofNullable(brightness));
        }
        throw new ApplianceTypeNotSupportedException(
                format("Getting appliance of type %s of id %s is not supported yet.",
                       applianceType.toString(), applianceIdResult));
    }
}
