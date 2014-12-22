package com.thoughtcrafters.homie.acceptance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableMap;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.thoughtcrafters.homie.HomieApplication;
import com.thoughtcrafters.homie.HomieConfiguration;
import com.thoughtcrafters.homie.domain.ApplianceId;
import com.thoughtcrafters.homie.domain.behaviours.SwitchState;
import com.thoughtcrafters.homie.domain.rooms.RoomId;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.core.MediaType;

import java.util.ArrayList;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.thoughtcrafters.homie.TestUtils.UUID_REGEX;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

public class RoomsAcceptanceTest extends AcceptanceTest {

    @ClassRule
    public static final DropwizardAppRule<HomieConfiguration> app =
            new DropwizardAppRule<>(HomieApplication.class, "homie.yml");

    @Test
    public void createsARoomCorrectly() throws JsonProcessingException {
        // given
        String requestEntity = jsonFrom(ImmutableMap.of("name", "roomName"));

        // when
        ClientResponse response = Client.create()
                .resource(format("http://localhost:%d/rooms", app().getLocalPort()))
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
        RoomId id = aRoomHasBeenCreatedWith("aRoomName");

        // when
        ClientResponse response = Client.create()
                .resource(format("http://localhost:%d/rooms/%s", app.getLocalPort(), id.uuid()))
                .get(ClientResponse.class);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK_200);

        assertThat(response.getEntity(Map.class))
                .isEqualTo(ImmutableMap.of(
                        "name", "aRoomName",
                        "appliances", new ArrayList<ApplianceId>()
                ));
    }

    @Test
    public void addsAnApplianceToTheRoom() throws JsonProcessingException {
        // given
        ApplianceId lightId = aLightHasBeenCreatedWith("lightName", SwitchState.ON);
        RoomId id = aRoomHasBeenCreatedWith("aRoomName");

        // when
        ClientResponse response = Client.create()
                .resource(format("http://localhost:%d/rooms/%s/add/%s",
                        app.getLocalPort(), id.uuid(), lightId.uuid()))
                .post(ClientResponse.class);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT_204);

        assertThat(aRoomResponseFor(id))
                .isEqualTo(ImmutableMap.of(
                        "name", "aRoomName",
                        "appliances", newArrayList(lightId.uuid().toString())
                ));

        assertThat(aLightResponseFor(lightId))
                .isEqualTo(ImmutableMap.of(
                        "name", "lightName",
                        "switchState", "ON",
                        "roomId", id.uuid().toString()
                ));
    }

    @Override
    public DropwizardAppRule<HomieConfiguration> app() {
        return app;
    }
}
