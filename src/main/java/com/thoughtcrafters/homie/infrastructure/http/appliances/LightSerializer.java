package com.thoughtcrafters.homie.infrastructure.http.appliances;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.thoughtcrafters.homie.domain.appliances.lights.Light;
import com.thoughtcrafters.homie.domain.appliances.operations.OperationDefinition;

import java.io.IOException;

public class LightSerializer extends JsonSerializer<Light> {

    @Override
    public void serialize(Light light,
                          JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider)
            throws IOException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField("id", light.id().uuid().toString());
        jsonGenerator.writeStringField("name", light.name());
        jsonGenerator.writeStringField("type", light.type().toString());

        if(light.roomId().isPresent()) {
            jsonGenerator.writeStringField("roomId", light.roomId().get().uuid().toString());
        }
        jsonGenerator.writeStringField("switchState", light.switchState().toString());

        jsonGenerator.writeArrayFieldStart("operations");

        for(OperationDefinition operationDefinition : light.definedOperations()) {
            jsonGenerator.writeObject(operationDefinition);
        }

        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }
}
