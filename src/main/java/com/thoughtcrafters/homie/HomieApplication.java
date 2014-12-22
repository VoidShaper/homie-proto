package com.thoughtcrafters.homie;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thoughtcrafters.homie.application.LightsApplicationService;
import com.thoughtcrafters.homie.application.RoomsApplicationService;
import com.thoughtcrafters.homie.infrastructure.http.LightsResource;
import com.thoughtcrafters.homie.infrastructure.http.RoomsResource;
import com.thoughtcrafters.homie.infrastructure.http.mappers.LightNotFoundExceptionMapper;
import com.thoughtcrafters.homie.infrastructure.persistence.HashMapLightsRepository;
import com.thoughtcrafters.homie.infrastructure.persistence.HashMapRoomsRepository;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HomieApplication extends Application<HomieConfiguration> {

    private static final Logger logger = LoggerFactory.getLogger(HomieApplication.class);

    @Override
    public void initialize(Bootstrap<HomieConfiguration> bootstrap) {

    }

    @Override
    public void run(HomieConfiguration configuration, Environment environment) throws Exception {
        environment.getObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

        environment.jersey().register(new LightNotFoundExceptionMapper());
        HashMapLightsRepository lightsRepository = new HashMapLightsRepository();
        environment.jersey().register(new LightsResource(new LightsApplicationService(lightsRepository)));
        environment.jersey().register(new RoomsResource(new RoomsApplicationService(new HashMapRoomsRepository(), lightsRepository)));
    }

    public static void main(String[] args) throws Exception {
        new HomieApplication().run(args);
    }
}
