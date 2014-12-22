package com.thoughtcrafters.homie.infrastructure.http;

import com.thoughtcrafters.homie.application.LightsApplicationService;
import com.thoughtcrafters.homie.domain.ApplianceId;
import com.thoughtcrafters.homie.domain.lights.Light;
import io.dropwizard.jersey.params.UUIDParam;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

@Path("/lights")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LightsResource {

    private LightsApplicationService lightsApplicationService;

    public LightsResource(LightsApplicationService lightsApplicationService1) {
        this.lightsApplicationService = lightsApplicationService1;
    }

    @GET
    @Path("/{lightId}")
    public LightResponse getLight(@PathParam("lightId") UUIDParam lightId) {
        return LightResponse.from(lightsApplicationService.getTheLightWith(new ApplianceId(lightId.get())));
    }

    @POST
    public Response createLight(@Valid NewLightRequest request) {
        Light light = lightsApplicationService.createLightFrom(request.getName(), request.getInitialState());
        return Response.created(UriBuilder.fromPath(light.id().uuid().toString()).build())
                .build();
    }

    @POST
    @Path("/{lightId}/on")
    public Response turnOnTheLight(@PathParam("lightId") UUIDParam lightId) {
        lightsApplicationService.turnOnTheLightWith(new ApplianceId(lightId.get()));
        return Response.noContent().build();
    }

    @POST
    @Path("/{lightId}/off")
    public Response turnOffTheLight(@PathParam("lightId") UUIDParam lightId) {
        lightsApplicationService.turnOffTheLightWith(new ApplianceId(lightId.get()));
        return Response.noContent().build();
    }
}
