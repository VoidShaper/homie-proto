package com.thoughtcrafters.homie.infrastructure.http.appliances;

import com.thoughtcrafters.homie.domain.appliances.NoMatchingOperationForExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class NoMatchingOperationForExecutionExceptionMapper implements ExceptionMapper<NoMatchingOperationForExecutionException> {

    private static final Logger logger = LoggerFactory.getLogger(LightNotFoundExceptionMapper.class);

    @Override
    public Response toResponse(
            NoMatchingOperationForExecutionException e) {
        logger.warn(e.getMessage());
        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(e.getMessage())
                .build();
    }
}
