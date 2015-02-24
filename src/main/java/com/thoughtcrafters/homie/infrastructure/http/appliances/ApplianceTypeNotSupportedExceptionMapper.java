package com.thoughtcrafters.homie.infrastructure.http.appliances;

import com.thoughtcrafters.homie.domain.appliances.ApplianceTypeNotSupportedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class ApplianceTypeNotSupportedExceptionMapper implements ExceptionMapper<ApplianceTypeNotSupportedException> {

    private static final Logger logger = LoggerFactory.getLogger(LightNotFoundExceptionMapper.class);

    @Override
    public Response toResponse(ApplianceTypeNotSupportedException e) {
        logger.warn(e.getMessage());
        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(e.getMessage())
                .build();
    }
}
