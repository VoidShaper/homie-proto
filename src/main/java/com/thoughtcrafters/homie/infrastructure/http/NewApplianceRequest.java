package com.thoughtcrafters.homie.infrastructure.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.thoughtcrafters.homie.domain.appliances.ApplianceType;
import com.thoughtcrafters.homie.domain.behaviours.SwitchState;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NewApplianceRequest {
    @NotEmpty
    private String name;
    @Valid
    private SwitchState initialState;
    @Valid
    private ApplianceType type;

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

    public ApplianceType getType() {
        return type;
    }

    public void setType(ApplianceType type) {
        this.type = type;
    }
}
