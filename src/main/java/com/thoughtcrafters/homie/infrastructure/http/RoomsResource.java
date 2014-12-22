package com.thoughtcrafters.homie.infrastructure.http;

import com.thoughtcrafters.homie.application.RoomsApplicationService;
import com.thoughtcrafters.homie.domain.ApplianceId;
import com.thoughtcrafters.homie.domain.rooms.Room;
import com.thoughtcrafters.homie.domain.rooms.RoomId;
import io.dropwizard.jersey.params.UUIDParam;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomsResource {

    private RoomsApplicationService roomsApplicationService;

    public RoomsResource(RoomsApplicationService roomsApplicationService) {
        this.roomsApplicationService = roomsApplicationService;
    }

    @POST
    public Response createRoom(@Valid NewRoomRequest request) {
        Room room = roomsApplicationService.createRoomWith(request.getName());
        return Response.created(UriBuilder.fromPath(room.id().uuid().toString()).build())
                       .build();
    }

    @GET
    @Path("/{roomId}")
    public RoomResponse getRoom(@PathParam("roomId") UUIDParam roomId) {
        Room room = roomsApplicationService.getTheRoomWith(new RoomId(roomId.get()));
        return RoomResponse.from(room);
    }

    @POST
    @Path("/{roomId}/add/{applianceId}")
    public Response addApplianceToRoom(@PathParam("roomId") UUIDParam roomId,
                                       @PathParam("applianceId") UUIDParam applianceId) {
        roomsApplicationService.addApplianceToRoom(new ApplianceId(applianceId.get()),
                                                   new RoomId(roomId.get()));
        return Response.noContent().build();
    }
}
