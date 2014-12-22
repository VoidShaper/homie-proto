package com.thoughtcrafters.homie.acceptance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableMap;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.thoughtcrafters.homie.HomieApplication;
import com.thoughtcrafters.homie.HomieConfguration;
import com.thoughtcrafters.homie.TestUtils;
import com.thoughtcrafters.homie.domain.ApplianceId;
import com.thoughtcrafters.homie.domain.behaviours.SwitchState;
import com.thoughtcrafters.homie.domain.lights.Light;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.assertj.core.api.AbstractCharSequenceAssert;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.ws.rs.core.MediaType;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.thoughtcrafters.homie.TestUtils.UUID_REGEX;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class LightsAcceptanceTest {

    @ClassRule
    public static final DropwizardAppRule<HomieConfguration> app =
            new DropwizardAppRule<>(HomieApplication.class, "homie.yml");
    private SwitchState switchState;

    @Parameterized.Parameters
    public static Collection switchStates() {
        return Arrays.asList(new Object[][]{
                {SwitchState.ON},
                {SwitchState.OFF}
        });
    }

    public LightsAcceptanceTest(SwitchState switchState) {
        this.switchState = switchState;
    }

    @Test
    public void createsALightCorrectly() throws JsonProcessingException {
        // given
        String requestEntity = jsonFrom(ImmutableMap.of(
                "name", "testName",
                "initialState", switchState.name()));


        // when
        ClientResponse response = Client.create()
                .resource(format("http://localhost:%d/lights", app.getLocalPort()))
                .entity(requestEntity, MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED_201);

        assertThat(response.getHeaders().getFirst("Location"))
                .matches(format("http://localhost:%d/lights/%s",
                        app.getLocalPort(), UUID_REGEX));
    }

    @Test
    public void getsACreatedALightCorrectly() throws JsonProcessingException {
        // given
        ApplianceId id = aLightHasBeenCreatedWith("aName", switchState);

        // when
        ClientResponse response = Client.create()
                .resource(format("http://localhost:%d/lights/%s", app.getLocalPort(), id.uuid()))
                .get(ClientResponse.class);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK_200);

        assertThat(response.getEntity(Map.class))
                .isEqualTo(ImmutableMap.of(
                        "switchState", switchState.name(),
                        "name", "aName"
                ));
    }

    @Test
    public void turnsALightOnCorrectly() throws JsonProcessingException {
        // given
        ApplianceId id = aLightHasBeenCreatedWith("aName", switchState);

        // when
        ClientResponse response = Client.create()
                .resource(format("http://localhost:%d/lights/%s/on",
                        app.getLocalPort(), id.uuid()))
                .post(ClientResponse.class);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT_204);

        assertThat(aLightResponseFor(id))
                .isEqualTo(ImmutableMap.of(
                        "switchState", SwitchState.ON.name(),
                        "name", "aName"
                ));
    }

    @Test
    public void turnsALightOffCorrectly() throws JsonProcessingException {
        // given
        ApplianceId id = aLightHasBeenCreatedWith("aName", switchState);

        // when
        ClientResponse response = Client.create()
                .resource(format("http://localhost:%d/lights/%s/off",
                        app.getLocalPort(), id.uuid()))
                .post(ClientResponse.class);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT_204);

        assertThat(aLightResponseFor(id))
                .isEqualTo(ImmutableMap.of(
                        "switchState", SwitchState.OFF.name(),
                        "name", "aName"
                ));
    }

    private Map<String, Object> aLightResponseFor(ApplianceId id) {
        ClientResponse response = Client.create()
                .resource(format("http://localhost:%d/lights/%s", app.getLocalPort(), id.uuid()))
                .get(ClientResponse.class);
        return response.getEntity(Map.class);
    }

    private ApplianceId aLightHasBeenCreatedWith(String aName, SwitchState initialState) throws JsonProcessingException {
        String requestEntity = jsonFrom(ImmutableMap.of(
                "name", aName,
                "initialState", initialState.name()));

        ClientResponse response = Client.create()
                .resource(format("http://localhost:%d/lights", app.getLocalPort()))
                .entity(requestEntity, MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class);

        String location = response.getHeaders().getFirst("Location");
        Pattern p = Pattern.compile(UUID_REGEX);
        Matcher m = p.matcher(location);
        m.find();
        return new ApplianceId(UUID.fromString(m.group(0)));
    }

    private String jsonFrom(ImmutableMap<String, String> request) throws JsonProcessingException {
        return app.getEnvironment().getObjectMapper()
                .writeValueAsString(request);
    }


}
