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

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getApplicationVersion() {
        return applicationVersion;
    }

    public void setApplicationVersion(String applicationVersion) {
        this.applicationVersion = applicationVersion;
    }
}
