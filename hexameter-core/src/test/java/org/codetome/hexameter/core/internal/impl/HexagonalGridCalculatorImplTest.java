package org.codetome.hexameter.core.internal.impl;

import org.codetome.hexameter.core.TestHexagonFactory;
import org.codetome.hexameter.core.api.CubeCoordinateTest;
import org.codetome.hexameter.core.api.Hexagon;
import org.codetome.hexameter.core.api.HexagonAttributes;
import org.codetome.hexameter.core.api.HexagonalGrid;
import org.codetome.hexameter.core.api.HexagonalGridBuilder;
import org.codetome.hexameter.core.api.HexagonalGridCalculator;
import org.codetome.hexameter.core.api.RotationDirection;
import org.codetome.hexameter.core.backport.Optional;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.codetome.hexameter.core.api.CubeCoordinate.fromCoordinates;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class HexagonalGridCalculatorImplTest {

    private HexagonalGrid<TestHexagon> grid;
    private HexagonalGridCalculator<TestHexagon> target;

    @Mock
    private Hexagon originalHex;
    @Mock
    private Hexagon targetHex;
    private RotationDirection rotationDirection;
    
    private TestHexagonAttributes testHexagonAttributes = new TestHexagonAttributes();
    
    @Before
    public void setUp() throws Exception {
        initMocks(this);
        final HexagonalGridBuilder<TestHexagon> builder = new HexagonalGridBuilder<TestHexagon>()
                .setGridHeight(10).setGridWidth(10).setRadius(10);
        builder.setHexagonFactory(new TestHexagonFactory());
        grid = builder.build();
        target = builder.buildCalculatorFor(grid, testHexagonAttributes);
    }

    @Test
    public void shouldProperlyCalculateDistanceBetweenTwoHexes() {
        final Hexagon hex0 = grid.getByCubeCoordinate(fromCoordinates(1, 1)).get();
        final Hexagon hex1 = grid.getByCubeCoordinate(fromCoordinates(4, 5)).get();
        assertEquals(7, target.calculateDistanceBetween(hex0, hex1));
    }

    @Test
    public void shouldProperlyCalculateMovementRangeFromHexWith1() {
        final Hexagon hex = grid.getByCubeCoordinate(fromCoordinates(3, 7)).get();
        final Set<Hexagon> expected = new HashSet<>();
        expected.add(hex);
        expected.add(grid.getByCubeCoordinate(fromCoordinates(3, 6)).get());
        expected.add(grid.getByCubeCoordinate(fromCoordinates(4, 6)).get());
        expected.add(grid.getByCubeCoordinate(fromCoordinates(4, 7)).get());
        expected.add(grid.getByCubeCoordinate(fromCoordinates(3, 8)).get());
        expected.add(grid.getByCubeCoordinate(fromCoordinates(2, 8)).get());
        expected.add(grid.getByCubeCoordinate(fromCoordinates(2, 7)).get());
        final Set<TestHexagon> actual = target.calculateMovementRangeFrom(hex, 1);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldProperlyCalculateMovementRangeFromHexWith2() {
        final Hexagon hex = grid.getByCubeCoordinate(fromCoordinates(3, 7)).get();
        final Set<Hexagon> expected = new HashSet<>();
        expected.add(hex);
        expected.add(grid.getByCubeCoordinate(fromCoordinates(3, 6)).get());
        expected.add(grid.getByCubeCoordinate(fromCoordinates(4, 6)).get());
        expected.add(grid.getByCubeCoordinate(fromCoordinates(4, 7)).get());
        expected.add(grid.getByCubeCoordinate(fromCoordinates(3, 8)).get());
        expected.add(grid.getByCubeCoordinate(fromCoordinates(2, 8)).get());
        expected.add(grid.getByCubeCoordinate(fromCoordinates(2, 7)).get());

        expected.add(grid.getByCubeCoordinate(fromCoordinates(3, 5)).get());
        expected.add(grid.getByCubeCoordinate(fromCoordinates(4, 5)).get());
        expected.add(grid.getByCubeCoordinate(fromCoordinates(5, 5)).get());
        expected.add(grid.getByCubeCoordinate(fromCoordinates(2, 6)).get());
        expected.add(grid.getByCubeCoordinate(fromCoordinates(5, 6)).get());
        expected.add(grid.getByCubeCoordinate(fromCoordinates(1, 7)).get());
        expected.add(grid.getByCubeCoordinate(fromCoordinates(5, 7)).get());
        expected.add(grid.getByCubeCoordinate(fromCoordinates(1, 8)).get());
        expected.add(grid.getByCubeCoordinate(fromCoordinates(4, 8)).get());
        expected.add(grid.getByCubeCoordinate(fromCoordinates(1, 9)).get());
        expected.add(grid.getByCubeCoordinate(fromCoordinates(3, 9)).get());
        expected.add(grid.getByCubeCoordinate(fromCoordinates(2, 9)).get());

        final Set<TestHexagon> actual = target.calculateMovementRangeFrom(hex, 2);
        assertEquals(expected, actual);
    }

    @Test
    public void shouldProperlyCalculateLine() {
        List<TestHexagon> actual = target.drawLine(grid.getByCubeCoordinate(fromCoordinates(3, 7)).get(),
                grid.getByCubeCoordinate(fromCoordinates(8, 1)).get()); 
        assertEquals(
                Arrays.asList(
                        grid.getByCubeCoordinate(fromCoordinates(3, 7)).get(),
                        grid.getByCubeCoordinate(fromCoordinates(4, 6)).get(),
                        grid.getByCubeCoordinate(fromCoordinates(5, 5)).get(),
                        grid.getByCubeCoordinate(fromCoordinates(6, 4)).get(),
                        grid.getByCubeCoordinate(fromCoordinates(6, 3)).get(),
                        grid.getByCubeCoordinate(fromCoordinates(7, 2)).get(),
                        grid.getByCubeCoordinate(fromCoordinates(8, 1)).get()),
                actual);
    }
    
    
    @Test
    public void shouldProperlyCalculateShortestPath() {
        TestHexagon from = grid.getByCubeCoordinate(fromCoordinates(-2, 4)).get();
        TestHexagon to = grid.getByCubeCoordinate(fromCoordinates(4, 4)).get();
        testHexagonAttributes.setCost(10000, fromCoordinates(2, 2), fromCoordinates(1, 3), fromCoordinates(1, 4),
                fromCoordinates(0, 5),  fromCoordinates(-2, 5));
        testHexagonAttributes.setCost(2, fromCoordinates(2,5) ,fromCoordinates(1, 6));
        List<TestHexagon> actual = target.findShortestPath(from, to);
        assertEquals(Arrays.asList(
                grid.getByCubeCoordinate(fromCoordinates(-2, 4)).get(),
                grid.getByCubeCoordinate(fromCoordinates(-1, 4)).get(),
                grid.getByCubeCoordinate(fromCoordinates(-1, 5)).get(),
                grid.getByCubeCoordinate(fromCoordinates(-1, 6)).get(),
                grid.getByCubeCoordinate(fromCoordinates(0, 6)).get(),
                grid.getByCubeCoordinate(fromCoordinates(1, 5)).get(),
                grid.getByCubeCoordinate(fromCoordinates(2, 4)).get(),
                grid.getByCubeCoordinate(fromCoordinates(3, 4)).get(),
                grid.getByCubeCoordinate(fromCoordinates(4, 4)).get()), actual);
    }

    @Test
    public void shouldProperlyCalculateRotationWhenGivenAValidGrid() {
        configureMockitoForRotation();

        final Optional<TestHexagon> resultOpt = target.rotateHexagon(originalHex, targetHex, RotationDirection.RIGHT);

        Hexagon result = resultOpt.get();

        assertThat(result.getGridX()).isEqualTo(3);
        assertThat(result.getGridY()).isEqualTo(-4);
        assertThat(result.getGridZ()).isEqualTo(1);
    }

    @Test
    public void shouldProperlyCalculateRingWhenGivenValidParameters() {
        when(targetHex.getGridX()).thenReturn(0);
        when(targetHex.getGridZ()).thenReturn(0);
        final Set<TestHexagon> result = target.calculateRingFrom(targetHex, 3);
    }

    private void configureMockitoForRotation() {
        when(originalHex.getGridX()).thenReturn(3);
        when(originalHex.getGridY()).thenReturn(-2);
        when(originalHex.getGridZ()).thenReturn(-1);

        when(targetHex.getGridX()).thenReturn(5);
        when(targetHex.getGridY()).thenReturn(-4);
        when(targetHex.getGridZ()).thenReturn(-1);
    }

}
