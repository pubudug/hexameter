package org.codetome.hexameter.core.internal.impl;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.codetome.hexameter.core.api.HexagonOrientation.FLAT_TOP;
import static org.codetome.hexameter.core.api.Point.fromPosition;

import java.util.ArrayList;
import java.util.List;

import org.codetome.hexameter.core.api.CubeCoordinate;
import org.codetome.hexameter.core.api.Hexagon;
import org.codetome.hexameter.core.api.Point;
import org.codetome.hexameter.core.internal.GridData;

import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Default implementation of the {@link Hexagon} interface.
 */
@EqualsAndHashCode
@ToString(of = "coordinate")
public class HexagonImpl implements Hexagon {

    private final CubeCoordinate coordinate;
    private final transient GridData sharedData;

    public HexagonImpl(final GridData gridData, final CubeCoordinate coordinate) {
        this.sharedData = gridData;
        this.coordinate = coordinate;
    }

    @Override
    public String getId() {
        return coordinate.toAxialKey();
    }

    @Override
    public final List<Point> getPoints() {
        final List<Point> points = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            final double angle = 2 * Math.PI / 6 * (i + sharedData.getOrientation().getCoordinateOffset());
            final double x = getCenterX() + sharedData.getRadius() * cos(angle);
            final double y = getCenterY() + sharedData.getRadius() * sin(angle);
            points.add(fromPosition(x, y));
        }
        return points;
    }

    @Override
    public CubeCoordinate getCubeCoordinate() {
        return coordinate;
    }

    @Override
    public int getGridX() {
        return coordinate.getGridX();
    }

    @Override
    public final int getGridY() {
        return coordinate.getGridY();
    }

    @Override
    public int getGridZ() {
        return coordinate.getGridZ();
    }

    @Override
    public final double getCenterX() {
        if (FLAT_TOP.equals(sharedData.getOrientation())) {
            return coordinate.getGridX() * sharedData.getHexagonWidth() + sharedData.getRadius();
        } else {
            return coordinate.getGridX() * sharedData.getHexagonWidth() + coordinate.getGridZ()
                    * sharedData.getHexagonWidth() / 2 + sharedData.getHexagonWidth() / 2;
        }
    }

    @Override
    public final double getCenterY() {
        if (FLAT_TOP.equals(sharedData.getOrientation())) {
            return coordinate.getGridZ() * sharedData.getHexagonHeight() + coordinate.getGridX()
                    * sharedData.getHexagonHeight() / 2 + sharedData.getHexagonHeight() / 2;
        } else {
            return coordinate.getGridZ() * sharedData.getHexagonHeight() + sharedData.getRadius();
        }
    }
}
