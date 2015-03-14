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
import com.thoughtcrafters.homie.infrastructure.persistence.sqlite.SqliteConnectionFactory;
import com.thoughtcrafters.homie.infrastructure.persistence.sqlite.SqliteDbRebuildCommand;
import io.dropwizard.testing.junit.ConfigOverride;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.assertj.core.data.MapEntry;
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

public class LightsAcceptanceTest extends AcceptanceTest {

    public static String dbTestFile = "homieTest.db";

    @ClassRule
    public static final DropwizardAppRule<HomieConfiguration> app =
            new DropwizardAppRule<>(HomieApplication.class, "homie.yml",
                                    ConfigOverride.config("dbPath", dbTestFile));

    private DBI jdbiConnection = SqliteConnectionFactory.jdbiConnectionTo(dbTestFile);

    public static final String SWITCH_DESCRIPTION = "turn on or off";

    @Before
    public void setUp() throws Exception {
        new SqliteDbRebuildCommand().rebuildDb(jdbiConnection);
    }

    @After
    public void tearDown() throws Exception {
        new File(dbTestFile).delete();
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
                                        .resource(appliancesUri(id).build())
                                        .get(ClientResponse.class);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK_200);

        assertThat(response.getEntity(Map.class))
                .containsOnly(
                        MapEntry.entry("id", id.uuid().toString()),
                        MapEntry.entry("state", "IDLE"),
                        MapEntry.entry("name", "aName"),
                        MapEntry.entry("type", "LIGHT"),
                        MapEntry.entry("properties", ImmutableMap.<String, Object>of(
                                "switchState",
                                ImmutableMap.<String, Object>of(
                                        "value", "OFF",
                                        "description", "If the light is on or off.",
                                        "type", "ENUM",
                                        "editable", true,
                                        "availableValues", ImmutableList.of("ON", "OFF"))
                        )),
                        MapEntry.entry("self", "/appliances/" + id.uuid())
                );
    }

    @Test
    public void returns404WhenLightIsNotFound() {
        // given
        ApplianceId id = new ApplianceId(UUID.randomUUID());

        // when
        ClientResponse response = Client.create()
                                        .resource(appliancesUri(id).build())
                                        .get(ClientResponse.class);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND_404);

        assertThat(response.getEntity(String.class))
                .isEqualToIgnoringCase(format("Appliance with id %s has not been found.", id.uuid()));
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
                .isEqualToIgnoringCase(format("Appliance with id %s has not been found.", id.uuid()));
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
                .containsEntry("properties", ImmutableMap.of(
                        "switchState",
                        ImmutableMap.<String, Object>of(
                                "value", "ON",
                                "description", "If the light is on or off.",
                                "type", "ENUM",
                                "editable", true,
                                "availableValues", ImmutableList.of("ON", "OFF"))
                ))
                .containsEntry("state", "WORKING");

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
                .containsEntry("properties", ImmutableMap.of(
                        "switchState",
                        ImmutableMap.<String, Object>of(
                                "value", "OFF",
                                "description", "If the light is on or off.",
                                "type", "ENUM",
                                "editable", true,
                                "availableValues", ImmutableList.of("ON", "OFF"))
                ))
                .containsEntry("state", "IDLE");
    }

    @Test
    public void triedToUpdateSwitchStateToAnUnknownValue() throws IOException {
        // given
        ApplianceId id = aLightHasBeenCreatedWith("aName");

        // when
        CloseableHttpResponse response = aLightHasBeenTurnedTo(id, "OFS");

        // then
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST_400);
        assertThat(EntityUtils.toString(response.getEntity()))
                .isEqualTo(format("Appliance %s does not support update of the property %s to %s",
                                  id.uuid(), "switchState", "OFS"));
        assertThat(aLightResponseFor(id))
                .containsEntry("properties", ImmutableMap.of(
                        "switchState",
                        ImmutableMap.<String, Object>of(
                                "value", "OFF",
                                "description", "If the light is on or off.",
                                "type", "ENUM",
                                "editable", true,
                                "availableValues", ImmutableList.of("ON", "OFF"))
                ))
                .containsEntry("state", "IDLE");
    }

    private CloseableHttpResponse aLightHasBeenTurnedTo(ApplianceId id, SwitchState switchState) throws IOException {
        return aLightHasBeenTurnedTo(id, switchState.toString());
    }

    private CloseableHttpResponse aLightHasBeenTurnedTo(ApplianceId id, String switchState) throws IOException {
        ImmutableMap<String, Object> request = ImmutableMap.of("op", "replace",
                                                               "path", "/switchState",
                                                               "value", switchState);

        HttpPatch httpPatch = new HttpPatch(appliancesUri(id).build());
        httpPatch.setEntity(new StringEntity(jsonFrom(request)));
        httpPatch.addHeader("Content-Type", "application/json-patch+json");

        return HttpClients.createDefault().execute(httpPatch);
    }

    @Test
    public void getsBothAddedAppliancesWhenGettingAll() throws JsonProcessingException {
        // given
        ApplianceId lightOffId = aLightHasBeenCreatedWith("firstLight");
        ApplianceId secondLightOffId = aLightHasBeenCreatedWith("secondLight");

        // when
        ClientResponse response = Client.create()
                                        .resource(appliancesUri().build())
                                        .get(ClientResponse.class);

        // then
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat((List<Map<String, Object>>) response.getEntity(List.class))
                .containsOnly(
                        ImmutableMap.<String, Object>builder()
                                    .put("name", "firstLight")
                                    .put("id", lightOffId.uuid().toString())
                                    .put("state", "IDLE")
                                    .put("type", "LIGHT")
                                    .put("properties", ImmutableMap.<String, Object>of(
                                            "switchState",
                                            ImmutableMap.<String, Object>of(
                                                    "value", "OFF",
                                                    "description", "If the light is on or off.",
                                                    "type", "ENUM",
                                                    "editable", true,
                                                    "availableValues", ImmutableList.of("ON", "OFF"))
                                    ))
                                    .put("self", "/appliances/" + lightOffId.uuid())
                                    .build(),
                        ImmutableMap.<String, Object>builder()
                                    .put("name", "secondLight")
                                    .put("id", secondLightOffId.uuid().toString())
                                    .put("state", "IDLE")
                                    .put("type", "LIGHT")
                                    .put("properties", ImmutableMap.<String, Object>of(
                                            "switchState",
                                            ImmutableMap.<String, Object>of(
                                                    "value", "OFF",
                                                    "description", "If the light is on or off.",
                                                    "type", "ENUM",
                                                    "editable", true,
                                                    "availableValues", ImmutableList.of("ON", "OFF"))
                                    ))
                                    .put("self", "/appliances/" + secondLightOffId.uuid())
                                    .build()
                );
    }

    @Override
    public DropwizardAppRule<HomieConfiguration> app() {
        return app;
    }
}
