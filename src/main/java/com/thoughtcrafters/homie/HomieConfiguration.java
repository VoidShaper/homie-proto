package com.thoughtcrafters.homie;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

public class HomieConfiguration extends Configuration {
    @NotEmpty
    @JsonProperty
    private String applicationName = "Homie";

    @NotEmpty
    @JsonProperty
    private String applicationVersion = "v0.1";

    @NotEmpty
    @JsonProperty
    private String dbPath = "homie.db";

    public String applicationName() {
        return applicationName;
    }

    public String applicationVersion() {
        return applicationVersion;
    }

    public String dbPath() {
        return dbPath;
    }
}
