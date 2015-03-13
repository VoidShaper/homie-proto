package com.thoughtcrafters.homie.infrastructure.http.appliances;

import com.thoughtcrafters.homie.domain.appliances.properties.PropertyUpdateNotAvailableException;
import org.eclipse.jetty.http.HttpStatus;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class PropertyUpdateNotAvailableExceptionMapper implements ExceptionMapper<PropertyUpdateNotAvailableException> {

    @Override
    public Response toResponse(PropertyUpdateNotAvailableException e) {
        return Response.status(HttpStatus.BAD_REQUEST_400)
                .entity(e.getMessage())
                .build();
    }
}
