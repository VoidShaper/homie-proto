package com.thoughtcrafters.homie.domain.rooms;

import com.google.common.collect.ImmutableList;

import java.util.List;

public class Shape {
    List<Point> outline;

    public Shape(List<Point> outline) {
        this.outline = ImmutableList.copyOf(outline);
    }

    public boolean surrounds(Point aPoint) {
        return true;
    }

    public List<Point> outline() {
        return ImmutableList.copyOf(outline);
    }
}
