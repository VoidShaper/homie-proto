package com.thoughtcrafters.homie.acceptance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.thoughtcrafters.homie.HomieApplication;
import com.thoughtcrafters.homie.HomieConfiguration;
import com.thoughtcrafters.homie.domain.appliances.ApplianceId;
import com.thoughtcrafters.homie.domain.behaviours.SwitchState;
import com.thoughtcrafters.homie.domain.rooms.RoomId;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.thoughtcrafters.homie.TestUtils.UUID_REGEX;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

public class RoomsAcceptanceTest extends AcceptanceTest {

    @ClassRule
    public static final DropwizardAppRule<HomieConfiguration> app =
            new DropwizardAppRule<>(HomieApplication.class, "homie.yml");

    @After
    public void tearDown() throws Exception {
        app.<HomieApplication>getApplication().clearAllData();
    }

    @Test
    public void createsARoomCorrectly() throws JsonProcessingException {
        // given
        String requestEntity = jsonFrom(
                ImmutableMap.<String, Object>of("name", "roomName",
                                                "shape", rectangle2x2()));

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
        RoomId id = aRoomHasBeenCreatedWith("aRoomName", rectangle2x2());

        // when
        ClientResponse response = Client.create()
                                        .resource(roomsUri().path(id.uuid().toString()).build())
                                        .get(ClientResponse.class);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK_200);

        assertThat(response.getEntity(Map.class))
                .isEqualTo(ImmutableMap.of(
                        "name", "aRoomName",
                        "appliances", new ArrayList<ApplianceId>(),
                        "shape", rectangle2x2()
                ));
    }

    @Test
    public void addsAnApplianceToTheRoom() throws JsonProcessingException {
        // given
        ApplianceId lightId = aLightHasBeenCreatedWith("lightName", SwitchState.ON);
        RoomId id = aRoomHasBeenCreatedWith("aRoomName", rectangle2x2());

        // when
        ClientResponse response = Client.create()
                                        .resource(roomsUri().path(id.uuid().toString())
                                                            .path("add")
                                                            .path(lightId.uuid().toString())
                                                            .build())
                                        .post(ClientResponse.class);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT_204);

        assertThat(aRoomResponseFor(id))
                .containsEntry("appliances", newArrayList(lightId.uuid().toString()));

        assertThat(aLightResponseFor(lightId))
                .containsEntry("roomId", id.uuid().toString());
    }

    @Test
    public void removesAnApplianceFromTheRoom() throws JsonProcessingException {
        // given
        ApplianceId lightId = aLightHasBeenCreatedWith("lightName", SwitchState.ON);
        RoomId id = aRoomHasBeenCreatedWith("aRoomName", rectangle2x2());
        anApplianceHasBeenAddedToTheRoom(id, lightId);

        // when
        ClientResponse response =
                Client.create()
                      .resource(roomsUri().path(id.uuid().toString())
                                          .path("remove")
                                          .path(lightId.uuid().toString())
                                          .build())
                      .post(ClientResponse.class);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT_204);

        assertThat(aRoomResponseFor(id))
                .containsEntry("appliances", newArrayList());

        assertThat(aLightResponseFor(lightId))
                .doesNotContainKey("roomId");
    }

    @Test
    public void getsAllTheRooms() throws Exception {
        // given
        RoomId id1 = aRoomHasBeenCreatedWith("nameOne", rectangle2x2());
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
                                        "appliances", ImmutableList.of(),
                                        "id", id1.uuid().toString(),
                                        "shape", rectangle2x2()),
                        ImmutableMap.of("name", "nameTwo",
                                        "appliances", ImmutableList.of(),
                                        "id", id2.uuid().toString(),
                                        "shape", polygon5p()));
    }

    @Override
    public DropwizardAppRule<HomieConfiguration> app() {
        return app;
    }
}
