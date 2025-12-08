package com.example.battleship.patterns;

import com.example.battleship.model.Board;
import com.example.battleship.model.Coordinate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Smart shooting strategy - Medium difficulty.
 * After hitting a ship, tries adjacent cells to find and sink it.
 *
 * @author Battleship Team
 * @version 1.0
 */
public class SmartStrategy implements ShootingStrategy {

    private Random random = new Random();
    private Coordinate lastHit = null;

    /**
     * Gets next shot with tactical awareness of recent hits.
     *
     * @param board The target board
     * @return A strategically chosen coordinate
     */
    @Override
    public Coordinate getNextShot(Board board) {
        // If we have a recent hit, try adjacent cells
        if (lastHit != null) {
            List<Coordinate> adjacent = getAdjacentCells(lastHit);

            for (Coordinate coord : adjacent) {
                if (isValidTarget(board, coord)) {
                    Board.CellState state = board.getGrid().get(coord);
                    if (state == Board.CellState.WATER || state == Board.CellState.SHIP) {
                        return coord;
                    }
                }
            }

            lastHit = null; // No valid adjacent cells
        }

        // Otherwise, use random strategy
        return new RandomStrategy().getNextShot(board);
    }

    /**
     * Gets the four adjacent cells (North, South, East, West).
     *
     * @param coord The center coordinate
     * @return List of adjacent coordinates
     */
    private List<Coordinate> getAdjacentCells(Coordinate coord) {
        List<Coordinate> adjacent = new ArrayList<>();
        int row = coord.getRow();
        int col = coord.getCol();

        // North, South, East, West
        adjacent.add(new Coordinate(row - 1, col));
        adjacent.add(new Coordinate(row + 1, col));
        adjacent.add(new Coordinate(row, col - 1));
        adjacent.add(new Coordinate(row, col + 1));

        return adjacent;
    }

    /**
     * Checks if coordinate is within board bounds.
     *
     * @param board The game board
     * @param coord The coordinate to check
     * @return true if valid, false otherwise
     */
    private boolean isValidTarget(Board board, Coordinate coord) {
        int row = coord.getRow();
        int col = coord.getCol();

        return row >= 0 && row < 10 && col >= 0 && col < 10;
    }

    /**
     * Call this when a hit is registered to track it.
     *
     * @param coord The coordinate that was hit
     */
    public void registerHit(Coordinate coord) {
        this.lastHit = coord;
    }
}