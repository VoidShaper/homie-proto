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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Shape shape = (Shape) o;

        if (!outline.equals(shape.outline)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return outline.hashCode();
    }

    @Override
    public String toString() {
        return "Shape{" +
                "outline=" + outline +
                '}';
    }
}
