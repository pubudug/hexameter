package org.codetome.hexameter.core.api;

public interface HexagonAttributes<H extends Hexagon> {

    double getMovementCost(H hexagon);
    
}
