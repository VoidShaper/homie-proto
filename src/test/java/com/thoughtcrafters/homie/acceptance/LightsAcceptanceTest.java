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
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import java.io.IOException;
import java.util.*;

import static com.thoughtcrafters.homie.TestUtils.UUID_REGEX;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

public class LightsAcceptanceTest extends AcceptanceTest {

    @ClassRule
    public static final DropwizardAppRule<HomieConfiguration> app =
            new DropwizardAppRule<>(HomieApplication.class, "homie.yml");

    public static final String SWITCH_DESCRIPTION = "turn on or off";

    @After
    public void tearDown() throws Exception {
        app.<HomieApplication>getApplication().clearAllData();
    }

    @Test
    public void createsALightCorrectly() throws JsonProcessingException {
        // given
        String requestEntity = jsonFrom(ImmutableMap.of(
                "name", "testName",
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
        ApplianceId id = aLightHasBeenCreatedWith("aName");

        // when
        ClientResponse response = Client.create()
                                        .resource(appliancesUri().path(id.uuid().toString()).build())
                                        .get(ClientResponse.class);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK_200);

        assertThat(response.getEntity(Map.class))
                .isEqualTo(ImmutableMap.of(
                        "id", id.uuid().toString(),
                        "switchState", SwitchState.OFF.name(),
                        "name", "aName",
                        "type", "LIGHT",
                        "operations", ImmutableList.of(
                                patchEnum(id,
                                          "switchState",
                                          SWITCH_DESCRIPTION,
                                          ImmutableList.of("ON", "OFF")))
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
    public void returns404WhenLightIsNotFoundWhenTryingToChangeTheSwitch() throws IOException {
        // given
        ApplianceId id = new ApplianceId(UUID.randomUUID());

        ImmutableMap<String, Object> request = ImmutableMap.of("op", "replace",
                                                               "path", "/switchState",
                                                               "value", "ON");

        // when
        HttpPatch httpPatch = new HttpPatch(appliancesUri(id).build());
        httpPatch.setEntity(new StringEntity(jsonFrom(request)));
        httpPatch.addHeader("Content-Type", "application/json-patch+json");

        CloseableHttpResponse response = HttpClients.createDefault().execute(httpPatch);

        // then
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND_404);
        assertThat(EntityUtils.toString(response.getEntity()))
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
    public void turnsTheLightOnWithAnUpdate() throws IOException {
        // given
        ApplianceId id = aLightHasBeenCreatedWith("aName");

        // when
        CloseableHttpResponse response = aLightHasBeenTurnedTo(id, SwitchState.ON);

        // then
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT_204);
        assertThat(aLightResponseFor(id))
                .containsEntry("switchState", SwitchState.ON.name());
    }

    @Test
    public void turnsTheLightOffWithAnUpdate() throws IOException {
        // given
        ApplianceId id = aLightHasBeenCreatedWith("aName");
        aLightHasBeenTurnedTo(id, SwitchState.ON);

        // when
        CloseableHttpResponse response = aLightHasBeenTurnedTo(id, SwitchState.OFF);

        // then
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT_204);
        assertThat(aLightResponseFor(id))
                .containsEntry("switchState", SwitchState.OFF.name());
    }

    private CloseableHttpResponse aLightHasBeenTurnedTo(ApplianceId id, SwitchState switchState) throws IOException {
        ImmutableMap<String, Object> request = ImmutableMap.of("op", "replace",
                                                               "path", "/switchState",
                                                               "value", switchState.toString());

        HttpPatch httpPatch = new HttpPatch(appliancesUri(id).build());
        httpPatch.setEntity(new StringEntity(jsonFrom(request)));
        httpPatch.addHeader("Content-Type", "application/json-patch+json");

        CloseableHttpResponse response = HttpClients.createDefault().execute(httpPatch);
        response.close();
        return response;
    }

    @Test
    public void getsBothAddedAppliancesWhenGettingAll() throws JsonProcessingException {
        // given
        ApplianceId lightOffId = aLightHasBeenCreatedWith("firstLight");
        ApplianceId lightOnId = aLightHasBeenCreatedWith("secondLight");

        // when
        ClientResponse response = Client.create()
                                        .resource(appliancesUri().build())
                                        .get(ClientResponse.class);

        // then
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat((List<Map<String, Object>>) response.getEntity(List.class))
                .containsOnly(
                        ImmutableMap.of("name", "firstLight",
                                        "switchState", SwitchState.OFF.name(),
                                        "id", lightOffId.uuid().toString(),
                                        "type", "LIGHT",
                                        "operations",
                                        ImmutableList.of(
                                                patchEnum(lightOffId, "switchState", SWITCH_DESCRIPTION,
                                                          ImmutableList.of("ON", "OFF")
                                                ))),
                        ImmutableMap.of("name", "secondLight",
                                        "switchState", SwitchState.OFF.name(),
                                        "id", lightOnId.uuid().toString(),
                                        "type", "LIGHT",
                                        "operations",
                                        ImmutableList.of(
                                                patchEnum(lightOnId, "switchState", SWITCH_DESCRIPTION,
                                                          ImmutableList.of("ON", "OFF")
                                                ))));
    }

    private ImmutableMap<String, Object> patchEnum(ApplianceId id,
                                                   String field,
                                                   String description,
                                                   ImmutableList<String> enumValues) {
        return ImmutableMap.<String, Object>builder()
                           .put("uri", UriBuilder.fromPath("/appliances/{applianceId}")
                                                 .build(id.uuid().toString())
                                                 .toString())
                           .put("description", description)
                           .put("method", "PATCH")
                           .put("property", field)
                           .put("propertyType", "ENUM")
                           .put("enumValues", enumValues)
                           .build();
    }

    @Override
    public DropwizardAppRule<HomieConfiguration> app() {
        return app;
    }
}
