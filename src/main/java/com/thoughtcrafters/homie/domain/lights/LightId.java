package com.thoughtcrafters.homie.domain.lights;

import java.util.UUID;

public class LightId {
    private final UUID id;

    public LightId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LightId lightId = (LightId) o;

        if (!id.equals(lightId.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "LightId{" +
                "id=" + id +
                '}';
    }
}
