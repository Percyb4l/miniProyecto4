package com.example.battleship.patterns;

import com.example.battleship.model.Board;
import com.example.battleship.model.Coordinate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Random shooting strategy - Easy difficulty.
 * Shoots at random valid positions without any tactical logic.
 *
 * @author Battleship Team
 * @version 1.0
 */
public class RandomStrategy implements ShootingStrategy {

    private Random random = new Random();

    /**
     * Gets next shot using pure random selection.
     *
     * @param board The target board
     * @return A random valid coordinate
     */
    @Override
    public Coordinate getNextShot(Board board) {
        List<Coordinate> validTargets = new ArrayList<>();

        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                Coordinate coord = new Coordinate(row, col);
                Board.CellState state = board.getGrid().get(coord);

                if (state == Board.CellState.WATER || state == Board.CellState.SHIP) {
                    validTargets.add(coord);
                }
            }
        }

        if (validTargets.isEmpty()) {
            return new Coordinate(0, 0); // Fallback
        }

        return validTargets.get(random.nextInt(validTargets.size()));
    }
}