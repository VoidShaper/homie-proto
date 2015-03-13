package com.thoughtcrafters.homie.infrastructure.http.appliances;

import com.thoughtcrafters.homie.application.AppliancesApplicationService;
import com.thoughtcrafters.homie.domain.appliances.Appliance;
import com.thoughtcrafters.homie.domain.appliances.ApplianceId;
import io.dropwizard.jersey.PATCH;
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

    @PATCH
    @Path("/{applianceId}")
    @Consumes("application/json-patch+json")
    public Response patchAppliance(@PathParam("applianceId") UUIDParam applianceId,
                                   @Valid PatchResourceRequest patchResourceRequest) {
        appliancesApplicationService.updateProperty(new ApplianceId(applianceId.get()),
                                                    patchResourceRequest.toPropertyUpdate());
        return Response.noContent().build();

    }

}
