package com.example.battleship.patterns;

import com.example.battleship.model.Board;
import com.example.battleship.model.Coordinate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Hunt-and-target strategy - Hard difficulty.
 * Uses checkerboard pattern for hunting, then AGGRESSIVELY targets adjacent cells after hits.
 * This strategy will pursue ships until they are sunk.
 *
 * @author Battleship Team
 * @version 2.0 - IMPROVED
 */
public class HuntTargetStrategy implements ShootingStrategy {

    private Random random = new Random();
    private List<Coordinate> targetQueue = new ArrayList<>();
    private Coordinate lastHit = null;
    private List<Coordinate> hitHistory = new ArrayList<>();

    /**
     * Gets next shot using improved hunt-target algorithm.
     * In target mode, pursues adjacent cells aggressively.
     *
     * @param board The target board
     * @return A strategically optimal coordinate
     */
    @Override
    public Coordinate getNextShot(Board board) {
        // TARGETING MODE: Pursue known hits aggressively
        if (!targetQueue.isEmpty()) {
            // Try each target in queue until finding a valid one
            while (!targetQueue.isEmpty()) {
                Coordinate target = targetQueue.remove(0);
                if (isValidShot(board, target)) {
                    System.out.println("🎯 AI TARGETING: " + target.getRow() + "," + target.getCol());
                    return target;
                }
            }
        }

        // After exhausting target queue, check for any hits that need follow-up
        updateTargetsFromHits(board);
        if (!targetQueue.isEmpty()) {
            Coordinate target = targetQueue.remove(0);
            if (isValidShot(board, target)) {
                System.out.println("🎯 AI RE-TARGETING: " + target.getRow() + "," + target.getCol());
                return target;
            }
        }

        // HUNT MODE: Use checkerboard pattern for efficiency
        List<Coordinate> checkerboard = new ArrayList<>();
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                if ((row + col) % 2 == 0) {
                    Coordinate coord = new Coordinate(row, col);
                    if (isValidShot(board, coord)) {
                        checkerboard.add(coord);
                    }
                }
            }
        }

        if (!checkerboard.isEmpty()) {
            Coordinate shot = checkerboard.get(random.nextInt(checkerboard.size()));
            System.out.println("🔍 AI HUNTING (checkerboard): " + shot.getRow() + "," + shot.getCol());
            return shot;
        }

        // Fallback: any valid shot
        System.out.println("🎲 AI RANDOM (fallback)");
        return new RandomStrategy().getNextShot(board);
    }

    /**
     * Checks if a shot is valid (not already fired at).
     */
    private boolean isValidShot(Board board, Coordinate coord) {
        if (coord.getRow() < 0 || coord.getRow() >= 10 ||
                coord.getCol() < 0 || coord.getCol() >= 10) {
            return false;
        }

        Board.CellState state = board.getGrid().get(coord);
        return state == Board.CellState.WATER || state == Board.CellState.SHIP;
    }

    /**
     * Registers a hit and adds adjacent cells to target queue.
     * This is called externally when AI scores a hit.
     */
    public void registerHit(Coordinate coord) {
        System.out.println("✅ AI HIT REGISTERED at " + coord.getRow() + "," + coord.getCol());
        lastHit = coord;
        hitHistory.add(coord);

        // Add all adjacent cells to target queue
        List<Coordinate> adjacent = getAdjacentCells(coord);
        for (Coordinate adj : adjacent) {
            if (!targetQueue.contains(adj)) {
                targetQueue.add(adj);
                System.out.println("   📍 Added to target queue: " + adj.getRow() + "," + adj.getCol());
            }
        }
    }

    /**
     * Updates target queue based on all previous hits.
     * Scans hit history for any hits that might need follow-up.
     */
    private void updateTargetsFromHits(Board board) {
        for (Coordinate hit : hitHistory) {
            Board.CellState state = board.getGrid().get(hit);

            // If this hit is not yet sunk, add adjacent cells
            if (state == Board.CellState.HIT) {
                List<Coordinate> adjacent = getAdjacentCells(hit);
                for (Coordinate adj : adjacent) {
                    if (isValidShot(board, adj) && !targetQueue.contains(adj)) {
                        targetQueue.add(adj);
                    }
                }
            }
        }
    }

    /**
     * Clears a ship from hit history once it's sunk.
     * This prevents the AI from wasting shots around sunk ships.
     */
    public void registerSunk(List<Coordinate> shipCoords) {
        System.out.println("💀 AI: Enemy ship SUNK, removing from pursuit");
        hitHistory.removeAll(shipCoords);

        // Remove adjacent cells of sunk ship from target queue
        for (Coordinate coord : shipCoords) {
            List<Coordinate> adjacent = getAdjacentCells(coord);
            targetQueue.removeAll(adjacent);
        }
    }

    /**
     * Gets the four adjacent cells (North, South, East, West).
     * Diagonal cells are NOT included (ships don't touch diagonally).
     */
    private List<Coordinate> getAdjacentCells(Coordinate coord) {
        List<Coordinate> adjacent = new ArrayList<>();
        int row = coord.getRow();
        int col = coord.getCol();

        // North
        if (row > 0) adjacent.add(new Coordinate(row - 1, col));
        // South
        if (row < 9) adjacent.add(new Coordinate(row + 1, col));
        // West
        if (col > 0) adjacent.add(new Coordinate(row, col - 1));
        // East
        if (col < 9) adjacent.add(new Coordinate(row, col + 1));

        return adjacent;
    }

    /**
     * Resets the strategy state (for new games).
     */
    public void reset() {
        targetQueue.clear();
        hitHistory.clear();
        lastHit = null;
    }
}