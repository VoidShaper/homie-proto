package com.thoughtcrafters.homie.infrastructure.http.appliances;

import com.thoughtcrafters.homie.application.AppliancesApplicationService;
import com.thoughtcrafters.homie.domain.appliances.Appliance;
import com.thoughtcrafters.homie.domain.appliances.ApplianceId;
import io.dropwizard.jersey.params.UUIDParam;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

@Path("/appliances")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AppliancesResource {

    private AppliancesApplicationService appliancesApplicationService;

    public AppliancesResource(AppliancesApplicationService appliancesApplicationService) {
        this.appliancesApplicationService = appliancesApplicationService;
    }

    @POST
    public Response createAppliance(@Valid NewApplianceRequest request) {
        Appliance appliance = appliancesApplicationService.createApplianceFrom(request.getType(), request.getName());
        return Response.created(UriBuilder.fromPath(appliance.id().uuid().toString()).build())
                       .build();
    }

    @GET
    public Appliance[] getAllAppliances() {
        return appliancesApplicationService.getAllAppliances().toArray(new Appliance[]{});
    }

    @GET
    @Path("/{applianceId}")
    public Appliance getAppliance(@PathParam("applianceId") UUIDParam applianceId) {
        return appliancesApplicationService.getApplianceWith(new ApplianceId(applianceId.get()));
    }

    @POST
    @Path("/{applianceId}/on")
    public Response turnOnTheAppliance(@PathParam("applianceId") UUIDParam applianceId) {
        appliancesApplicationService.turnOnTheLightWith(new ApplianceId(applianceId.get()));
        return Response.noContent().build();
    }

    @POST
    @Path("/{applianceId}/off")
    public Response turnOffTheAppliance(@PathParam("applianceId") UUIDParam applianceId) {
        appliancesApplicationService.turnOffTheLightWith(new ApplianceId(applianceId.get()));
        return Response.noContent().build();
    }

}
