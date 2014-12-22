package com.thoughtcrafters.homie.domain.appliances;

import java.util.UUID;

public class ApplianceId {
    private final UUID uuid;

    public ApplianceId(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID uuid() {
        return uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApplianceId applianceId = (ApplianceId) o;

        if (!uuid.equals(applianceId.uuid)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String toString() {
        return "ApplianceId{" +
                "uuid=" + uuid +
                '}';
    }
}
