package org.codetome.hexameter.core;

import org.codetome.hexameter.core.api.CubeCoordinate;
import org.codetome.hexameter.core.api.HexagonFactory;
import org.codetome.hexameter.core.internal.GridData;
import org.codetome.hexameter.core.internal.impl.TestHexagon;

public class TestHexagonFactory implements HexagonFactory<TestHexagon>{

    @Override
    public TestHexagon create(GridData gridData, CubeCoordinate coordinate) {
        return new TestHexagon(gridData, coordinate);
    }

}
