package com.thoughtcrafters.homie.infrastructure.http;

import com.thoughtcrafters.homie.domain.rooms.Point;

import javax.validation.constraints.NotNull;

public class PointBody {
    @NotNull
    private Double x;
    @NotNull
    private Double y;

    private PointBody(Double x, Double y) {
        this.x = x;
        this.y = y;
    }

    public PointBody() {
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public static PointBody from(Point point) {
        return new PointBody(point.x(), point.y());
    }
}
