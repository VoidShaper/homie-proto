package com.thoughtcrafters.homie;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.thoughtcrafters.homie.application.LightsApplicationService;
import com.thoughtcrafters.homie.application.RoomsApplicationService;
import com.thoughtcrafters.homie.domain.appliances.lights.Light;
import com.thoughtcrafters.homie.domain.appliances.operations.PropertyUpdate;
import com.thoughtcrafters.homie.infrastructure.http.appliances.AppliancesResource;
import com.thoughtcrafters.homie.infrastructure.http.appliances.LightSerializer;
import com.thoughtcrafters.homie.infrastructure.http.appliances.PropertyUpdateSerializer;
import com.thoughtcrafters.homie.infrastructure.http.rooms.RoomsResource;
import com.thoughtcrafters.homie.infrastructure.http.appliances.LightNotFoundExceptionMapper;
import com.thoughtcrafters.homie.infrastructure.persistence.HashMapLightsRepository;
import com.thoughtcrafters.homie.infrastructure.persistence.HashMapRoomsRepository;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomieApplication extends Application<HomieConfiguration> {

    private static final Logger logger = LoggerFactory.getLogger(HomieApplication.class);
    private HashMapRoomsRepository roomsRepository;
    private HashMapLightsRepository lightsRepository;

    @Override
    public void initialize(Bootstrap<HomieConfiguration> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/assets/css", "/css", null, "css"));
        bootstrap.addBundle(new AssetsBundle("/assets/js", "/js", null, "js"));
        bootstrap.addBundle(new AssetsBundle("/assets/fonts", "/fonts", null, "fonts"));
        bootstrap.addBundle(new AssetsBundle("/assets/html", "/html", null, "html"));
        bootstrap.addBundle(new AssetsBundle("/assets/html/index.html", "/ui", null, "ui"));
    }

    @Override
    public void run(HomieConfiguration configuration, Environment environment) throws Exception {
        environment.getObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
        SimpleModule homieModule = new SimpleModule("HomieModule");
        homieModule.addSerializer(Light.class, new LightSerializer());
        homieModule.addSerializer(PropertyUpdate.class, new PropertyUpdateSerializer());
        environment.getObjectMapper().registerModule(homieModule);

        environment.jersey().register(new LightNotFoundExceptionMapper());
        lightsRepository = new HashMapLightsRepository();
        environment.jersey().register(new AppliancesResource(new LightsApplicationService(lightsRepository)));
        roomsRepository = new HashMapRoomsRepository();
        environment.jersey()
                   .register(new RoomsResource(new RoomsApplicationService(roomsRepository, lightsRepository)));

    }

    public static void main(String[] args) throws Exception {
        new HomieApplication().run(args);
    }

    public void clearAllData() {
        roomsRepository.clearAll();
        lightsRepository.clearAll();
    }
}
