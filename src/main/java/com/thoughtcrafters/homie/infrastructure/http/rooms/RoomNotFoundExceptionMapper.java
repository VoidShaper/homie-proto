package com.thoughtcrafters.homie.infrastructure.http.rooms;

import com.thoughtcrafters.homie.domain.rooms.RoomNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import static java.lang.String.format;

public class RoomNotFoundExceptionMapper implements ExceptionMapper<RoomNotFoundException> {
    private static final Logger logger = LoggerFactory.getLogger(RoomNotFoundExceptionMapper.class);
    @Override
    public Response toResponse(RoomNotFoundException e) {
        logger.warn(e.getMessage());
        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(format("Room with id %s has not been found.",
                               e.roomId().uuid())).build();
    }
}
