package com.thoughtcrafters.homie.infrastructure.http.rooms;

import com.google.common.collect.FluentIterable;
import com.thoughtcrafters.homie.application.RoomsApplicationService;
import com.thoughtcrafters.homie.domain.appliances.ApplianceId;
import com.thoughtcrafters.homie.domain.rooms.Room;
import com.thoughtcrafters.homie.domain.rooms.RoomId;
import io.dropwizard.jersey.params.UUIDParam;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.List;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomsResource {

    private RoomsApplicationService roomsApplicationService;

    public RoomsResource(RoomsApplicationService roomsApplicationService) {
        this.roomsApplicationService = roomsApplicationService;
    }

    @GET
    public List<RoomResponse> allRooms() {
        return FluentIterable.from(roomsApplicationService.getAllRooms())
                             .transform(RoomResponse::withIdFrom)
                             .toList();
    }

    @POST
    public Response createRoom(@Valid NewRoomRequest request) {
        Room room = roomsApplicationService.createRoomWith(request.getName(), request.outline());
        return Response.created(UriBuilder.fromPath(room.id().uuid().toString()).build())
                       .build();
    }

    @GET
    @Path("/{roomId}")
    public RoomResponse getRoom(@PathParam("roomId") UUIDParam roomId) {
        Room room = roomsApplicationService.getTheRoomWith(new RoomId(roomId.get()));
        return RoomResponse.withoutIdFrom(room);
    }

    @PUT
    @Path("/{roomId}/appliances/{applianceId}")
    public Response addApplianceToRoom(@PathParam("roomId") UUIDParam roomId,
                                       @PathParam("applianceId") UUIDParam applianceId,
                                       @Valid PointBody pointBody) {
        roomsApplicationService.addApplianceToRoom(new ApplianceId(applianceId.get()),
                                                   new RoomId(roomId.get()),
                                                   pointBody.asPoint());
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{roomId}/appliances/{applianceId}")
    public Response removeApplianceFromRoom(@PathParam("roomId") UUIDParam roomId,
                                            @PathParam("applianceId") UUIDParam applianceId) {
        roomsApplicationService.removeApplianceFromRoom(new ApplianceId(applianceId.get()),
                                                        new RoomId(roomId.get()));
        return Response.noContent().build();
    }
}
