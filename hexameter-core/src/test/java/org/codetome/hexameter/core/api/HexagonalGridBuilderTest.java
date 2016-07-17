package org.codetome.hexameter.core.api;

import junit.framework.Assert;
import org.codetome.hexameter.core.api.exception.HexagonalGridCreationException;
import org.codetome.hexameter.core.internal.impl.TestHexagon;
import org.codetome.hexameter.core.internal.impl.layoutstrategy.GridLayoutStrategy;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.codetome.hexameter.core.api.HexagonOrientation.FLAT_TOP;
import static org.codetome.hexameter.core.api.HexagonalGridLayout.RECTANGULAR;
import static org.codetome.hexameter.core.api.HexagonalGridLayout.TRIANGULAR;

import org.codetome.hexameter.core.TestHexagonFactory;

public class HexagonalGridBuilderTest {

    private static final int GRID_HEIGHT = 9;
    private static final HexagonalGridLayout GRID_LAYOUT = RECTANGULAR;
    private static final GridLayoutStrategy GRID_LAYOUT_STRATEGY = RECTANGULAR.getGridLayoutStrategy();
    private static final int GRID_WIDTH = 9;
    private static final HexagonOrientation ORIENTATION = FLAT_TOP;
    private static final double RADIUS = 30;

    private HexagonalGridBuilder<TestHexagon> target;

    @Before
    public void setUp() {
        target = new HexagonalGridBuilder<TestHexagon>();
        target.setGridHeight(GRID_HEIGHT).setGridLayout(GRID_LAYOUT).setGridWidth(GRID_WIDTH).setOrientation(ORIENTATION).setRadius(RADIUS);
        target.setHexagonFactory(new TestHexagonFactory());
    }

    @Test
    public void shouldContainProperValuesWhenGettersAreCalled() {
        assertEquals(GRID_HEIGHT, target.getGridHeight());
        assertEquals(GRID_WIDTH, target.getGridWidth());
        assertEquals(GRID_LAYOUT_STRATEGY, target.getGridLayoutStrategy());
        assertEquals(RADIUS, target.getRadius());
        Assert.assertNotNull(target.getGridData());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailGettingSharedHexagonDataWhenOrientationIsNull() {
        target.setOrientation(null);
        target.getGridData();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailGettingSharedHexagonDataWhenRadiusIsZero() {
        target.setRadius(0);
        target.getGridData();
    }

    @Test(expected = HexagonalGridCreationException.class)
    public void shouldFailBuildWhenOrientationIsNull() {
        target.setOrientation(null);
        target.build();
    }

    @Test(expected = HexagonalGridCreationException.class)
    public void shouldFailBuildWhenRadiusIsZero() {
        target.setRadius(0);
        target.build();
    }

    @Test(expected = HexagonalGridCreationException.class)
    public void shouldFailBuildWhenGridLayoutIsNull() {
        target.setGridLayout(null);
        target.build();
    }

    @Test(expected = HexagonalGridCreationException.class)
    public void shouldFailBuildWhenSizeIsNotCompatibleWithLayout() {
        target.setGridLayout(TRIANGULAR);
        target.setGridHeight(4);
        target.build();
    }

    @Test
    public void shouldBuildCalculatorWhenBuildCalculatorIsCalled() {
        final HexagonalGridCalculator calc = target.buildCalculatorFor(null, null);
        assertNotNull(calc);
    }

    @Test
    public void shouldReturnProperOrientationWhenGetOrientationIsCalled() {
        assertEquals(ORIENTATION, target.getOrientation());
    }

    @Test
    public void shouldBuildWhenProperParametersArePresent() {
        final HexagonalGrid grid = target.build();
        assertNotNull(grid);
    }

}
