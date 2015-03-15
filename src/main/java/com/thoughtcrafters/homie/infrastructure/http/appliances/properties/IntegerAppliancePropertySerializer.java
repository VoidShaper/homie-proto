package com.thoughtcrafters.homie.infrastructure.http.appliances.properties;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.thoughtcrafters.homie.domain.appliances.properties.IntegerProperty;

import java.io.IOException;

public class IntegerAppliancePropertySerializer extends JsonSerializer<IntegerProperty> {
    @Override
    public void serialize(IntegerProperty integerProperty,
                          JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider)
            throws IOException, JsonProcessingException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeNumberField("value", integerProperty.value());
        jsonGenerator.writeStringField("description", integerProperty.description());

        jsonGenerator.writeStringField("type", integerProperty.type().name());
        jsonGenerator.writeBooleanField("editable", integerProperty.editable());

        jsonGenerator.writeNumberField("min", integerProperty.min());
        jsonGenerator.writeNumberField("max", integerProperty.max());

        jsonGenerator.writeEndObject();
    }
}
