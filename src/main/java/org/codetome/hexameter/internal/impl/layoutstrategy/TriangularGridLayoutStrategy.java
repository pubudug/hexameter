package org.codetome.hexameter.internal.impl.layoutstrategy;

import static org.codetome.hexameter.api.CoordinateConverter.createKeyFromCoordinate;

import java.util.HashMap;
import java.util.Map;

import org.codetome.hexameter.api.Hexagon;
import org.codetome.hexameter.api.HexagonalGrid;
import org.codetome.hexameter.api.HexagonalGridBuilder;
import org.codetome.hexameter.internal.impl.HexagonImpl;

/**
 * This strategy is responsible for generating a {@link HexagonalGrid} which has a triangular
 * shape.
 */
public final class TriangularGridLayoutStrategy extends AbstractGridLayoutStrategy {

	public Map<String, Hexagon> createHexagons(HexagonalGridBuilder builder) {
		int gridSize = builder.getGridHeight();
		Map<String, Hexagon> hexagons = new HashMap<> ();
		for (int y = 0; y < gridSize; y++) {
			int endX = gridSize - y;
			for (int x = 0; x < endX; x++) {
				Hexagon hexagon = new HexagonImpl(builder.getSharedHexagonData(), x, y);
				hexagons.put(createKeyFromCoordinate(x, y), hexagon);
			}
		}
		addCustomHexagons(builder, hexagons);
		return hexagons;
	}

	public boolean checkParameters(int gridHeight, int gridWidth) {
		boolean superResult = super.checkParameters(gridHeight, gridWidth);
		boolean result = gridHeight == gridWidth;
		return superResult && result;
	}
}