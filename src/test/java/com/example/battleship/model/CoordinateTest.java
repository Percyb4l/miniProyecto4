package com.example.battleship.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Coordinate class.
 * Tests equality, hashing, and basic getters.
 *
 * @author Battleship Team
 * @version 1.0
 * @since 2025-12-10
 */
class CoordinateTest {

    private Coordinate coord1;
    private Coordinate coord2;
    private Coordinate coord3;
    private Coordinate coord4;

    @BeforeEach
    void setUp() {
        coord1 = new Coordinate(5, 7);
        coord2 = new Coordinate(5, 7);
        coord3 = new Coordinate(3, 4);
        coord4 = new Coordinate(5, 8);
    }

    @Test
    @DisplayName("Should return correct row value")
    void testGetRow() {
        assertEquals(5, coord1.getRow(), "Row should be 5");
        assertEquals(3, coord3.getRow(), "Row should be 3");
    }

    @Test
    @DisplayName("Should return correct column value")
    void testGetCol() {
        assertEquals(7, coord1.getCol(), "Column should be 7");
        assertEquals(4, coord3.getCol(), "Column should be 4");
    }

    @Test
    @DisplayName("Two coordinates with same values should be equal")
    void testEqualsTrue() {
        assertTrue(coord1.equals(coord2), "Coordinates with same values should be equal");
    }

    @Test
    @DisplayName("Two coordinates with different values should not be equal")
    void testEqualsFalse() {
        assertFalse(coord1.equals(coord3), "Coordinates with different values should not be equal");
        assertFalse(coord1.equals(coord4), "Coordinates with different columns should not be equal");
    }

    @Test
    @DisplayName("Coordinate should equal itself")
    void testEqualsSelf() {
        assertTrue(coord1.equals(coord1), "Coordinate should equal itself");
    }

    @Test
    @DisplayName("Coordinate should not equal null")
    void testEqualsNull() {
        assertFalse(coord1.equals(null), "Coordinate should not equal null");
    }

    @Test
    @DisplayName("Coordinate should not equal object of different type")
    void testEqualsDifferentType() {
        assertFalse(coord1.equals("Not a coordinate"), "Coordinate should not equal string");
    }

    @Test
    @DisplayName("Equal coordinates should have same hash code")
    void testHashCodeEqual() {
        assertEquals(coord1.hashCode(), coord2.hashCode(),
                "Equal coordinates should have same hash code");
    }

    @Test
    @DisplayName("Different coordinates should likely have different hash codes")
    void testHashCodeDifferent() {
        assertNotEquals(coord1.hashCode(), coord3.hashCode(),
                "Different coordinates should likely have different hash codes");
    }

    @Test
    @DisplayName("Hash code should be consistent across multiple calls")
    void testHashCodeConsistent() {
        int hash1 = coord1.hashCode();
        int hash2 = coord1.hashCode();
        assertEquals(hash1, hash2, "Hash code should be consistent");
    }

    @Test
    @DisplayName("Should create coordinate at origin (0,0)")
    void testOriginCoordinate() {
        Coordinate origin = new Coordinate(0, 0);
        assertEquals(0, origin.getRow(), "Origin row should be 0");
        assertEquals(0, origin.getCol(), "Origin column should be 0");
    }

    @Test
    @DisplayName("Should create coordinate at maximum board position (9,9)")
    void testMaxCoordinate() {
        Coordinate max = new Coordinate(9, 9);
        assertEquals(9, max.getRow(), "Max row should be 9");
        assertEquals(9, max.getCol(), "Max column should be 9");
    }

    @Test
    @DisplayName("Should handle negative coordinates")
    void testNegativeCoordinates() {
        Coordinate negative = new Coordinate(-1, -1);
        assertEquals(-1, negative.getRow(), "Negative row should be -1");
        assertEquals(-1, negative.getCol(), "Negative column should be -1");
    }

    @Test
    @DisplayName("Should handle large coordinate values")
    void testLargeCoordinates() {
        Coordinate large = new Coordinate(100, 200);
        assertEquals(100, large.getRow(), "Large row should be 100");
        assertEquals(200, large.getCol(), "Large column should be 200");
    }
}