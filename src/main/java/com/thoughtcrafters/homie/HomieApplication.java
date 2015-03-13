package com.thoughtcrafters.homie;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.thoughtcrafters.homie.application.AppliancesApplicationService;
import com.thoughtcrafters.homie.application.RoomTask;
import com.thoughtcrafters.homie.application.RoomsApplicationService;
import com.thoughtcrafters.homie.domain.appliances.lights.Light;
import com.thoughtcrafters.homie.domain.appliances.properties.EnumProperty;
import com.thoughtcrafters.homie.infrastructure.http.appliances.ApplianceTypeNotSupportedExceptionMapper;
import com.thoughtcrafters.homie.infrastructure.http.appliances.AppliancesResource;
import com.thoughtcrafters.homie.infrastructure.http.appliances.EnumAppliancePropertySerializer;
import com.thoughtcrafters.homie.infrastructure.http.appliances.LightNotFoundExceptionMapper;
import com.thoughtcrafters.homie.infrastructure.http.appliances.LightSerializer;
import com.thoughtcrafters.homie.infrastructure.http.appliances.PropertyUpdateNotAvailableExceptionMapper;
import com.thoughtcrafters.homie.infrastructure.http.rooms.RoomNotFoundExceptionMapper;
import com.thoughtcrafters.homie.infrastructure.http.rooms.RoomTaskDeserializer;
import com.thoughtcrafters.homie.infrastructure.http.rooms.RoomsResource;
import com.thoughtcrafters.homie.infrastructure.persistence.sqlite.SqliteApplianceRepository;
import com.thoughtcrafters.homie.infrastructure.persistence.sqlite.SqliteConnectionFactory;
import com.thoughtcrafters.homie.infrastructure.persistence.sqlite.SqliteDbRebuildCommand;
import com.thoughtcrafters.homie.infrastructure.persistence.sqlite.SqliteRoomRepository;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;

public class HomieApplication extends Application<HomieConfiguration> {

    @Override
    public void initialize(Bootstrap<HomieConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/assets/css", "/css", null, "css"));
        bootstrap.addBundle(new AssetsBundle("/assets/js", "/js", null, "js"));
        bootstrap.addBundle(new AssetsBundle("/assets/fonts", "/fonts", null, "fonts"));
        bootstrap.addBundle(new AssetsBundle("/assets/html", "/html", null, "html"));
        bootstrap.addBundle(new AssetsBundle("/assets/html/index.html", "/ui", null, "ui"));

        bootstrap.addCommand(new SqliteDbRebuildCommand());
    }

    @Override
    public void run(HomieConfiguration configuration, Environment environment) throws Exception {
        DBI dbi = SqliteConnectionFactory.jdbiConnectionTo(configuration.dbPath());

        SqliteApplianceRepository applianceRepository = new SqliteApplianceRepository(dbi);
        RoomsApplicationService roomsApplicationService = new RoomsApplicationService(new SqliteRoomRepository(dbi),
                                                                                      applianceRepository);
        environment.getObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
        SimpleModule homieModule = new SimpleModule("HomieModule");
        homieModule.addSerializer(Light.class, new LightSerializer());
        homieModule.addSerializer(EnumProperty.class, new EnumAppliancePropertySerializer());
        homieModule.addDeserializer(RoomTask.class, new RoomTaskDeserializer(roomsApplicationService));
        environment.getObjectMapper().registerModule(homieModule);

        environment.jersey().register(new RoomNotFoundExceptionMapper());
        environment.jersey().register(new LightNotFoundExceptionMapper());
        environment.jersey().register(new PropertyUpdateNotAvailableExceptionMapper());
        environment.jersey().register(new ApplianceTypeNotSupportedExceptionMapper());
        environment.jersey().register(new AppliancesResource(new AppliancesApplicationService(applianceRepository)));
        environment.jersey()
                   .register(new RoomsResource(roomsApplicationService,
                                               environment.getObjectMapper()));
    }


    public static void main(String[] args) throws Exception {
        new HomieApplication().run(args);
    }

    public void clearAllData() {
    }
}
