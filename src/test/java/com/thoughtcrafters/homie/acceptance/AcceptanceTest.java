package com.thoughtcrafters.homie.acceptance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableMap;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.thoughtcrafters.homie.HomieConfguration;
import com.thoughtcrafters.homie.TestUtils;
import com.thoughtcrafters.homie.domain.ApplianceId;
import com.thoughtcrafters.homie.domain.behaviours.SwitchState;
import com.thoughtcrafters.homie.domain.rooms.RoomId;
import io.dropwizard.testing.junit.DropwizardAppRule;

import javax.ws.rs.core.MediaType;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.thoughtcrafters.homie.TestUtils.UUID_REGEX;
import static java.lang.String.format;

public abstract class AcceptanceTest {

    protected RoomId aRoomHasBeenCreatedWith(String aRoomName) throws JsonProcessingException {
        String requestEntity = jsonFrom(ImmutableMap.of("name", aRoomName));

        ClientResponse response = Client.create()
                .resource(format("http://localhost:%d/rooms", app().getLocalPort()))
                .entity(requestEntity, MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class);

        String uuid = uuidFrom(response);
        return new RoomId(UUID.fromString(uuid));
    }

    public abstract DropwizardAppRule<HomieConfguration> app();

    public Map<String, Object> aLightResponseFor(ApplianceId id) {
        ClientResponse response = Client.create()
                .resource(format("http://localhost:%d/lights/%s", app().getLocalPort(), id.uuid()))
                .get(ClientResponse.class);
        return response.getEntity(Map.class);
    }

    public ApplianceId aLightHasBeenCreatedWith(String aName, SwitchState initialState)
            throws JsonProcessingException {
        String requestEntity = jsonFrom(ImmutableMap.of(
                "name", aName,
                "initialState", initialState.name()));

        ClientResponse response = Client.create()
                .resource(format("http://localhost:%d/lights", app().getLocalPort()))
                .entity(requestEntity, MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class);

        String uuid = uuidFrom(response);
        return new ApplianceId(UUID.fromString(uuid));
    }

    private String uuidFrom(ClientResponse response) {
        String location = response.getHeaders().getFirst("Location");
        Pattern p = Pattern.compile(UUID_REGEX);
        Matcher m = p.matcher(location);
        m.find();
        return m.group(0);
    }

    public String jsonFrom(ImmutableMap<String, String> request) throws JsonProcessingException {
        return app().getEnvironment().getObjectMapper()
                .writeValueAsString(request);
    }

}
