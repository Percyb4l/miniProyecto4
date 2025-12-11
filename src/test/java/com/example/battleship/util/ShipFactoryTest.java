package com.example.battleship.util;

import com.example.battleship.model.Ship;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ShipFactory class.
 * Tests factory pattern implementation for ship creation.
 *
 * @author Battleship Team
 * @version 1.0
 * @since 2025-12-10
 */
class ShipFactoryTest {

    @Test
    @DisplayName("Should create Carrier with size 4")
    void testCreateCarrier() {
        Ship carrier = ShipFactory.createShip("Carrier");

        assertNotNull(carrier, "Carrier should not be null");
        assertEquals("Carrier", carrier.getType(), "Ship type should be Carrier");
        assertEquals(4, carrier.getSize(), "Carrier size should be 4");
    }

    @Test
    @DisplayName("Should create Submarine with size 3")
    void testCreateSubmarine() {
        Ship submarine = ShipFactory.createShip("Submarine");

        assertNotNull(submarine, "Submarine should not be null");
        assertEquals("Submarine", submarine.getType(), "Ship type should be Submarine");
        assertEquals(3, submarine.getSize(), "Submarine size should be 3");
    }

    @Test
    @DisplayName("Should create Destroyer with size 2")
    void testCreateDestroyer() {
        Ship destroyer = ShipFactory.createShip("Destroyer");

        assertNotNull(destroyer, "Destroyer should not be null");
        assertEquals("Destroyer", destroyer.getType(), "Ship type should be Destroyer");
        assertEquals(2, destroyer.getSize(), "Destroyer size should be 2");
    }

    @Test
    @DisplayName("Should create Frigate with size 1")
    void testCreateFrigate() {
        Ship frigate = ShipFactory.createShip("Frigate");

        assertNotNull(frigate, "Frigate should not be null");
        assertEquals("Frigate", frigate.getType(), "Ship type should be Frigate");
        assertEquals(1, frigate.getSize(), "Frigate size should be 1");
    }

    @Test
    @DisplayName("Should be case insensitive for ship type")
    void testCaseInsensitiveCarrier() {
        Ship carrier1 = ShipFactory.createShip("carrier");
        Ship carrier2 = ShipFactory.createShip("CARRIER");
        Ship carrier3 = ShipFactory.createShip("CaRrIeR");

        assertEquals("Carrier", carrier1.getType(), "Lowercase should create Carrier");
        assertEquals("Carrier", carrier2.getType(), "Uppercase should create Carrier");
        assertEquals("Carrier", carrier3.getType(), "Mixed case should create Carrier");

        assertEquals(4, carrier1.getSize(), "All carriers should have size 4");
        assertEquals(4, carrier2.getSize(), "All carriers should have size 4");
        assertEquals(4, carrier3.getSize(), "All carriers should have size 4");
    }

    @Test
    @DisplayName("Should be case insensitive for all ship types")
    void testCaseInsensitiveAllTypes() {
        Ship submarine = ShipFactory.createShip("submarine");
        Ship destroyer = ShipFactory.createShip("DESTROYER");
        Ship frigate = ShipFactory.createShip("FrIgAtE");

        assertEquals("Submarine", submarine.getType(), "Lowercase submarine should work");
        assertEquals("Destroyer", destroyer.getType(), "Uppercase destroyer should work");
        assertEquals("Frigate", frigate.getType(), "Mixed case frigate should work");
    }

    @Test
    @DisplayName("Should throw exception for unknown ship type")
    void testUnknownShipType() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ShipFactory.createShip("Battleship"),
                "Should throw exception for unknown type"
        );

        assertEquals("Unknown ship type", exception.getMessage(),
                "Exception message should indicate unknown ship type");
    }

    @Test
    @DisplayName("Should throw exception for empty string")
    void testEmptyString() {
        assertThrows(
                IllegalArgumentException.class,
                () -> ShipFactory.createShip(""),
                "Should throw exception for empty string"
        );
    }

    @Test
    @DisplayName("Should throw exception for null")
    void testNullType() {
        assertThrows(
                NullPointerException.class,
                () -> ShipFactory.createShip(null),
                "Should throw exception for null"
        );
    }

    @Test
    @DisplayName("Created ships should not be sunk initially")
    void testNewShipsNotSunk() {
        Ship carrier = ShipFactory.createShip("Carrier");
        Ship submarine = ShipFactory.createShip("Submarine");
        Ship destroyer = ShipFactory.createShip("Destroyer");
        Ship frigate = ShipFactory.createShip("Frigate");

        assertFalse(carrier.isSunk(), "New carrier should not be sunk");
        assertFalse(submarine.isSunk(), "New submarine should not be sunk");
        assertFalse(destroyer.isSunk(), "New destroyer should not be sunk");
        assertFalse(frigate.isSunk(), "New frigate should not be sunk");
    }

    @Test
    @DisplayName("Created ships should have empty coordinates")
    void testNewShipsEmptyCoordinates() {
        Ship carrier = ShipFactory.createShip("Carrier");

        assertTrue(carrier.getCoordinates().isEmpty(),
                "New ship should have no coordinates");
        assertEquals(0, carrier.getCoordinates().size(),
                "Coordinate list should be empty");
    }

    @Test
    @DisplayName("Multiple ships of same type should be independent")
    void testIndependentShips() {
        Ship carrier1 = ShipFactory.createShip("Carrier");
        Ship carrier2 = ShipFactory.createShip("Carrier");

        assertNotSame(carrier1, carrier2, "Should create different instances");

        carrier1.registerHit();
        assertFalse(carrier1.isSunk(), "Carrier 1 should not be sunk after 1 hit");
        assertFalse(carrier2.isSunk(), "Carrier 2 should not be affected");

        assertEquals(4, carrier1.getSize(), "Both carriers should have size 4");
        assertEquals(4, carrier2.getSize(), "Both carriers should have size 4");
    }
}