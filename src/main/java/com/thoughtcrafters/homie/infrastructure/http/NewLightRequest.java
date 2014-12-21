package com.thoughtcrafters.homie.infrastructure.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.thoughtcrafters.homie.domain.behaviours.SwitchState;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NewLightRequest {
    @NotEmpty
    private String name;
    @Valid
    private SwitchState initialState;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SwitchState getInitialState() {
        return initialState;
    }

    public void setInitialState(SwitchState initialState) {
        this.initialState = initialState;
    }
}
