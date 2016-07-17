package org.codetome.hexameter.swtexample;

import org.codetome.hexameter.core.api.CubeCoordinate;
import org.codetome.hexameter.core.internal.GridData;
import org.codetome.hexameter.core.internal.impl.HexagonImpl;

public class SWTExampleHexagon extends HexagonImpl{

    private boolean isSelected;

    public SWTExampleHexagon(GridData gridData, CubeCoordinate coordinate) {
        super(gridData, coordinate);
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
    

}
