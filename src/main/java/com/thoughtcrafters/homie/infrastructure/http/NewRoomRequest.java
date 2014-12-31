package com.thoughtcrafters.homie.infrastructure.http;

import com.google.common.collect.FluentIterable;
import com.thoughtcrafters.homie.domain.rooms.Point;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import java.util.List;

public class NewRoomRequest {
    @NotEmpty
    private String name;

    @Valid
    @NotEmpty
    private List<PointBody> shape;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PointBody> getShape() {
        return shape;
    }

    public void setShape(List<PointBody> shape) {
        this.shape = shape;
    }

    public List<Point> outline() {
        return FluentIterable.from(shape)
                             .transform(x -> new Point(x.getX(), x.getY()))
                             .toList();
    }
}
