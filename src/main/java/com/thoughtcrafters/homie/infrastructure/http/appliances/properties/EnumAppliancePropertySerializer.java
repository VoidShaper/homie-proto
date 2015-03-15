package com.thoughtcrafters.homie.infrastructure.http.appliances.properties;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.thoughtcrafters.homie.domain.appliances.properties.EnumProperty;

import java.io.IOException;

public class EnumAppliancePropertySerializer extends JsonSerializer<EnumProperty> {
    @Override
    public void serialize(EnumProperty enumProperty,
                          JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider)
            throws IOException, JsonProcessingException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField("value", enumProperty.value().name());
        jsonGenerator.writeStringField("description", enumProperty.description());

        jsonGenerator.writeStringField("type", enumProperty.type().name());
        jsonGenerator.writeBooleanField("editable", enumProperty.editable());

        jsonGenerator.writeArrayFieldStart("availableValues");
        for (Enum availableValue : enumProperty.availableValues()) {
            jsonGenerator.writeString(availableValue.name());
        }

        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }
}
