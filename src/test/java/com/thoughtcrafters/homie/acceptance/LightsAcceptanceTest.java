package com.thoughtcrafters.homie.acceptance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableMap;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.thoughtcrafters.homie.HomieApplication;
import com.thoughtcrafters.homie.HomieConfiguration;
import com.thoughtcrafters.homie.domain.appliances.ApplianceId;
import com.thoughtcrafters.homie.domain.behaviours.SwitchState;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.ws.rs.core.MediaType;

import java.util.*;

import static com.thoughtcrafters.homie.TestUtils.UUID_REGEX;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class LightsAcceptanceTest extends AcceptanceTest {

    @ClassRule
    public static final DropwizardAppRule<HomieConfiguration> app =
            new DropwizardAppRule<>(HomieApplication.class, "homie.yml");

    private SwitchState switchState;

    @Parameterized.Parameters
    public static Collection switchStates() {
        return Arrays.asList(new Object[][]{
                {SwitchState.ON},
                {SwitchState.OFF}
        });
    }

    @After
    public void tearDown() throws Exception {
        app.<HomieApplication>getApplication().clearAllData();
    }

    public LightsAcceptanceTest(SwitchState switchState) {
        this.switchState = switchState;
    }

    @Test
    public void createsALightCorrectly() throws JsonProcessingException {
        // given
        String requestEntity = jsonFrom(ImmutableMap.of(
                "name", "testName",
                "initialState", switchState.name(),
                "type", "LIGHT"));

        // when
        ClientResponse response = Client.create()
                                        .resource(appliancesUri().build())
                                        .entity(requestEntity, MediaType.APPLICATION_JSON_TYPE)
                                        .post(ClientResponse.class);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED_201);

        assertThat(response.getHeaders().getFirst("Location"))
                .matches(format("%s/%s", appliancesUri().build(), UUID_REGEX));
    }

    @Test
    public void getsACreatedLightCorrectly() throws JsonProcessingException {
        // given
        ApplianceId id = aLightHasBeenCreatedWith("aName", switchState);

        // when
        ClientResponse response = Client.create()
                                        .resource(appliancesUri().path(id.uuid().toString()).build())
                                        .get(ClientResponse.class);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK_200);

        assertThat(response.getEntity(Map.class))
                .isEqualTo(ImmutableMap.of(
                        "switchState", switchState.name(),
                        "name", "aName",
                        "type", "LIGHT"
                ));
    }

    @Test
    public void returns404WhenLightIsNotFound() {
        // given
        ApplianceId id = new ApplianceId(UUID.randomUUID());

        // when
        ClientResponse response = Client.create()
                                        .resource(appliancesUri().path(id.uuid().toString()).build())
                                        .get(ClientResponse.class);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND_404);

        assertThat(response.getEntity(String.class))
                .isEqualToIgnoringCase(format("Light with id %s has not been found.", id.uuid()));
    }

    @Test
    public void turnsALightOnCorrectly() throws JsonProcessingException {
        // given
        ApplianceId id = aLightHasBeenCreatedWith("aName", switchState);

        // when
        ClientResponse response = Client.create()
                                        .resource(appliancesUri().path(id.uuid().toString()).path("on").build())
                                        .post(ClientResponse.class);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT_204);

        assertThat(aLightResponseFor(id))
                .isEqualTo(ImmutableMap.of(
                        "switchState", SwitchState.ON.name(),
                        "name", "aName",
                        "type", "LIGHT"
                ));
    }

    @Test
    public void turnsALightOffCorrectly() throws JsonProcessingException {
        // given
        ApplianceId id = aLightHasBeenCreatedWith("aName", switchState);

        // when
        ClientResponse response = Client.create()
                                        .resource(appliancesUri().path(id.uuid().toString()).path("off").build())
                                        .post(ClientResponse.class);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT_204);

        assertThat(aLightResponseFor(id))
                .isEqualTo(ImmutableMap.of(
                        "switchState", SwitchState.OFF.name(),
                        "name", "aName",
                        "type", "LIGHT"
                ));
    }

    @Test
    public void returns404WhenLightIsNotFoundWhenTryingToChangeTheSwitch() {
        // given
        ApplianceId id = new ApplianceId(UUID.randomUUID());

        // when
        ClientResponse response =
                Client.create()
                      .resource(appliancesUri().path(id.uuid().toString())
                                               .path(switchState.name().toLowerCase()).build())
                      .post(ClientResponse.class);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND_404);
        assertThat(response.getEntity(String.class))
                .isEqualToIgnoringCase(format("Light with id %s has not been found.", id.uuid()));
    }

    @Test
    public void getsAnEmptyListOfAppliancesIfNoneWereAddedWhenGettingAll()
            throws JsonProcessingException {

        // when
        ClientResponse response = Client.create()
                                        .resource(appliancesUri().build())
                                        .get(ClientResponse.class);

        // then
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getEntity(List.class)).isEmpty();
    }

    @Test
    public void getsBothAddedAppliancesWhenGettingAll() throws JsonProcessingException {
        // given
        ApplianceId lightOffId = aLightHasBeenCreatedWith("firstLightOff", SwitchState.OFF);
        ApplianceId lIghtOnId = aLightHasBeenCreatedWith("secondLightOn", SwitchState.ON);

        // when
        ClientResponse response = Client.create()
                                        .resource(appliancesUri().build())
                                        .get(ClientResponse.class);

        // then
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat((List<Map<String, String>>) response.getEntity(List.class))
                .containsOnly(
                        ImmutableMap.of("name", "firstLightOff",
                                        "switchState", SwitchState.OFF.name(),
                                        "id", lightOffId.uuid().toString(),
                                        "type", "LIGHT"),
                        ImmutableMap.of("name", "secondLightOn",
                                        "switchState", SwitchState.ON.name(),
                                        "id", lIghtOnId.uuid().toString(),
                                        "type", "LIGHT"));
    }

    @Override
    public DropwizardAppRule<HomieConfiguration> app() {
        return app;
    }
}
