package org.codetome.hexameter.core.internal.impl;

import org.codetome.hexameter.core.api.CubeCoordinate;
import org.codetome.hexameter.core.api.Hexagon;
import org.codetome.hexameter.core.api.HexagonalGrid;
import org.codetome.hexameter.core.api.HexagonalGridCalculator;
import org.codetome.hexameter.core.api.RotationDirection;
import org.codetome.hexameter.core.backport.Optional;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.round;

public final class HexagonalGridCalculatorImpl<T extends Hexagon> implements HexagonalGridCalculator<T> {

    private final HexagonalGrid<T> hexagonalGrid;

    public HexagonalGridCalculatorImpl(final HexagonalGrid<T> hexagonalGrid) {
        this.hexagonalGrid = hexagonalGrid;
    }

    @Override
    public int calculateDistanceBetween(final Hexagon hex0, final Hexagon hex1) {
        final double absX = abs(hex0.getGridX() - hex1.getGridX());
        final double absY = abs(hex0.getGridY() - hex1.getGridY());
        final double absZ = abs(hex0.getGridZ() - hex1.getGridZ());
        return (int) max(max(absX, absY), absZ);
    }

    @Override
    public Set<T> calculateMovementRangeFrom(final Hexagon hexagon, final int distance) {
        final Set<T> ret = new HashSet<>();
        for (int x = -distance; x <= distance; x++) {
            for (int y = max(-distance, -x - distance); y <= min(distance, -x + distance); y++) {
                final int z = -x - y;
                final int tmpX = hexagon.getGridX() + x;
                final int tmpZ = hexagon.getGridZ() + z;
                final CubeCoordinate tempCoordinate = CubeCoordinate.fromCoordinates(tmpX, tmpZ);
                if (hexagonalGrid.containsCubeCoordinate(tempCoordinate)) {
                    final T hex = hexagonalGrid.getByCubeCoordinate(tempCoordinate).get();
                    ret.add(hex);
                }
            }
        }
        return ret;
    }

    @Override
    public Optional<T> rotateHexagon(Hexagon originalHex, Hexagon targetHex, RotationDirection rotationDirection) {
        final int diffX = targetHex.getGridX() - originalHex.getGridX();
        final int diffZ = targetHex.getGridZ() - originalHex.getGridZ();
        final CubeCoordinate diffCoord = CubeCoordinate.fromCoordinates(diffX, diffZ);
        final CubeCoordinate rotatedCoord = rotationDirection.calculateRotation(diffCoord);
        final CubeCoordinate resultCoord = CubeCoordinate.fromCoordinates(
                originalHex.getGridX() + rotatedCoord.getGridX(),
                originalHex.getGridZ() + rotatedCoord.getGridZ()); // 0, x,
        return hexagonalGrid.getByCubeCoordinate(resultCoord);
    }

    @Override
    public Set<T> calculateRingFrom(Hexagon centerHexagon, int radius) {
        final Set<T> result = new HashSet<>();
        final int neighborIndex = 0;
        Hexagon currentHexagon = centerHexagon;
        for (int i = 0; i < radius; i++) {
            final Optional<T> neighbor = hexagonalGrid.getNeighborByIndex(currentHexagon, neighborIndex);
            if (neighbor.isPresent()) {
                currentHexagon = neighbor.get();
            } else {
                return result;
            }
        }
        return result;
    }

    @Override
    public List<T> drawLine(Hexagon from, Hexagon to) {
        int distance = calculateDistanceBetween(from, to);
        List<T> results = new LinkedList<>();
        for (int i = 0; i <= distance; i++) {
            CubeCoordinate interpolatedCoordinate = cubeLinearInterpolate(from.getCubeCoordinate(),
                    to.getCubeCoordinate(), (1.0 / distance) * i);
            results.add(hexagonalGrid.getByCubeCoordinate(interpolatedCoordinate).get());
        }
        return results;
    }

    private CubeCoordinate cubeLinearInterpolate(CubeCoordinate from, CubeCoordinate to, double sample) {
        return roundToCubeCoordinate(linearInterpolate(from.getGridX(), to.getGridX(), sample),
                linearInterpolate(from.getGridY(), to.getGridY(), sample),
                linearInterpolate(from.getGridZ(), to.getGridZ(), sample));
    }

    private double linearInterpolate(int from, int to, double sample) {
        return from + (to - from) * sample;
    }

    private CubeCoordinate roundToCubeCoordinate(double gridX, double gridY, double gridZ) {
        int rx = (int) round(gridX);
        int ry = (int) round(gridY);
        int rz = (int) round(gridZ);

        double differenceX = abs(rx - gridX);
        double differenceY = abs(ry - gridY);
        double differenceZ = abs(rz - gridZ);

        if (differenceX > differenceY && differenceX > differenceZ) {
            rx = -ry - rz;
        } else if (differenceY <= differenceZ) {
            rz = -rx - ry;
        }
        return CubeCoordinate.fromCoordinates(rx, rz);
    }
}
