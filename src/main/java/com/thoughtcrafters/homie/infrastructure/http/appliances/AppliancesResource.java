package com.thoughtcrafters.homie.infrastructure.http.appliances;

import com.thoughtcrafters.homie.application.AppliancesApplicationService;
import com.thoughtcrafters.homie.application.FullAppliance;
import com.thoughtcrafters.homie.domain.appliances.Appliance;
import com.thoughtcrafters.homie.domain.appliances.ApplianceId;
import io.dropwizard.jersey.PATCH;
import io.dropwizard.jersey.params.UUIDParam;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.List;
import java.util.stream.Collectors;

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
        Appliance appliance = appliancesApplicationService.createApplianceFrom(request.toApplianceCreation());
        return Response.created(UriBuilder.fromPath(appliance.id().uuid().toString()).build())
                       .build();
    }

    @GET
    public List<ApplianceResponse> getAllAppliances() {
        return appliancesApplicationService.getAllAppliances().stream()
                .map(ApplianceResponse::fromFullAppliance)
                .collect(Collectors.toList());
    }

    @GET
    @Path("/{applianceId}")
    public ApplianceResponse getAppliance(@PathParam("applianceId") UUIDParam applianceId) {
        FullAppliance fullAppliance = appliancesApplicationService.getApplianceWith(new ApplianceId(applianceId.get()));
        return ApplianceResponse.fromFullAppliance(fullAppliance);
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
