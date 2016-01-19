package org.codetome.hexameter.core.api;

import org.codetome.hexameter.core.api.exception.HexagonalGridCreationException;
import org.codetome.hexameter.core.internal.SharedHexagonData;
import org.codetome.hexameter.core.internal.impl.HexagonalGridImpl;
import org.codetome.hexameter.core.internal.impl.HexagonalGridCalculatorImpl;
import org.codetome.hexameter.core.internal.impl.layoutstrategy.GridLayoutStrategy;

import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.codetome.hexameter.core.api.HexagonalGridLayout.RECTANGULAR;

/**
 * <p>Builder for a {@link HexagonalGrid}.
 * Can be used to build a {@link HexagonalGrid}.
 * Mandatory parameters are:</p>
 * <ul>
 * <li>width of the grid</li>
 * <li>height of the grid</li>
 * <li>radius of a {@link Hexagon}</li>
 * </ul>
 * Defaults for orientation and grid layout are POINTY_TOP and RECTANGULAR.
 *
 */
public final class HexagonalGridBuilder {
    private int gridWidth;
    private int gridHeight;
    private double radius;
    private Map<String, Hexagon> customStorage = new ConcurrentHashMap<>();
    private HexagonOrientation orientation = HexagonOrientation.POINTY_TOP;
    private HexagonalGridLayout gridLayout = RECTANGULAR;

    /**
     * Sets the {@link HexagonalGridLayout} which will be used when creating the {@link HexagonalGrid}.
     * If it is not set <pre>RECTANGULAR</pre> will be assumed.
     *
     * @param gridLayout
     *
     * @return this {@link HexagonalGridBuilder}.
     */
    public HexagonalGridBuilder setGridLayout(final HexagonalGridLayout gridLayout) {
        this.gridLayout = gridLayout;
        return this;
    }

    /**
     * Builds a {@link HexagonalGrid} using the parameters supplied.
     * Throws {@link HexagonalGridCreationException} if not all mandatory parameters
     * are filled and/or they are not valid. In both cases you will be supplied with
     * a {@link HexagonalGridCreationException} detailing the cause of failure.
     *
     * @return {@link HexagonalGrid}
     */
    public HexagonalGrid build() {
        checkParameters();
        return new HexagonalGridImpl(this);
    }

    private void checkParameters() {
        if (orientation == null) {
            throw new HexagonalGridCreationException("Orientation must be set.");
        }
        if (radius <= 0) {
            throw new HexagonalGridCreationException("Radius must be greater than 0.");
        }
        if (gridLayout == null) {
            throw new HexagonalGridCreationException("Grid layout must be set.");
        }
        if (!gridLayout.checkParameters(gridHeight, gridWidth)) {
            throw new HexagonalGridCreationException("Width: " + gridWidth + " and height: " + gridHeight + " is not valid for: " + gridLayout.name() + " layout.");
        }
    }

    /**
     * Creates a {@link HexagonalGridCalculator} for your {@link HexagonalGrid}.
     *
     * @param hexagonalGrid
     *
     * @return calculator
     */
    public HexagonalGridCalculator buildCalculatorFor(final HexagonalGrid hexagonalGrid) {
        return new HexagonalGridCalculatorImpl(hexagonalGrid);
    }

    public double getRadius() {
        return radius;
    }

    /**
     * Sets the radius of the {@link Hexagon}s contained in the resulting {@link HexagonalGrid}.
     *
     * @param radius in pixels
     *
     * @return this {@link HexagonalGridBuilder}
     */
    public HexagonalGridBuilder setRadius(final double radius) {
        this.radius = radius;
        return this;
    }

    public int getGridWidth() {
        return gridWidth;
    }

    /**
     * Mandatory parameter. Sets the number of {@link Hexagon}s in the horizontal direction.
     *
     * @param gridWidth
     *
     * @return this {@link HexagonalGridBuilder}
     */
    public HexagonalGridBuilder setGridWidth(final int gridWidth) {
        this.gridWidth = gridWidth;
        return this;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    /**
     * Mandatory parameter. Sets the number of {@link Hexagon}s in the vertical direction.
     *
     * @param gridHeight
     *
     * @return this {@link HexagonalGridBuilder}
     */
    public HexagonalGridBuilder setGridHeight(final int gridHeight) {
        this.gridHeight = gridHeight;
        return this;
    }

    public HexagonOrientation getOrientation() {
        return orientation;
    }

    /**
     * Sets the {@link HexagonOrientation} used in the resulting {@link HexagonalGrid}.
     * If it is not set HexagonOrientation.POINTY will be used.
     *
     * @param orientation
     *
     * @return this {@link HexagonalGridBuilder}
     */
    public HexagonalGridBuilder setOrientation(final HexagonOrientation orientation) {
        this.orientation = orientation;
        return this;
    }

    public GridLayoutStrategy getGridLayoutStrategy() {
        return gridLayout.getGridLayoutStrategy();
    }

    public Map<String, Hexagon> getCustomStorage() {
        return customStorage;
    }

    /**
     * Sets a custom storage object to the {@link HexagonalGrid}. It will be used
     * instead of the internal storage. You can pass any custom storage object
     * as long as it implements the {@link Map} interface. Refer to the swt example
     * project for examples. <strong>Please note</strong> that if you supply a Map
     * which is not empty the {@link HexagonalGrid} will overwrite its contents.
     * Methods you must implement:
     * <ul>
     * <li> {@link Map#containsKey(Object)}</li>
     * <li> {@link Map#get(Object)}</li>
     * <li> {@link Map#putAll(Map)}</li>
     * <li> {@link Map#put(Object, Object)}</li>
     * <li> {@link Map#remove(Object)}</li>
     * <li> {@link Map#keySet()}</li>
     * </ul>
     * Others are not necessary but highly recommended. Refer to the javadoc of {@link AbstractMap} if you need help.
     *
     * @param customStorage
     *
     * @return this {@link HexagonalGridBuilder}.
     */
    public HexagonalGridBuilder setCustomStorage(final Map<String, Hexagon> customStorage) {
        if (customStorage == null) {
            throw new IllegalArgumentException("customStorage cannot be null!");
        }
        this.customStorage = customStorage;
        return this;
    }

    public SharedHexagonData getSharedHexagonData() {
        if (orientation == null || radius == 0) {
            throw new IllegalStateException("orientation or radius is not yet initialized");
        }
        return new SharedHexagonData(orientation, radius);
    }

    public HexagonalGridLayout getGridLayout() {
        return gridLayout;
    }
}