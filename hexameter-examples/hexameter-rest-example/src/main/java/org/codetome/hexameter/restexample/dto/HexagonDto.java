package org.codetome.hexameter.restexample.dto;

import lombok.Data;
import org.codetome.hexameter.core.api.Hexagon;
import org.codetome.hexameter.core.api.Point;
import org.codetome.hexameter.core.backport.Optional;

import java.util.ArrayList;

@Data
public class HexagonDto {
    /**
     * Represents the grid coordinate (x,z) of a Hexagon.
     * This is an CubeCoordinate.
     */
    private int[] gridCoordinate;
    /**
     * Represents the center point of a Hexagon (x, y).
     */
    private Double[] centerPoint;
    /**
     * Represents the (x,y) ponts of the edges of a Hexagon.
     */
    private Double[][] points;

    /**
     * Represents the satellite data stored in the source Hexagon (if any).
     */
    public static HexagonDto fromHexagon(final Hexagon hexagon) {
        HexagonDto result = new HexagonDto();
        result.setCenterPoint(new Double[]{hexagon.getCenterX(), hexagon.getCenterY()});
        result.setGridCoordinate(new int[]{hexagon.getGridX(), hexagon.getGridZ()});
        Double[][] points = new Double[6][2];
        final ArrayList<Point> hexPoints = new ArrayList<>(hexagon.getPoints());
        for (int i = 0; i < 6; i++) {
            points[i][0] = hexPoints.get(i).getCoordinateX();
            points[i][1] = hexPoints.get(i).getCoordinateY();
        }
        result.setPoints(points);
        return result;
    }
}
