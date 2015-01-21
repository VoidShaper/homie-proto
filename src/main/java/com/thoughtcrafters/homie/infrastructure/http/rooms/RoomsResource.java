package com.thoughtcrafters.homie.infrastructure.http.rooms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.FluentIterable;
import com.thoughtcrafters.homie.application.RoomTask;
import com.thoughtcrafters.homie.application.RoomsApplicationService;
import com.thoughtcrafters.homie.domain.rooms.Room;
import com.thoughtcrafters.homie.domain.rooms.RoomId;
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
import java.io.IOException;
import java.util.List;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomsResource {

    private RoomsApplicationService roomsApplicationService;
    private ObjectMapper objectMapper;

    public RoomsResource(RoomsApplicationService roomsApplicationService,
                         ObjectMapper objectMapper) {
        this.roomsApplicationService = roomsApplicationService;
        this.objectMapper = objectMapper;
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

    @PATCH
    @Path("/{roomId}")
    @Consumes("application/json-patch+json")
    public Response patchRoom(@PathParam("roomId") UUIDParam roomId,
                              @Valid RoomTask roomTask) throws IOException {
        roomTask.performTaskOn(new RoomId(roomId.get()));
        return Response.noContent().build();
    }
}
