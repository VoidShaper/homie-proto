package com.thoughtcrafters.homie.infrastructure.http.rooms;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.thoughtcrafters.homie.application.PlaceApplianceInTheRoomTask;
import com.thoughtcrafters.homie.application.RemoveApplianceFromTheRoomTask;
import com.thoughtcrafters.homie.application.RoomTask;
import com.thoughtcrafters.homie.application.RoomsApplicationService;
import com.thoughtcrafters.homie.domain.appliances.ApplianceId;
import com.thoughtcrafters.homie.domain.rooms.Point;
import io.dropwizard.jersey.params.UUIDParam;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.IOException;

public class RoomTaskDeserializer extends StdDeserializer<RoomTask> {

    private RoomsApplicationService roomsApplicationService;

    public RoomTaskDeserializer(RoomsApplicationService roomsApplicationService) {
        super(RoomTask.class);
        this.roomsApplicationService = roomsApplicationService;
    }

    @Override
    public RoomTask deserialize(JsonParser jsonParser,
                                DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ObjectCodec objectMapper = jsonParser.getCodec();
        JsonNode root = objectMapper.readTree(jsonParser);
        String path = root.get("path").textValue();
        String op = root.get("op").textValue();
        switch (path) {
            case "/appliances":
                if (op.equals("add")) {
                    JsonNode value = root.get("value");
                    UUIDParam applianceId = new UUIDParam(value.get("applianceId").textValue());
                    JsonNode point = value.get("point");
                    double x = point.get("x").asDouble();
                    double y = point.get("y").asDouble();
                    return new PlaceApplianceInTheRoomTask(roomsApplicationService,
                                                           new ApplianceId(applianceId.get()),
                                                           new Point(x, y));
                } else if (op.equals("remove")) {
                    JsonNode value = root.get("value");
                    UUIDParam applianceId = new UUIDParam(value.textValue());
                    return new RemoveApplianceFromTheRoomTask(roomsApplicationService,
                                                              new ApplianceId(applianceId.get()));
                }
        }
        String message = String.format("Cannot recognize a patch room operation %s on path %s", op, path);
        throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                                                  .entity(message)
                                                  .build());
    }
}
