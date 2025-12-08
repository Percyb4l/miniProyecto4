package com.example.battleship.patterns;

import com.example.battleship.model.Board;
import com.example.battleship.model.Coordinate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Hunt-and-target strategy - Hard difficulty.
 * Uses checkerboard pattern for hunting, then systematically targets adjacent cells.
 *
 * @author Battleship Team
 * @version 1.0
 */
public class HuntTargetStrategy implements ShootingStrategy {

    private Random random = new Random();
    private List<Coordinate> targetQueue = new ArrayList<>();

    /**
     * Gets next shot using hunt-target algorithm.
     * Hunts with checkerboard pattern, targets after finding ships.
     *
     * @param board The target board
     * @return A strategically optimal coordinate
     */
    @Override
    public Coordinate getNextShot(Board board) {
        // Target mode: pursue known hits
        if (!targetQueue.isEmpty()) {
            Coordinate target = targetQueue.remove(0);
            if (isValidShot(board, target)) {
                return target;
            }
        }

        // Hunt mode: checkerboard pattern (ships must occupy checkerboard cells)
        List<Coordinate> checkerboard = new ArrayList<>();
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                if ((row + col) % 2 == 0) { // Checkerboard pattern
                    Coordinate coord = new Coordinate(row, col);
                    if (isValidShot(board, coord)) {
                        checkerboard.add(coord);
                    }
                }
            }
        }

        if (!checkerboard.isEmpty()) {
            return checkerboard.get(random.nextInt(checkerboard.size()));
        }

        // Fallback: any valid shot
        return new RandomStrategy().getNextShot(board);
    }

    /**
     * Checks if a shot is valid (not already fired at).
     *
     * @param board The game board
     * @param coord The coordinate to check
     * @return true if valid shot, false otherwise
     */
    private boolean isValidShot(Board board, Coordinate coord) {
        Board.CellState state = board.getGrid().get(coord);
        return state == Board.CellState.WATER || state == Board.CellState.SHIP;
    }

    /**
     * Adds adjacent cells to target queue when a hit occurs.
     *
     * @param coord The coordinate that was hit
     */
    public void registerHit(Coordinate coord) {
        targetQueue.addAll(getAdjacentCells(coord));
    }

    /**
     * Gets the four adjacent cells (North, South, East, West).
     *
     * @param coord The center coordinate
     * @return List of adjacent coordinates within bounds
     */
    private List<Coordinate> getAdjacentCells(Coordinate coord) {
        List<Coordinate> adjacent = new ArrayList<>();
        int row = coord.getRow();
        int col = coord.getCol();

        if (row > 0) adjacent.add(new Coordinate(row - 1, col));
        if (row < 9) adjacent.add(new Coordinate(row + 1, col));
        if (col > 0) adjacent.add(new Coordinate(row, col - 1));
        if (col < 9) adjacent.add(new Coordinate(row, col + 1));

        return adjacent;
    }
}