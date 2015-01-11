package com.thoughtcrafters.homie.infrastructure.http.appliances;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.thoughtcrafters.homie.domain.appliances.operations.PropertyUpdate;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

public class PropertyUpdateSerializer extends JsonSerializer<PropertyUpdate> {
    @Override
    public void serialize(PropertyUpdate propertyUpdate,
                          JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider)
            throws IOException, JsonProcessingException {

        URI uri = UriBuilder.fromPath("/appliances/{applianceId}")
                            .build(propertyUpdate.applianceId().uuid().toString());
        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField("description", propertyUpdate.description());
        jsonGenerator.writeStringField("uri", uri.toString());
        jsonGenerator.writeObjectField("method", "PATCH");
        jsonGenerator.writeStringField("property", propertyUpdate.property());
        jsonGenerator.writeStringField("propertyType", propertyUpdate.propertyType().toString());

        if(propertyUpdate.enumValues().isPresent()) {
            jsonGenerator.writeArrayFieldStart("enumValues");
            for(String availableValue : propertyUpdate.enumValues().get()) {
                jsonGenerator.writeString(availableValue);
            }
            jsonGenerator.writeEndArray();
        }
        jsonGenerator.writeEndObject();
    }
}
