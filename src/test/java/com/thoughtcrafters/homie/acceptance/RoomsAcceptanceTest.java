package com.thoughtcrafters.homie.acceptance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableMap;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.thoughtcrafters.homie.HomieApplication;
import com.thoughtcrafters.homie.HomieConfiguration;
import com.thoughtcrafters.homie.domain.appliances.ApplianceId;
import com.thoughtcrafters.homie.domain.rooms.Point;
import com.thoughtcrafters.homie.domain.rooms.RoomId;
import com.thoughtcrafters.homie.infrastructure.persistence.sqlite.SqliteConnectionFactory;
import com.thoughtcrafters.homie.infrastructure.persistence.sqlite.SqliteDbRebuildCommand;
import io.dropwizard.testing.junit.ConfigOverride;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;

import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.thoughtcrafters.homie.TestUtils.UUID_REGEX;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

public class RoomsAcceptanceTest extends AcceptanceTest {

    public static String dbTestFile = "homieTest.db";

    @ClassRule
    public static final DropwizardAppRule<HomieConfiguration> app =
            new DropwizardAppRule<>(HomieApplication.class, "homie.yml",
                                    ConfigOverride.config("dbPath", dbTestFile));

    private DBI jdbiConnection = SqliteConnectionFactory.jdbiConnectionTo(dbTestFile);

    @Before
    public void setUp() throws Exception {
        new SqliteDbRebuildCommand().rebuildDb(jdbiConnection);
    }

    @After
    public void tearDown() throws Exception {
        app.<HomieApplication>getApplication().clearAllData();
        new File(dbTestFile).delete();
    }

    @Test
    public void createsARoomCorrectly() throws JsonProcessingException {
        // given
        String requestEntity = jsonFrom(
                ImmutableMap.<String, Object>of("name", "roomName",
                                                "shape", rectangle20x20()));

        // when
        ClientResponse response = Client.create()
                                        .resource(roomsUri().build())
                                        .entity(requestEntity, MediaType.APPLICATION_JSON_TYPE)
                                        .post(ClientResponse.class);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED_201);

