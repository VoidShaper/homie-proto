package com.thoughtcrafters.homie.infrastructure.http.mappers;

import com.thoughtcrafters.homie.domain.LightNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import static java.lang.String.format;

public class LightNotFoundExceptionMapper implements ExceptionMapper<LightNotFoundException> {

    private static final Logger logger = LoggerFactory.getLogger(LightNotFoundExceptionMapper.class);

    @Override
    public Response toResponse(LightNotFoundException e) {
        logger.warn(e.getMessage());
        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(format("Light with id %s has not been found.", e.lightId().uuid())).build();
    }
}
