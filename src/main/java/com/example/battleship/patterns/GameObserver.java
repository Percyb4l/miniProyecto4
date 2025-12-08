package com.example.battleship.patterns;

/**
 * Observer interface for game events (Observer Pattern).
 * Allows components to be notified of game state changes without tight coupling.
 *
 * <p>Design Pattern: Observer</p>
 * <p>Purpose: Decouple game logic from UI updates and allow multiple observers</p>
 *
 * @author Battleship Team
 * @version 1.0
 * @since 2025-12-07
 */
public interface GameObserver {

    /**
     * Called when a player's board changes (ship placement or shot).
     *
     * @param isPlayerBoard true if player's board changed, false for machine board
     */
    void onBoardChanged(boolean isPlayerBoard);

    /**
     * Called when a shot is fired by either player.
     *
     * @param isHit true if shot hit a ship, false if it was a miss
     * @param isSunk true if the shot sunk a ship completely
     */
    void onShotFired(boolean isHit, boolean isSunk);

    /**
     * Called when the game ends with a winner.
     *
     * @param playerWon true if player won, false if machine won
     */
    void onGameOver(boolean playerWon);

    /**
     * Called when the active turn changes between players.
     *
     * @param isPlayerTurn true if it's player's turn, false for machine turn
     */
    void onTurnChanged(boolean isPlayerTurn);
}