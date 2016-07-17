package org.codetome.hexameter.restexample.dto;

import lombok.Data;
import org.codetome.hexameter.core.api.HexagonOrientation;
import org.codetome.hexameter.core.api.HexagonalGridLayout;


/**
 * Represents a HexagonalGrid in a format which is typically used
 * to represent a map.
 */
@Data
public class MapDto {

    private int cellWidth;
    private int cellHeight;
    private double cellRadius;
    private HexagonOrientation orientation;
    private HexagonalGridLayout layout;

}
