package com.example.battleship.patterns;

import com.example.battleship.model.Board;
import com.example.battleship.model.Coordinate;

/**
 * Strategy interface for AI shooting behavior (Strategy Pattern).
 * Allows different AI difficulty levels without modifying core game logic.
 *
 * <p>Design Pattern: Strategy</p>
 * <p>Purpose: Enable different AI algorithms to be swapped at runtime</p>
 *
 * @author Battleship Team
 * @version 1.0
 * @since 2025-12-07
 */
public interface ShootingStrategy {

    /**
     * Determines the next shot coordinate based on the strategy.
     *
     * @param board The target board to shoot at
     * @return The coordinate to shoot at
     */
    Coordinate getNextShot(Board board);
}