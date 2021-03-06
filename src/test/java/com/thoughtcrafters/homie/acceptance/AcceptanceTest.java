package com.thoughtcrafters.homie.acceptance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.thoughtcrafters.homie.HomieConfiguration;
import com.thoughtcrafters.homie.domain.appliances.ApplianceId;
import com.thoughtcrafters.homie.domain.rooms.Point;
import com.thoughtcrafters.homie.domain.rooms.RoomId;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.thoughtcrafters.homie.TestUtils.UUID_REGEX;
import static java.lang.String.format;

public abstract class AcceptanceTest {

    protected static final String APPLIANCES_PATH = "appliances";
    protected static final String ROOMS_PATH = "rooms";

    protected UriBuilder appliancesUri() {
        return UriBuilder.fromPath(format("http://localhost:%d", app().getLocalPort()))
                         .path(APPLIANCES_PATH);
    }

    protected UriBuilder appliancesUri(ApplianceId applianceId) {
        return UriBuilder.fromPath(format("http://localhost:%d", app().getLocalPort()))
                         .path(APPLIANCES_PATH)
                         .path(applianceId.uuid().toString());
    }

    protected UriBuilder roomsUri(RoomId roomId) {
        return roomsUri().path(roomId.uuid().toString());
    }

    protected UriBuilder roomsUri() {
        return UriBuilder.fromPath(format("http://localhost:%d", app().getLocalPort()))
                         .path(ROOMS_PATH);
    }

    protected RoomId aRoomHasBeenCreatedWith(String aRoomName,
                                             ImmutableList<ImmutableMap<String, Object>> shape)
            throws JsonProcessingException {
        String requestEntity = jsonFrom(ImmutableMap.of("name", aRoomName,
                                                        "shape", shape));

        ClientResponse response = Client.create()
                                        .resource(roomsUri().build())
                                        .entity(requestEntity, MediaType.APPLICATION_JSON_TYPE)
                                        .post(ClientResponse.class);

        String uuid = uuidFrom(response);
        return new RoomId(UUID.fromString(uuid));
    }

    protected Map<String, Object> aRoomResponseFor(RoomId id) {
        ClientResponse response = Client.create()
                                        .resource(roomsUri().path(id.uuid().toString()).build())
                                        .get(ClientResponse.class);
        return response.getEntity(Map.class);
    }

    protected CloseableHttpResponse anApplianceHasBeenAddedToTheRoom(RoomId id, ApplianceId applianceId, Point point)
            throws IOException {
        ImmutableMap<String, Object> value =
                ImmutableMap.<String, Object>of("applianceId", applianceId.uuid().toString(),
                                                "point", ImmutableMap.of("x", point.x(),
                                                                         "y", point.y()));

        String request = jsonFrom(ImmutableMap.of("op", "add",
                                                  "path", "/appliances",
                                                  "value", value));

        // when
        HttpPatch httpPatch = new HttpPatch(roomsUri(id).build());
        httpPatch.setEntity(new StringEntity(request));
        httpPatch.addHeader("Content-Type", "application/json-patch+json");
        return HttpClients.createDefault().execute(httpPatch);
    }

    protected ImmutableList<ImmutableMap<String, Object>> rectangle20x20() {
        return ImmutableList.of(
                ImmutableMap.<String, Object>of("x", num(0), "y", num(0)),
                ImmutableMap.<String, Object>of("x", num(20), "y", num(0)),
                ImmutableMap.<String, Object>of("x", num(20), "y", num(20)),
                ImmutableMap.<String, Object>of("x", num(0), "y", num(20)));
    }

    protected ImmutableList<ImmutableMap<String, Object>> polygon5p() {
        return ImmutableList.of(
                ImmutableMap.<String, Object>of("x", num(1), "y", num(1)),
                ImmutableMap.<String, Object>of("x", num(4), "y", num(4)),
                ImmutableMap.<String, Object>of("x", num(3), "y", num(5)),
                ImmutableMap.<String, Object>of("x", num(2), "y", num(5)),
                ImmutableMap.<String, Object>of("x", num(0), "y", num(2)));
    }

    protected Double num(double number) {
        return new Double(number);
    }

    public abstract DropwizardAppRule<HomieConfiguration> app();

    public Map<String, Object> aLightResponseFor(ApplianceId id) {
        ClientResponse response = Client.create()
                                        .resource(appliancesUri().path(id.uuid().toString()).build())
                                        .get(ClientResponse.class);
        return response.getEntity(Map.class);
    }

    public ApplianceId aLightHasBeenCreatedWith(String aName, boolean dimmable)
            throws JsonProcessingException {
        String requestEntity = jsonFrom(ImmutableMap.of(
                "name", aName,
                "type", "LIGHT",
                "dimmable", dimmable));

        ClientResponse response = Client.create()
                                        .resource(appliancesUri().build())
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

    public String jsonFrom(ImmutableMap<String, Object> request) throws JsonProcessingException {
        return app().getEnvironment().getObjectMapper()
                    .writeValueAsString(request);
    }

}
