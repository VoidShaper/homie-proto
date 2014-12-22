package com.thoughtcrafters.homie.infrastructure.http;

import org.hibernate.validator.constraints.NotEmpty;

public class NewRoomRequest {
    @NotEmpty
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