        assertThat(response.getHeaders().getFirst("Location"))
                .matches(format("http://localhost:%d/rooms/%s",
                                app().getLocalPort(), UUID_REGEX));
    }

    @Test
    public void getsACreatedRoomCorrectly() throws JsonProcessingException {
        // given
        RoomId id = aRoomHasBeenCreatedWith("aRoomName", rectangle20x20());

        // when
        ClientResponse response = Client.create()
                                        .resource(roomsUri().path(id.uuid().toString()).build())
                                        .get(ClientResponse.class);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK_200);

        assertThat(response.getEntity(Map.class))
                .isEqualTo(ImmutableMap.of(
                        "name", "aRoomName",
                        "self", roomsUri(id).build().getPath(),
                        "id", id.uuid().toString(),
                        "appliances", ImmutableMap.of(),
                        "shape", rectangle20x20()
                ));
    }

    @Test
    public void placesAnApplianceInTheRoomWithAPatch() throws IOException {
        // given
        ApplianceId lightId = aLightHasBeenCreatedWith("lightName", false);
        RoomId id = aRoomHasBeenCreatedWith("aRoomName", rectangle20x20());

        // when
        CloseableHttpResponse response = anApplianceHasBeenAddedToTheRoom(id, lightId, new Point(5, 6));

        // then
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT_204);

        assertThat(aRoomResponseFor(id))
                .containsEntry("appliances",
                               ImmutableMap.of(lightId.uuid().toString(),
                                               ImmutableMap.of("point",
                                                               ImmutableMap.of("x", num(5),
                                                                               "y", num(6)))));

        assertThat(aLightResponseFor(lightId))
                .containsEntry("room", ImmutableMap.of("id", id.uuid().toString(),
                                                       "self", roomsUri(id).build().getPath(),
                                                       "name", "aRoomName"));
    }

    @Test
    public void removesAnApplianceFromTheRoom() throws IOException {
        // given
        ApplianceId lightId = aLightHasBeenCreatedWith("lightName", false);
        RoomId id = aRoomHasBeenCreatedWith("aRoomName", rectangle20x20());
        anApplianceHasBeenAddedToTheRoom(id, lightId, new Point(8, 12));

        String request = jsonFrom(ImmutableMap.of("op", "remove",
                                                  "path", "/appliances",
                                                  "value", lightId.uuid().toString()));

        // when
        HttpPatch httpPatch = new HttpPatch(roomsUri(id).build());
        httpPatch.setEntity(new StringEntity(request));
        httpPatch.addHeader("Content-Type", "application/json-patch+json");
        CloseableHttpResponse response = HttpClients.createDefault().execute(httpPatch);

        // then
        assertThat(response.getStatusLine().getStatusCode())
                .isEqualTo(HttpStatus.NO_CONTENT_204);

        assertThat(aRoomResponseFor(id))
                .containsEntry("appliances", ImmutableMap.of());

        assertThat(aLightResponseFor(lightId))
                .doesNotContainKey("roomId");
    }

    @Test
    public void placingAlreadyPlacedApplianceInTheSameRoomMovesItToNewPosition()
            throws IOException {
        // given
        ApplianceId lightId = aLightHasBeenCreatedWith("lightName", false);
        RoomId roomId = aRoomHasBeenCreatedWith("aFirstPlacementRoom", rectangle20x20());
        anApplianceHasBeenAddedToTheRoom(roomId, lightId, new Point(18, 32));

        // when
        CloseableHttpResponse response =
                anApplianceHasBeenAddedToTheRoom(roomId, lightId, new Point(87, 33.45));

        // then
        assertThat(response.getStatusLine().getStatusCode())
                .isEqualTo(HttpStatus.NO_CONTENT_204);

        assertThat(aLightResponseFor(lightId))
                .containsEntry("room", ImmutableMap.of("id", roomId.uuid().toString(),
                                                       "name", "aFirstPlacementRoom",
                                                       "self", roomsUri(roomId).build().getPath()));

        assertThat(aRoomResponseFor(roomId))
                .containsEntry("appliances",
                               ImmutableMap.of(lightId.uuid().toString(),
                                               ImmutableMap.of("point",
                                                               ImmutableMap.of("x", num(87),
                                                                               "y", num(33.45)))));
    }

    @Test
    public void placingAlreadyPlacedApplianceInADifferentRoomRemovesItFromTheOldRoom()
            throws IOException {
        // given
        ApplianceId lightId = aLightHasBeenCreatedWith("lightName", false);
        RoomId firstRoomId = aRoomHasBeenCreatedWith("aFirstPlacementRoom", rectangle20x20());
        anApplianceHasBeenAddedToTheRoom(firstRoomId, lightId, new Point(8, 12));
        RoomId secondRoomId = aRoomHasBeenCreatedWith("aSecondPlacementRoom", rectangle20x20());

        // when
        CloseableHttpResponse response =
                anApplianceHasBeenAddedToTheRoom(secondRoomId, lightId, new Point(9.5, 6.3));

        // then
        assertThat(response.getStatusLine().getStatusCode())
                .isEqualTo(HttpStatus.NO_CONTENT_204);

        assertThat(aLightResponseFor(lightId))
                .containsEntry("room", ImmutableMap.of("id", secondRoomId.uuid().toString(),
                                                       "name", "aSecondPlacementRoom",
                                                       "self", roomsUri(secondRoomId).build().getPath()));

        assertThat(aRoomResponseFor(secondRoomId))
                .containsEntry("appliances",
                               ImmutableMap.of(lightId.uuid().toString(),
                                               ImmutableMap.of("point",
                                                               ImmutableMap.of("x", num(9.5),
                                                                               "y", num(6.3)))));
        assertThat(aRoomResponseFor(firstRoomId))
                .containsEntry("appliances", ImmutableMap.of());
    }

    @Test
    public void getsAllTheRooms() throws Exception {
        // given
        RoomId id1 = aRoomHasBeenCreatedWith("nameOne", rectangle20x20());
        RoomId id2 = aRoomHasBeenCreatedWith("nameTwo", polygon5p());

        // when
        ClientResponse clientResponse = Client.create()
                                              .resource(roomsUri().build())
                                              .get(ClientResponse.class);

        // then
        assertThat(clientResponse.getStatus()).isEqualTo(HttpStatus.OK_200);
        assertThat((List<Map<String, Object>>) clientResponse.getEntity(List.class))
                .containsOnly(
                        ImmutableMap.of("name", "nameOne",
                                        "self", roomsUri(id1).build().getPath(),
                                        "appliances", ImmutableMap.of(),
                                        "id", id1.uuid().toString(),
                                        "shape", rectangle20x20()),
                        ImmutableMap.of("name", "nameTwo",
                                        "self", roomsUri(id2).build().getPath(),
                                        "appliances", ImmutableMap.of(),
                                        "id", id2.uuid().toString(),
                                        "shape", polygon5p()));
    }

    @Test
    public void returns404WhenLightIsNotFound() {
        // given
        RoomId roomId = new RoomId(UUID.randomUUID());

        // when
        ClientResponse response = Client.create()
                                        .resource(roomsUri(roomId).build())
                                        .get(ClientResponse.class);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND_404);

        assertThat(response.getEntity(String.class))
                .isEqualToIgnoringCase(format("Room with id %s has not been found.",
                                              roomId.uuid()));
    }

    @Override
    public DropwizardAppRule<HomieConfiguration> app() {
        return app;
    }
}
