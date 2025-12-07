package com.example.battleship.util;

import com.example.battleship.model.Ship;

public class ShipFactory {

    public static Ship createShip(String type) {
        switch (type.toUpperCase()) {
            case "CARRIER": return new Ship("Carrier", 4);
            case "SUBMARINE": return new Ship("Submarine", 3);
            case "DESTROYER": return new Ship("Destroyer", 2);
            case "FRIGATE": return new Ship("Frigate", 1);
            default: throw new IllegalArgumentException("Unknown ship type");
        }
    }
}