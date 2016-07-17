package org.codetome.hexameter.swtexample;

import org.codetome.hexameter.core.api.CubeCoordinate;
import org.codetome.hexameter.core.api.HexagonFactory;
import org.codetome.hexameter.core.internal.GridData;

public class SWTExampleHexagonFactory implements HexagonFactory<SWTExampleHexagon> {

    @Override
    public SWTExampleHexagon create(GridData gridData, CubeCoordinate coordinate) {
        return new SWTExampleHexagon(gridData, coordinate);
    }

}
