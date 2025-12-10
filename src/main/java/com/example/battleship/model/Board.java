package com.example.battleship.model;

import com.example.battleship.exceptions.InvalidShipPlacementException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Board implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum CellState { WATER, SHIP, HIT, MISS, SUNK }

    private Map<Coordinate, CellState> grid;
    private Map<Coordinate, Ship> shipPlacement; // Para saber a qué barco le dimos

    public Board() {
        grid = new HashMap<>();
        shipPlacement = new HashMap<>();
        initializeBoard();
    }

    private void initializeBoard() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                grid.put(new Coordinate(i, j), CellState.WATER);
            }
        }
    }

    /**
     * HU-1: Valida y coloca un barco en el tablero.
     */
    public void placeShip(Ship ship, Coordinate start, boolean isHorizontal) throws InvalidShipPlacementException {
        int size = ship.getSize(); // Asumimos que Ship tiene getSize()
        int row = start.getRow();
        int col = start.getCol();

        // 1. Validar límites del tablero
        if (isHorizontal) {
            if (col + size > 10) throw new InvalidShipPlacementException("Ship goes out of bounds (Horizontal)");
        } else {
            if (row + size > 10) throw new InvalidShipPlacementException("Ship goes out of bounds (Vertical)");
        }

        // 2. Validar superposición (Overlapping)
        for (int i = 0; i < size; i++) {
            Coordinate checkCoord;
            if (isHorizontal) checkCoord = new Coordinate(row, col + i);
            else              checkCoord = new Coordinate(row + i, col);

            if (grid.getOrDefault(checkCoord, CellState.WATER) == CellState.SHIP) {
                throw new InvalidShipPlacementException("Position occupied by another ship");
            }
        }

        // 3. Si todo es válido, colocar el barco
        for (int i = 0; i < size; i++) {
            Coordinate newCoord;
            if (isHorizontal) newCoord = new Coordinate(row, col + i);
            else              newCoord = new Coordinate(row + i, col);

            grid.put(newCoord, CellState.SHIP);
            shipPlacement.put(newCoord, ship);
            ship.addCoordinate(newCoord); // Agregar coord al objeto barco
        }
    }
    public Object[] getShipRenderInfo(Coordinate coord) {
        Ship ship = shipPlacement.get(coord);
        if (ship == null) return null;

        String type = ship.getType();
        int position = ship.getCoordinates().indexOf(coord);
        int size = ship.getSize();
        boolean isHorizontal = isShipHorizontal(ship);

        return new Object[]{type, position, size, isHorizontal};
    }

    private boolean isShipHorizontal(Ship ship) {
        if (ship.getCoordinates().size() < 2) return true;

        Coordinate first = ship.getCoordinates().get(0);
        Coordinate second = ship.getCoordinates().get(1);

        return first.getRow() == second.getRow();
    }

    public Map<Coordinate, CellState> getGrid() { return grid; }
    public Map<Coordinate, Ship> getShipPlacement() { return shipPlacement; }
}