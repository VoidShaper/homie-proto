package com.thoughtcrafters.homie.infrastructure.http.appliances;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.thoughtcrafters.homie.domain.appliances.operations.PropertyUpdateDefinition;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;

public class PropertyUpdateSerializer extends JsonSerializer<PropertyUpdateDefinition> {
    @Override
    public void serialize(PropertyUpdateDefinition propertyUpdateDefinition,
                          JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider)
            throws IOException, JsonProcessingException {

        URI uri = UriBuilder.fromPath("/appliances/{applianceId}")
                            .build(propertyUpdateDefinition.applianceId().uuid().toString());
        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField("description", propertyUpdateDefinition.description());
        jsonGenerator.writeStringField("uri", uri.toString());
        jsonGenerator.writeObjectField("method", "PATCH");
        jsonGenerator.writeStringField("property", propertyUpdateDefinition.property());
        jsonGenerator.writeStringField("propertyType", propertyUpdateDefinition.propertyType().toString());

        if(propertyUpdateDefinition.enumValues().isPresent()) {
            jsonGenerator.writeArrayFieldStart("enumValues");
            for(String availableValue : propertyUpdateDefinition.enumValues().get()) {
                jsonGenerator.writeString(availableValue);
            }
            jsonGenerator.writeEndArray();
        }
        jsonGenerator.writeEndObject();
    }
}
