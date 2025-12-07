package com.example.battleship.exceptions;

/**
 * Exception thrown when a ship cannot be placed at the desired coordinate.
 */
public class InvalidShipPlacementException extends Exception {
    public InvalidShipPlacementException(String message) {
        super(message);
    }
}