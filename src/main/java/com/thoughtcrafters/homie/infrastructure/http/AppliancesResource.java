package com.thoughtcrafters.homie.infrastructure.http;

import com.google.common.collect.FluentIterable;
import com.thoughtcrafters.homie.application.LightsApplicationService;
import com.thoughtcrafters.homie.domain.appliances.ApplianceId;
import com.thoughtcrafters.homie.domain.lights.Light;
import io.dropwizard.jersey.params.UUIDParam;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.List;

@Path("/appliances")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AppliancesResource {

    private LightsApplicationService lightsApplicationService;

    public AppliancesResource(LightsApplicationService lightsApplicationService) {
        this.lightsApplicationService = lightsApplicationService;
    }

    @POST
    public Response createAppliance(@Valid NewApplianceRequest request) {
        Light light = lightsApplicationService.createLightFrom(request.getName(), request.getInitialState());
        return Response.created(UriBuilder.fromPath(light.id().uuid().toString()).build())
                       .build();
    }

    @GET
    public List<ApplianceResponse> getAllAppliances() {
        return FluentIterable.from(lightsApplicationService.getAllAppliances())
                             .transform(ApplianceResponse::withIdFrom)
                             .toList();
    }

    @GET
    @Path("/{applianceId}")
    public ApplianceResponse getAppliance(@PathParam("applianceId") UUIDParam applianceId) {
        return ApplianceResponse.withoutIdFrom(
                lightsApplicationService.getTheLightWith(new ApplianceId(applianceId.get())));
    }

    @POST
    @Path("/{applianceId}/on")
    public Response turnOnTheAppliance(@PathParam("applianceId") UUIDParam applianceId) {
        lightsApplicationService.turnOnTheLightWith(new ApplianceId(applianceId.get()));
        return Response.noContent().build();
    }

    @POST
    @Path("/{applianceId}/off")
    public Response turnOffTheAppliance(@PathParam("applianceId") UUIDParam applianceId) {
        lightsApplicationService.turnOffTheLightWith(new ApplianceId(applianceId.get()));
        return Response.noContent().build();
    }

}
