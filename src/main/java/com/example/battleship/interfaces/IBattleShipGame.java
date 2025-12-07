package com.example.battleship.interfaces;

import com.example.battleship.model.Coordinate;
import com.example.battleship.exceptions.InvalidShipPlacementException;

/**
 * Defines the contract for the Battleship game logic.
 */
public interface IBattleShipGame {
    void startNewGame();
    void placeShip(Coordinate start, int length, boolean isHorizontal) throws InvalidShipPlacementException;
    boolean shoot(Coordinate target);
    boolean isGameOver();
    void saveGame();
    void loadGame();
}