package com.thoughtcrafters.homie;

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

public class HomieApplication extends Application<HomieConfguration> {

    private static final Logger logger = LoggerFactory.getLogger(HomieApplication.class);

    @Override
    public void initialize(Bootstrap<HomieConfguration> bootstrap) {

    }

    @Override
    public void run(HomieConfguration configuration, Environment environment) throws Exception {
        environment.jersey().register(new LightNotFoundExceptionMapper());
        environment.jersey().register(new LightsResource(new LightsApplicationService(new HashMapLightsRepository())));
        environment.jersey().register(new RoomsResource(new RoomsApplicationService(new HashMapRoomsRepository())));
    }

    public static void main(String[] args) throws Exception {
        new HomieApplication().run(args);
    }
}
