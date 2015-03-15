package com.thoughtcrafters.homie.infrastructure.http.appliances;

import com.thoughtcrafters.homie.domain.appliances.ApplianceCreation;
import com.thoughtcrafters.homie.domain.appliances.lights.LightCreation;

public class NewLightRequest extends NewApplianceRequest {
    public Boolean dimmable = false;

    public Boolean getDimmable() {
        return dimmable;
    }

    public void setDimmable(Boolean dimmable) {
        this.dimmable = dimmable;
    }

    @Override
    public ApplianceCreation toApplianceCreation() {
        return new LightCreation(getName(), getDimmable());
    }
}
