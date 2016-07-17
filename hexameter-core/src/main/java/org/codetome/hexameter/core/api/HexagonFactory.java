package org.codetome.hexameter.core.api;

import org.codetome.hexameter.core.internal.GridData;

public interface HexagonFactory<T extends Hexagon> {
    T create(final GridData gridData, final CubeCoordinate coordinate);
}
