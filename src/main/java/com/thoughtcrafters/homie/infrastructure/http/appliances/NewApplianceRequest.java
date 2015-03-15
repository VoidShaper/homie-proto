package com.thoughtcrafters.homie.infrastructure.http.appliances;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.thoughtcrafters.homie.domain.appliances.ApplianceCreation;
import com.thoughtcrafters.homie.domain.appliances.ApplianceType;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
use = JsonTypeInfo.Id.NAME,
include = JsonTypeInfo.As.PROPERTY,
property = "type")
@JsonSubTypes({
@JsonSubTypes.Type(value = NewLightRequest.class, name = "LIGHT"),
})
public abstract class NewApplianceRequest {
    @NotEmpty
    private String name;
    @Valid
    private ApplianceType type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ApplianceType getType() {
        return type;
    }

    public void setType(ApplianceType type) {
        this.type = type;
    }

    public abstract ApplianceCreation toApplianceCreation();
}
