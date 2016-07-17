package org.codetome.hexameter.core.internal.impl;

import static org.codetome.hexameter.core.api.CubeCoordinate.fromCoordinates;
import static org.codetome.hexameter.core.api.Point.fromPosition;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.codetome.hexameter.core.api.CoordinateConverter;
import org.codetome.hexameter.core.api.CubeCoordinate;
import org.codetome.hexameter.core.api.Hexagon;
import org.codetome.hexameter.core.api.HexagonFactory;
import org.codetome.hexameter.core.api.HexagonalGrid;
import org.codetome.hexameter.core.api.HexagonalGridBuilder;
import org.codetome.hexameter.core.api.Point;
import org.codetome.hexameter.core.backport.Optional;
import org.codetome.hexameter.core.internal.GridData;

import lombok.Getter;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.functions.Action1;

@Getter
@SuppressWarnings("PMD.UnusedPrivateField")
public final class HexagonalGridImpl<T extends Hexagon> implements HexagonalGrid<T> {

    private static final int[][] NEIGHBORS = {{+1, 0}, {+1, -1}, {0, -1}, {-1, 0}, {-1, +1}, {0, +1}};
    private static final int NEIGHBOR_X_INDEX = 0;
    private static final int NEIGHBOR_Z_INDEX = 1;

    private final GridData gridData;
    private final Set<CubeCoordinate> coordinates;
    private final HexagonFactory<T> hexagonFactory;
    private final Map<CubeCoordinate, T> hexagons;

    /**
     * Creates a new HexagonalGrid based on the provided HexagonalGridBuilder.
     *
     * @param builder builder
     */
    public HexagonalGridImpl(final HexagonalGridBuilder<T> builder) {
        this.hexagonFactory = builder.getHexagonFactory();
        this.gridData = builder.getGridData();
        this.coordinates = new LinkedHashSet<>();
        this.hexagons = new LinkedHashMap<>();
        builder.getGridLayoutStrategy().fetchGridCoordinates(builder).subscribe(new Action1<CubeCoordinate>() {
            @Override
            public void call(CubeCoordinate cubeCoordinate) {
                HexagonalGridImpl.this.coordinates.add(cubeCoordinate);
                hexagons.put(cubeCoordinate, hexagonFactory.create(gridData, cubeCoordinate));
            }
        });
    }

    @Override
    public Observable<T> getHexagons() {
        return Observable.from(hexagons.values());
    }

    @Override
    public Observable<T> getHexagonsByCubeRange(final CubeCoordinate from, final CubeCoordinate to) {
        Observable<T> result = Observable.create(new OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                for (int gridZ = from.getGridZ(); gridZ <= to.getGridZ(); gridZ++) {
                    for (int gridX = from.getGridX(); gridX <= to.getGridX(); gridX++) {
                        final CubeCoordinate currentCoordinate = fromCoordinates(gridX, gridZ);
                        if (containsCubeCoordinate(currentCoordinate)) {
                            subscriber.onNext(getByCubeCoordinate(currentCoordinate).get());
                        }
                    }
                }
                subscriber.onCompleted();
            }
        });
        return result;
    }

    @Override
    public Observable<T> getHexagonsByOffsetRange(final int gridXFrom, final int gridXTo, final int gridYFrom, final int gridYTo) {
        Observable<T> result = Observable.create(new OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                for (int gridX = gridXFrom; gridX <= gridXTo; gridX++) {
                    for (int gridY = gridYFrom; gridY <= gridYTo; gridY++) {
                        final int cubeX = CoordinateConverter.convertOffsetCoordinatesToCubeX(gridX, gridY, gridData.getOrientation());
                        final int cubeZ = CoordinateConverter.convertOffsetCoordinatesToCubeZ(gridX, gridY, gridData.getOrientation());
                        final CubeCoordinate cubeCoordinate = fromCoordinates(cubeX, cubeZ);
                        final Optional<T> hex = getByCubeCoordinate(cubeCoordinate);
                        if (hex.isPresent()) {
                            subscriber.onNext(hex.get());
                        }
                    }
                }
                subscriber.onCompleted();
            }
        });
        return result;
    }

    @Override
    public boolean containsCubeCoordinate(final CubeCoordinate coordinate) {
        return this.coordinates.contains(coordinate);
    }

    @Override
    public Optional<T> getByCubeCoordinate(final CubeCoordinate coordinate) {

        return containsCubeCoordinate(coordinate)
                ? Optional.of(hexagons.get(coordinate))
                : Optional.<T>empty();
    }

    @Override
    public Optional<T> getByPixelCoordinate(final double coordinateX, final double coordinateY) {
        int estimatedGridX = (int) (coordinateX / gridData.getHexagonWidth());
        int estimatedGridZ = (int) (coordinateY / gridData.getHexagonHeight());
        estimatedGridX = CoordinateConverter.convertOffsetCoordinatesToCubeX(estimatedGridX, estimatedGridZ, gridData.getOrientation());
        estimatedGridZ = CoordinateConverter.convertOffsetCoordinatesToCubeZ(estimatedGridX, estimatedGridZ, gridData.getOrientation());
        // it is possible that the estimated coordinates are off the grid so we
        // create a virtual hexagon
        final CubeCoordinate estimatedCoordinate = fromCoordinates(estimatedGridX, estimatedGridZ);
        final T tempHex = hexagonFactory.create(gridData, estimatedCoordinate);

        T trueHex = refineHexagonByPixel(tempHex, fromPosition(coordinateX, coordinateY));

        if (hexagonsAreAtTheSamePosition(tempHex, trueHex)) {
            return getByCubeCoordinate(estimatedCoordinate);
        } else {
            return containsCubeCoordinate(trueHex.getCubeCoordinate())
                    ? getByCubeCoordinate(trueHex.getCubeCoordinate()) : Optional.<T>empty();
        }
    }

    @Override
    public Optional<T> getNeighborByIndex(Hexagon hexagon, int index) {
        final int neighborGridX = hexagon.getGridX() + NEIGHBORS[index][NEIGHBOR_X_INDEX];
        final int neighborGridZ = hexagon.getGridZ() + NEIGHBORS[index][NEIGHBOR_Z_INDEX];
        final CubeCoordinate neighborCoordinate = fromCoordinates(neighborGridX, neighborGridZ);
        return getByCubeCoordinate(neighborCoordinate);
    }

    @Override
    public Collection<T> getNeighborsOf(final Hexagon hexagon) {
        final Set<T> neighbors = new HashSet<>();
        for (int i = 0; i < NEIGHBORS.length; i++) {
            Optional<T> retHex = getNeighborByIndex(hexagon, i);
            if (retHex.isPresent()) {
                neighbors.add(retHex.get());
            }
        }
        return neighbors;
    }

    @Override
    public GridData getGridData() {
        return gridData;
    }

    private static boolean hexagonsAreAtTheSamePosition(final Hexagon hex0, final Hexagon hex1) {
        return hex0.getGridX() == hex1.getGridX() && hex0.getGridZ() == hex1.getGridZ();
    }

    private T refineHexagonByPixel(final T hexagon, final Point clickedPoint) {
        T refined = hexagon;
        double smallestDistance = clickedPoint.distanceFrom(fromPosition(refined.getCenterX(), refined.getCenterY()));
        for (final T neighbor : getNeighborsOf(hexagon)) {
            final double currentDistance = clickedPoint.distanceFrom(fromPosition(neighbor.getCenterX(), neighbor.getCenterY()));
            if (currentDistance < smallestDistance) {
                refined = neighbor;
                smallestDistance = currentDistance;
            }
        }
        return refined;
    }
}
