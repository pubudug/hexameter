package org.codetome.hexameter.core.internal.impl;

import java.util.HashMap;
import java.util.Map;

import org.codetome.hexameter.core.api.CubeCoordinate;
import org.codetome.hexameter.core.api.HexagonAttributes;

public class TestHexagonAttributes implements HexagonAttributes<TestHexagon> {
    private Map<CubeCoordinate, Double> costMap = new HashMap<>();

    public void setCost(double cost, CubeCoordinate... coordinates) {
        for (CubeCoordinate cubeCoordinate : coordinates) {
            costMap.put(cubeCoordinate, cost);
        }
    }

    @Override
    public double getMovementCost(TestHexagon hexagon) {
        CubeCoordinate cubeCoordinate = hexagon.getCubeCoordinate();
        if (costMap.containsKey(cubeCoordinate)) {
            return costMap.get(cubeCoordinate);
        } else {
            return 1;
        }
    }

}
