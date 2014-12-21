package com.thoughtcrafters.homie.infrastructure.http;

import com.thoughtcrafters.homie.application.LightsApplicationService;
import com.thoughtcrafters.homie.domain.lights.Light;
import io.dropwizard.jersey.params.UUIDParam;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

@Path("/lights")
public class LightsResource {

    private LightsApplicationService lightsApplicationService;

    public LightsResource(LightsApplicationService lightsApplicationService1) {
        this.lightsApplicationService = lightsApplicationService1;
    }

    @GET
    @Path("/{lightId}")
    @Produces("application/json")
    public LightResponse getLight(@PathParam("lightId") UUIDParam lightId) {
        return LightResponse.from(lightsApplicationService.getLightBy(lightId.get()));
    }

    @POST
    @Produces("application/json")
    @Consumes("application/json")
    public Response createLight(@Valid NewLightRequest request) {
        Light light = lightsApplicationService.createLight(request.getName(), request.getInitialState());
        return Response.created(UriBuilder.fromPath(light.id().getId().toString()).build())
                .build();
//        return Response.created(UriBuilder.fromMethod(LightsResource.class, "getLight").build()).build();
    }

}
